package V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ZAppCompt_RepositoryComposable

fun upsertVentCouleurOperation(
    fCouleurVentOperation: FCouleurVentOperationInfos? = null,
    produit: ArticlesBasesStatsTable,
    colorIndex: Int,
    quantity: Int,
    zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    getter: AGetter,
) {
    val relatedCouleur = getter.getRelatedCouleur(produit, colorIndex)
    val zCompt = zAppComptRepositoryComposable.currentAppCompt

    fCouleurVentOperation?.let { existingOperation ->
        val updatedOperation = existingOperation.copy(quantityAchete = quantity)
        getter.fVentCouleurOperationRepository.addOrUpdateData(updatedOperation)
    } ?: zCompt?.let {
        getter.fVentCouleurOperationRepository.acheterUneCouleur(
            it, relatedCouleur, quantity
        )
    }
}
