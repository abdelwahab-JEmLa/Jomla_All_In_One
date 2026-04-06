package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Buttons.View.View

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
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
    val produitById = repositorysMainGetter.repo1ProduitInfos.datasValue.associateBy { it.keyID }
    val categorieById =
        repositorysMainGetter.repoM16CategorieProduit.datasValue.associateBy { it.id }
    val catalogueById = get_ListM21CataloguesCategorie().associateBy { it.id }

    fun catalogueKeyOf(color: M3CouleurProduitInfos): String =
        produitById[color.parentBProduitInfosKeyID]
            ?.let { categorieById[it.idParentCategorie] }
            ?.let { catalogueById[it.catalogueParentId] }
            ?.keyID ?: ""

    val allColors = repositorysMainGetter.repo3CouleurProduit.datasValue
    val colorsToMove = allColors.filter { !it.hasBackupImage(catalogueKeyOf(it)) }
    if (colorsToMove.isEmpty()) {
        onProgress(1f); return
    }

    CoroutineScope(Dispatchers.IO).launch {
        val total = colorsToMove.size.toFloat()
        var done = 0

        colorsToMove.forEach { color ->
            try {
                M3CouleurProduitInfos.ref_Non_Active_Datas.child(color.keyID)
                    .setValue(color.toFirebaseMap()).await()
                M3CouleurProduitInfos.ref.child(color.keyID).removeValue().await()
            } catch (_: Exception) {
            }
            withContext(Dispatchers.Main) { onProgress(++done / total) }
        }

        val activeProductIds = allColors
            .filter { it.hasBackupImage(catalogueKeyOf(it)) }
            .map { it.parentBProduitInfosKeyID }.toSet()

        val productsToMove = repositorysMainGetter.repo1ProduitInfos.datasValue.filter { product ->
            colorsToMove.any { it.parentBProduitInfosKeyID == product.keyID } &&
                    product.keyID !in activeProductIds
        }
        val movedProductIds = productsToMove.map { it.keyID }.toSet()

        productsToMove.forEach { product ->
            try {
                M01Produit.ref_Non_Active_Datas.child(product.keyFireBase)
                    .setValue(product.toFirebaseMap()).await()
                M01Produit.ref.child(product.keyFireBase).removeValue().await()
            } catch (_: Exception) {
            }
        }

        repositorysMainGetter.repo13TarificationInfos.datasValue
            .filter { it.parent_M1Produit_KeyId in movedProductIds }
            .forEach { tariff ->
                try {
                    M13TarificationInfos.ref_NonActiveDatas.child(tariff.keyID)
                        .setValue(tariff.toFirebaseMap()).await()
                    M13TarificationInfos.ref.child(tariff.keyID).removeValue().await()
                } catch (_: Exception) {
                }
            }

        withContext(Dispatchers.Main) { onProgress(1f) }
    }
}
