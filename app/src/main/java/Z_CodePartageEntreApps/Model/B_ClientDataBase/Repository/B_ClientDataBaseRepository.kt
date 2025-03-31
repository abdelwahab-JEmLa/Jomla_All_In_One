package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface B_ClientDataBaseRepository {
    var modelDatas: SnapshotStateList<B_ClientDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addData(data: B_ClientDataBase)
    fun updateData(data: B_ClientDataBase? = null)
    suspend fun updateDatas(datas: SnapshotStateList<B_ClientDataBase>)

    companion object {
        val caReference = firebaseDatabase.getReference("B_ClientsDataBase")
    }

    fun deleteUnSeulData(data: B_ClientDataBase)
}
