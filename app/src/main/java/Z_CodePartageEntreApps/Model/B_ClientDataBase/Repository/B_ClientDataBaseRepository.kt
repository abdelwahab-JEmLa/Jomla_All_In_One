package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBaseProtoJuin3
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface B_ClientDataBaseRepository {
    var modelDatas: SnapshotStateList<B_ClientDataBaseProtoJuin3>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addData(data: B_ClientDataBaseProtoJuin3)
    fun updateUnSeulData(data: B_ClientDataBaseProtoJuin3? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<B_ClientDataBaseProtoJuin3>)

    companion object {
        val caReference = firebaseDatabase.getReference("B_ClientsDataBase")
    }

    fun deleteUnSeulData(data: B_ClientDataBaseProtoJuin3)
}
