package Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface B_ClientsDataBaseRepository{
    var modelDatas: SnapshotStateList<B_ClientsDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun checkConnectivity()
    fun checkConnectivityAndSync()

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<B_ClientsDataBase>, Flow<Float>>
    fun restartDatabaseListener()
    fun stopDatabaseListener()

    fun updateData(data: B_ClientsDataBase? = null,)
    suspend fun updateDatas(datas: SnapshotStateList<B_ClientsDataBase>)

    companion object {
        val caReference = ref_HeadOfModels.child("B_ClientsDataBase")
        val caReferenceProtoDecembre = ref_HeadOfModels.child("produits")
        val autreRef = firebaseDatabase
            .getReference("1_SQL_PROTO_MARS").child("B_ClientsDataBase")
    }
}



