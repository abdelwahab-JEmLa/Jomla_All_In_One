package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import kotlinx.coroutines.flow.MutableStateFlow

interface _0_0_HeadSQLRepositorys {
    var repositorys_Model: _0_0_HeadOfRepositorys_Model

    val progressRepo: MutableStateFlow<Float>
        get() {
            return MutableStateFlow(0f)
        }

    companion object {
        const val TAG = "_0_0_HeadOfRepositorys"
    }

    fun updateActiveIdDe_1_5_Vendeur(id: Long = -1L)
    fun notifyDataChanged_2_1_ProduitsDataBase_Repository()
    fun notifyDataChanged_1_3_TransactionCommercial_Repository()
    fun upsertUneDataEtReturnVID_1_5_Vendeur(data: _1_5_Vendeur, onSuccess: (Long) -> Unit = {})


    fun upsertUneDataEtReturnVID_1_4_PeriodeVent(
        data: _1_4_PeriodeVent,
        onSuccess: (Long) -> Unit = {},
    )


    fun <T> upsertUneDataEtReturnVID(data: T, onSuccess: (Long) -> Unit = {})

    fun <T> deleteData(
        data: T,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    )
}
