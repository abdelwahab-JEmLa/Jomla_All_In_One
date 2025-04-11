package Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.firebaseDatabase
import Z_CodePartageEntreApps.Model._2_2_ClientsDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _2_2_ClientsDataBase_Repository{
    var modelDatasSnapList: SnapshotStateList<_2_2_ClientsDataBase>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()


    fun addData(data: _2_2_ClientsDataBase)
    fun updateUnSeulData(data: _2_2_ClientsDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_2_2_ClientsDataBase>)
    fun deleteUnSeulData(data: _2_2_ClientsDataBase)

    companion object {
        const val TAG = "_2_2_ClientsDataBase"

        val sonDataBaseRef = firebaseDatabase
            .getReference("B_ClientsDataBase")
    }

}
