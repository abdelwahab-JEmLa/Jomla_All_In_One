package Z_CodePartageEntreApps.Model.BProto_ClientsDataBaseRepo.Repository

import Z_CodePartageEntreApps.Model.BProto_ClientsDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface BProto_ClientsDataBaseRepository{
    var modelDatas: SnapshotStateList<BProto_ClientsDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun checkConnectivity()

    fun load()

    fun updateData(data: BProto_ClientsDataBase? = null,)
    suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>)

    companion object {
        val caReference = ref_HeadOfModels.child("B_ClientsDataBase")
    }
}



