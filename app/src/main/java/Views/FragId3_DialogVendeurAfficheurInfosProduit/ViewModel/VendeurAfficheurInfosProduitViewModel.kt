package Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.FCouleurVentOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ASetterCentral
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.lifecycle.ViewModel

class VendeurAfficheurInfosProduitViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
    val aSetterCentralProto26: ASetterCentral,
) : ViewModel() {
    fun getRelatedFAchatCouleurOperation(produitID: Long, index:Int): FCouleurVentOperation? {
      return  aCentralDatasHandlerProtoJuin9.getRelatedFAchatCouleurOperation(produitID,index)
    }

    fun acheter(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        aSetterCentralProto26.acheterACaSetterCentral(produit,colorIndex,quantity)
    }
}
