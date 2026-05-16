package Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components

import Application4.App.Fragment.ID1.Fragment.ViewModel.ActiveDatasFragNewProto
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

object FlowsFunctions_ActiveDatasFragNewProto {

    fun getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
        dao_M9AppCompt: Dao_M9AppCompt,
        activeDatasFragNewProto: ActiveDatasFragNewProto,
    ): Flow<M09AppCompt?> =
        dao_M9AppCompt.getFlow_ByKeyID(
            M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
        ).onEach { activeDatasFragNewProto.active_M9Compt = it }

    fun getFlow_list_M1Produit(
        dao_M1Produit: Dao_M1Produit,
        activeDatasFragNewProto: ActiveDatasFragNewProto,
    ): Flow<List<M01Produit>> =
        dao_M1Produit.getAllFlow().onEach { activeDatasFragNewProto.list_M1Produit = it }

}
