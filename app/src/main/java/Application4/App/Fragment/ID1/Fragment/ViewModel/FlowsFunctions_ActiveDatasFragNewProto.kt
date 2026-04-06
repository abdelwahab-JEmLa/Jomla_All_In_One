package Application4.App.Fragment.ID1.Fragment.ViewModel

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

object FlowsFunctions_ActiveDatasFragNewProto {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFlow_listM10OperationVentCouleur_By_active_Central_Values(
        dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
        dao_M9AppCompt: Dao_M9AppCompt,
    ): Flow<Pair<String?, List<M10OperationVentCouleur>>> =
        dao_M9AppCompt.getFlow_ByKeyID(
            M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
        ).flatMapLatest { activeCompt ->
            val onVentKey =
                activeCompt?.onVentM8BonVentKey?.takeIf { it.isNotBlank() && it != "null" }
            if (onVentKey == null) flowOf(null to emptyList())
            else dao_M10OperationVentCouleur
                .getFlow_ListM10OperationVentCouleur_Of_Active_M8Bon_Key(onVentKey)
                .map { list -> onVentKey to list.filter { it.parent_M8BonVent_KeyId == onVentKey } }
        }

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
