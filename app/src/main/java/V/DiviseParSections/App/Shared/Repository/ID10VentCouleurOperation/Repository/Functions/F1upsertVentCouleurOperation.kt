package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt

fun upsertVentCouleurOperation(
    fCouleurVentOperation: M10OperationVentCouleur? = null,
    produit: M01Produit,
    colorIndex: Int,
    quantity: Int,
    zAppComptRepositoryComposable: Repo9AppCompt,
    getter: RepositorysMainGetter,
) {
    val relatedCouleur = getter.getRelatedCouleur(produit, colorIndex)
    val zCompt = zAppComptRepositoryComposable.currentAppCompt

    fCouleurVentOperation?.let { existingOperation ->
        val updatedOperation = existingOperation.copy(quantity = quantity)
        getter.repo10OperationVentCouleur.addOrUpdateData(updatedOperation)
    } ?: zCompt?.let {
        getter.repo10OperationVentCouleur.acheterUneCouleur(
            it, relatedCouleur, quantity
        )
    }
}
