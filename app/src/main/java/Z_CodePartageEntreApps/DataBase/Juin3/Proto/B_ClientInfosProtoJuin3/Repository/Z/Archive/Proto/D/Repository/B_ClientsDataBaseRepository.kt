package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface B_ClientsDataBaseRepository{
    var modelDatas: SnapshotStateList<B_ClientsDataBaseProtoD>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun checkConnectivity()
    fun checkConnectivityAndSync()

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<B_ClientsDataBaseProtoD>, Flow<Float>>
    fun restartDatabaseListener()
    fun stopDatabaseListener()

    fun updateUnSeulData(data: B_ClientsDataBaseProtoD? = null,)
    suspend fun updateMultiDatas(datas: SnapshotStateList<B_ClientsDataBaseProtoD>)

    companion object {
        val caReference = ref_HeadOfModels.child("B_ClientsDataBaseProtoD")
    }
}



