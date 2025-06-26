package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

class ASetterCentral(
    val b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,
    val fCouleurAchatOperationRepositoryComposable: FAchatOperationCouleurRepositoryComposable,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    fun getRelatedCouleur(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int
    ) =
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find {
                it.parentBProduitOldID == produit.id
                        && it.indexCouleurDansAncienProto == colorIndex
            }!!

    fun acheterACaSetterCentral(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val data = zAppComptRepositoryComposable
            .ouvrireProduitEtCouleurVent(
                produit,
                relatedCouleur = getRelatedCouleur(
                    produit,
                    colorIndex
                ),
            )

        data.let {
            fCouleurAchatOperationRepositoryComposable
                .acheterUneCouleur(
                    ouvertData = it,
                    relatedCouleur = getRelatedCouleur(
                        produit,
                        colorIndex
                    ),
                    quantity
                )
        }
    }
}
