package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository


class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
    val fCouleurAchatOperationRepositoryComposable: FAchatOperationCouleurRepositoryComposable,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    fun getRelatedCouleur(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        getter.getRelatedCouleur(produit, colorIndex)

    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperation? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val relatedCouleur = getRelatedCouleur(produit, colorIndex)
        val data = zAppComptRepositoryComposable.ouvrireProduitEtCouleurVent(
            produit,
            relatedCouleur = relatedCouleur,
        )

        fCouleurVentOperation?.let { existingOperation ->
            if (existingOperation.quantityAchete != quantity) {
                val updatedOperation = existingOperation.copy(
                    quantityAchete = quantity,
                )
                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedOperation)
            } else {
                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(existingOperation)
            }
        } ?: run {
            fCouleurAchatOperationRepositoryComposable.acheterUneCouleur(
                ouvertData = data,
                relatedCouleur = relatedCouleur,
                quantity = quantity
            )
        }
    }
}
