package Z_CodePartageEntreApps.Model.C_GrossistsDataBaseRepository

import Z_CodePartageEntreApps.Model.C_GrossistsDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface C_GrossistsDataBaseRepository {
    var modelDatas: SnapshotStateList<C_GrossistsDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<C_GrossistsDataBase>, Flow<Float>>
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    suspend fun updateDatas(datas: SnapshotStateList<C_GrossistsDataBase>)
    fun updateData(data: C_GrossistsDataBase? = null,)
    fun addData(data: C_GrossistsDataBase) // Added method to fix the error

    companion object {
        val caReference = firebaseDatabase
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("C_GrossistsDataBase")
    }
}
