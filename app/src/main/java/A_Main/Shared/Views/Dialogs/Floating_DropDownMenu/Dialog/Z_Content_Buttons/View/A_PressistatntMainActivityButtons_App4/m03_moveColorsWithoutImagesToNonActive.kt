package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

private fun M3CouleurProduitInfos.hasBackupImage(catalogueKeyId: String): Boolean =
    File(
        M3CouleurProduitInfos.backup_Images_storageLink,
        "$catalogueKeyId/$nomImageFichieSansEtansion.$extensionDisponible"
    ).exists()

fun moveColorsWithoutImagesToNonActive(
    repositorysMainGetter: RepositorysMainGetter,
    onProgress: (Float) -> Unit = {},
) {
    val produitById    = repositorysMainGetter.repo1ProduitInfos.datasValue.associateBy { it.keyID }
    val categorieById  = repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val catalogueById  = get_ListM21CataloguesCategorie().associateBy { it.id }

    // Returns "" when any link in the color→produit→categorie→catalogue chain is missing.
    fun catalogueKeyOf(color: M3CouleurProduitInfos): String =
        produitById[color.parentBProduitInfosKeyID]
            ?.let { categorieById[it.idParentCategorie] }
            ?.let { catalogueById[it.catalogueParentId] }
            ?.keyID ?: ""

    val allColors  = repositorysMainGetter.repo3CouleurProduit.datasValue
    val allTariffs = repositorysMainGetter.repo13TarificationInfos.datasValue
    val productIdsWithTariff = allTariffs
        .filter { !it.typeChoisi.ignore_affiche && it.prixCurrency > 0 }
        .map { it.parent_M1Produit_KeyId }.toSet()

    val colorsToMove = allColors.filter { color ->
        val catalogueKey = catalogueKeyOf(color)
        // If the catalogue chain is broken the key resolves to "". In that case
        // hasBackupImage("") would always return false (path "/<empty>/$name.ext"
        // never exists), which would wrongly flag the color as image-less and move
        // it to non-active. Skip such colors entirely — they belong to a product
        // whose catalogue/category metadata hasn't loaded or is missing.
        if (catalogueKey.isEmpty()) return@filter false
        !color.hasBackupImage(catalogueKey) ||
                color.parentBProduitInfosKeyID !in productIdsWithTariff
    }.distinctBy { it.keyID }

    if (colorsToMove.isEmpty()) { onProgress(1f); return }

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
        val activeProductIds = allColors
            .filter { it.keyID !in movedColorIds }
            .map { it.parentBProduitInfosKeyID }.toSet()

        val productsToMove = repositorysMainGetter.repo1ProduitInfos.datasValue.filter { product ->
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

        withContext(Dispatchers.Main) { onProgress(1f) }
    }
}
