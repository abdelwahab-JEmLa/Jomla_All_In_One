package Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_2_ProduitAcheteOperation_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_2_ProduitAcheteOperation>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    suspend fun add(produitAcheteOperation: _1_2_ProduitAcheteOperation): Long

    fun updateUnSeulData(data: _1_2_ProduitAcheteOperation)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_2_ProduitAcheteOperation>)
    fun deleteUnSeulData(data: _1_2_ProduitAcheteOperation)

    companion object {
        const val TAG = "_1_2_ProduitAcheteOperation"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_2_")
    }

    fun addDataAndReturneItVID(data: _1_2_ProduitAcheteOperation,
                               onAddSuccess: (Long) -> Unit={}
    )

    val repositoryScope: CoroutineScope
}
