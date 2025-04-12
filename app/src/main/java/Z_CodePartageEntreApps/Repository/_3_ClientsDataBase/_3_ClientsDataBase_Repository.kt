package Z_CodePartageEntreApps.Repository._3_ClientsDataBase

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views.Models._3_ClientsDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _3_ClientsDataBase_Repository{
    var modelDatasSnapList: SnapshotStateList<_3_ClientsDataBase>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()



    fun addData(data: _3_ClientsDataBase)
    fun updateUnSeulData(data: _3_ClientsDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_3_ClientsDataBase>)
    fun deleteUnSeulData(data: _3_ClientsDataBase)

    companion object {
        const val TAG = "_3_ClientsDataBase"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_3_")
            .child("_Main")
    }


    fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_3_ClientsDataBase>,
        onAddSuccess: (List<Long>) -> Unit
    )
}
