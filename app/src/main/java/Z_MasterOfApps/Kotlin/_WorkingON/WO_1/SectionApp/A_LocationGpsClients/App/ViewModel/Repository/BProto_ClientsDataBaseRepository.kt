package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface BProto_ClientsDataBaseRepository{
    var modelDatas: SnapshotStateList<BProto_ClientsDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

     fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope)

    fun checkConnectivity()

    fun addData(data: BProto_ClientsDataBase)
    fun updateData(data: BProto_ClientsDataBase? = null,)
    suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>)

    companion object {
        val caReference = firebaseDatabase.getReference("B_ClientsDataBase")
    }

    fun loadDepuitRoom(viewModelScope: CoroutineScope)

}



