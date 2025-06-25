package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.FCouleurVentOperation
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    fun getRelatedFAchatCouleurOperation(produitID: Long, index:Int): FCouleurVentOperation? {
      return  aCentralDatasHandlerProtoJuin9.getRelatedFAchatCouleurOperation(produitID,index)
    }

    fun acheter(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val data = aCentralDatasHandlerProtoJuin9
            .zAppComptRepositoryComposable
            .ouvrireProduitEtCouleurVent(produit, colorIndex)

        data.let {
            aCentralDatasHandlerProtoJuin9
                .fCouleurAchatOperationRepositoryComposable
                .acheterUneCouleur(it, produit, quantity, colorIndex)

        }
    }

}
