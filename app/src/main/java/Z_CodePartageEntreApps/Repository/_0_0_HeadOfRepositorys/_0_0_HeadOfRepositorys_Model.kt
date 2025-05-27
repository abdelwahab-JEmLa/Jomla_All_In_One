package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

class _0_0_HeadOfRepositorys_Model(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val repositoryC2_ProduitAcheteOperation: _1_2_ProduitAcheteOperation_Repository,

    val repository_1_3_TransactionCommercial: C3_BonAchate_Repository,
    var activeVId_1_3_TransactionCommercial: MutableStateFlow<Long>,

    val repository_1_4_PeriodeVent: _1_4_PeriodeVent_Repository,

    val repository_1_5_Vendeur: _1_5_Vendeur_Repository,

    val _2_1_ProduitsDataBase_Repository: _2_1_ProduitsDataBase_Repository,
    val repository_3_ClientsDataBase: _3_ClientsDataBase_Repository,

    val _4_CouleurOperationCommand_Repository: _4_CouleurOperationCommand_Repository,

    val e1SecteurDeClientsRepository: E1SecteurDeClientsRepository,

    var activeIdDe_1_5_Vendeur: Long = 1L,
) {
    companion object {
        fun getHeadSqlDataBaseRef(itsProductionMode:Boolean = true): DatabaseReference {
            val _01_HeadRef = Firebase.database.getReference("00_DataPrototype-04-02")

            val _1_developingRef = _01_HeadRef.child("_1_developingRef")
            val _2_productionTestRef = _01_HeadRef.child("_2_productionTestRef")

            return if (itsProductionMode) _2_productionTestRef else _1_developingRef
        }
    }
}
