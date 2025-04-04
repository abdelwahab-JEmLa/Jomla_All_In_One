package Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation

import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_1_CouleurAcheteOperationRepository {
    var modelDatasSnapList: SnapshotStateList<_1_1_CouleurAcheteOperation>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    // Added ensureDataIsInitialized function to the interface
    suspend fun ensureDataIsInitialized()

    fun addData(data: _1_1_CouleurAcheteOperation)
    fun updateUnSeulData(data: _1_1_CouleurAcheteOperation)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_1_CouleurAcheteOperation>)
    fun deleteUnSeulData(data: _1_1_CouleurAcheteOperation)

    companion object {
        const val TAG = "_1_1_CouleurAcheteOperation"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_1_")
    }
}
