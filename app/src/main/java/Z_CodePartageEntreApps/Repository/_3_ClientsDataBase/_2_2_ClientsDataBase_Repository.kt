package Z_CodePartageEntreApps.Repository._3_ClientsDataBase

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.firebaseDatabase
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models._3_ClientsDataBase
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

        val sonDataBaseRef = firebaseDatabase
            .getReference("B_ClientsDataBase")
    }

}
