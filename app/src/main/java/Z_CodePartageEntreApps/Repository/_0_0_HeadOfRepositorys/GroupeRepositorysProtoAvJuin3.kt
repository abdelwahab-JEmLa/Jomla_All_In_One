package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import EntreApps.Shared.Models.Z_AppCompt
import kotlinx.coroutines.flow.MutableStateFlow

interface GroupeRepositorysProtoAvJuin3 {
    var repositorys_Model: GroupeRepositorysProtoAvJuin3Model

    val progressRepo: MutableStateFlow<Float>
        get() {
            return MutableStateFlow(0f)
        }

    companion object {
        const val TAG = "GroupeRepositorysProtoAvJuin3"
    }

    fun updateActiveIdDe_1_5_Vendeur(id: Long = -1L)
    fun notifyDataChanged_2_1_ProduitsDataBase_Repository()
    fun upsertUneDataEtReturnVID_1_5_Vendeur(data: Z_AppCompt, onSuccess: (Long) -> Unit = {})




    fun <T> upsertUneDataEtReturnVID(data: T, onSuccess: (Long) -> Unit = {})

    fun <T> deleteData(
        data: T,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
    )
}
