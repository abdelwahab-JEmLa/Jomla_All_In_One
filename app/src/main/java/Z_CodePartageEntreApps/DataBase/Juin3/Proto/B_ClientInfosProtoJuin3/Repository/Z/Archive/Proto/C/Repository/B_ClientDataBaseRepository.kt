package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface B_ClientDataBaseRepository {
    var modelDatas: SnapshotStateList<B_ClientDataBaseProtoC>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addData(data: B_ClientDataBaseProtoC)
    fun updateUnSeulData(data: B_ClientDataBaseProtoC? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<B_ClientDataBaseProtoC>)

    companion object {
        val caReference = firebaseDatabase.getReference("B_ClientsDataBaseProtoD")
    }

    fun deleteUnSeulData(data: B_ClientDataBaseProtoC)
}
