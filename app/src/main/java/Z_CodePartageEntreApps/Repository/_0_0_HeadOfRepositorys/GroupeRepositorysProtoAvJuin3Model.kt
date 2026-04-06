package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.E1SecteurDeClients.Repository.E1SecteurDeClientsRepository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent.DataBaseFactoryMVentPeriode
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase_Repository
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand_Repository
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

val startActive = 1L

class GroupeRepositorysProtoAvJuin3Model(
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val repositoryC2_ProduitAcheteOperation: _1_2_ProduitAcheteOperation_Repository,

    val repositoryMVentPeriode: DataBaseFactoryMVentPeriode,


    val _2_1_ProduitsDataBase_Repository: _2_1_ProduitsDataBase_Repository,

    val _4_CouleurOperationCommand_Repository: _4_CouleurOperationCommand_Repository,

    val e1SecteurDeClientsRepository: E1SecteurDeClientsRepository,

    ) {
    companion object {
        fun getHeadSqlDataBaseRef(itsProductionMode:Boolean = false): DatabaseReference {
            val _01_HeadRef = Firebase.database.getReference("00_DataPrototype-04-02")

            val _1_developingRef = _01_HeadRef.child("_1_developingRef")
            val _2_productionTestRef = _01_HeadRef.child("_2_productionTestRef")

            return if (itsProductionMode) _2_productionTestRef else _1_developingRef
        }
    }
}
