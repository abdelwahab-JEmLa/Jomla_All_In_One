package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.FAchatOperationCouleurRepositoryComposable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.ZAppCompt_RepositoryComposable

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
