package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG_M3 = "M3Cleanup"

private fun M3CouleurProduitInfos.hasBackupImage(catalogueKeyId: String): Boolean =
    File(
        M3CouleurProduitInfos.backup_Images_storageLink,
        "$catalogueKeyId/$nomImageFichieSansEtansion.$extensionDisponible"
    ).exists()

private fun M3CouleurProduitInfos.isMissingImageInAnyCatalogue(): Boolean {
    val hasName = nomImageFichieSansEtansion.isNotBlank() && nomImageFichieSansEtansion != "Non Dispo"
    if (!hasName) return true
    for (catalogue in get_ListM21CataloguesCategorie()) {
        if (!hasBackupImage(catalogue.keyID)) return true
    }
    return false
}

fun moveColorsWithoutImagesToNonActive(
    repositorysMainGetter: RepositorysMainGetter,
    onProgress: (Float) -> Unit = {},
    onSummary: (String) -> Unit = {},
) {
    val allColors    = repositorysMainGetter.repo3CouleurProduit.datasValue
    val allTariffs   = repositorysMainGetter.repo13TarificationInfos.datasValue
    val allProducts  = repositorysMainGetter.repo1ProduitInfos.datasValue
    val allCategories = repositorysMainGetter.repoM16CategorieProduit.datasValue

    Log.d(TAG_M3, "START — M3=${allColors.size}  M1=${allProducts.size}  M13=${allTariffs.size}  cats=${allCategories.size}")

    fun catalogueKeyOf(color: M3CouleurProduitInfos): String {
        var productKeyId: Long = -1
        for (product in allProducts) {
            if (product.keyID == color.parentBProduitInfosKeyID) {
                productKeyId = product.idParentCategorie
                break
            }
        }
        if (productKeyId == -1L) return ""

        var catalogueParentId: Long = -1
        for (category in allCategories) {
            if (category.id == productKeyId) {
                catalogueParentId = category.catalogueParentId
                break
            }
        }
        if (catalogueParentId == -1L) return ""

        for (catalogue in get_ListM21CataloguesCategorie()) {
            if (catalogue.id == catalogueParentId) return catalogue.keyID
        }
        return ""
    }

    val productIdsWithTariff = mutableSetOf<String>()
    for (tariff in allTariffs) {
        if (!tariff.typeChoisi.ignore_affiche && tariff.prixCurrency > 0) {
            productIdsWithTariff.add(tariff.parent_M1Produit_KeyId)
        }
    }

    val noCatalogue    = allColors.count { catalogueKeyOf(it).isEmpty() }
    val noTariff       = allColors.count { c -> catalogueKeyOf(c).isNotEmpty() && c.parentBProduitInfosKeyID !in productIdsWithTariff }
    val missingImgOnly = allColors.count { c ->
        catalogueKeyOf(c).isNotEmpty() &&
        c.parentBProduitInfosKeyID in productIdsWithTariff &&
        c.isMissingImageInAnyCatalogue()
    }
    Log.d(TAG_M3, "filter breakdown — noCatalogue=$noCatalogue  noTariff=$noTariff  hasTariff+missingBackupImg=$missingImgOnly")
    Log.d(TAG_M3, "badge est. (string-only) ≈ ${noTariff + allColors.count { c -> catalogueKeyOf(c).isNotEmpty() && c.parentBProduitInfosKeyID !in productIdsWithTariff || (c.nomImageFichieSansEtansion.isBlank() || c.nomImageFichieSansEtansion == "Non Dispo") }}")

    val colorsToMove = allColors.filter { color ->
        val catalogueKey = catalogueKeyOf(color)
        //if (catalogueKey.isEmpty()) return@filter false

        val hasTariff = color.parentBProduitInfosKeyID in productIdsWithTariff
        if (!hasTariff) return@filter true

        color.dropBox_key.isNotBlank()
    }.distinctBy { it.keyID }


    Log.d(TAG_M3, "colorsToMove=${colorsToMove.size}  (noTariff=$noTariff + missingImg=$missingImgOnly = ${noTariff + missingImgOnly}, distinctBy deduped=${noTariff + missingImgOnly - colorsToMove.size})")

    if (colorsToMove.isEmpty()) {
        onSummary("Aucun changement — données déjà propres ✓")
        onProgress(1f)
        return
    }

    val beforeM3  = allColors.size
    val beforeM1  = allProducts.size
    val beforeM13 = allTariffs.size

    CoroutineScope(Dispatchers.IO).launch {

        // ── Phase 1 · Colors ─────────────────────────────────────────────────
        try {
            val nonActiveColors: Map<String, Any> =
                colorsToMove.associate { it.keyID to it.toFirebaseMap() }
            M3CouleurProduitInfos.ref_Non_Active_Datas.updateChildren(nonActiveColors).await()

            val nullColors: Map<String, Any?> = colorsToMove.associate { it.keyID to null }
            M3CouleurProduitInfos.ref.updateChildren(nullColors).await()
        } catch (_: Exception) { }

        withContext(Dispatchers.Main) { onProgress(0.4f) }

        // ── Phase 2 · Products that now have zero active colors ───────────────
        val movedColorIds    = colorsToMove.map { it.keyID }.toSet()
        val activeProductIds = mutableSetOf<String>()
        for (color in allColors) {
            if (color.keyID !in movedColorIds) activeProductIds.add(color.parentBProduitInfosKeyID)
        }

        val productsToMove = allProducts.filter { product ->
            colorsToMove.any { it.parentBProduitInfosKeyID == product.keyID } &&
                    product.keyID !in activeProductIds
        }
        val movedProductIds = productsToMove.map { it.keyID }.toSet()

        if (productsToMove.isNotEmpty()) {
            try {
                val nonActiveProducts: Map<String, Any> =
                    productsToMove.associate { it.keyFireBase to it.toFirebaseMap() }
                M01Produit.ref_Non_Active_Datas.updateChildren(nonActiveProducts).await()

                val nullProducts: Map<String, Any?> =
                    productsToMove.associate { it.keyFireBase to null }
                M01Produit.ref.updateChildren(nullProducts).await()
            } catch (_: Exception) { }
        }

        withContext(Dispatchers.Main) { onProgress(0.7f) }

        // ── Phase 3 · Tariffs that belonged to the now-inactive products ──────
        val tariffsToMove = allTariffs.filter { it.parent_M1Produit_KeyId in movedProductIds }

        if (tariffsToMove.isNotEmpty()) {
            try {
                val nonActiveTariffs: Map<String, Any> =
                    tariffsToMove.associate { it.keyID to it.toFirebaseMap() }
                M13TarificationInfos.ref_NonActiveDatas.updateChildren(nonActiveTariffs).await()

                val nullTariffs: Map<String, Any?> = tariffsToMove.associate { it.keyID to null }
                M13TarificationInfos.ref.updateChildren(nullTariffs).await()
            } catch (_: Exception) { }
        }

        val afterM3  = beforeM3  - colorsToMove.size
        val afterM1  = beforeM1  - productsToMove.size
        val afterM13 = beforeM13 - tariffsToMove.size
        val summary = buildString {
            appendLine("Nettoyage terminé ✓")
            append("M3: $beforeM3 → $afterM3  (−${colorsToMove.size})")
            appendLine()
            append("M1: $beforeM1 → $afterM1  (−${productsToMove.size})")
            appendLine()
            append("M13: $beforeM13 → $afterM13  (−${tariffsToMove.size})")
        }

        withContext(Dispatchers.Main) {
            onSummary(summary)
            onProgress(1f)
        }
    }
}
