package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface C3_BonAchate_Repository {
    var modelDatasSnapList: SnapshotStateList<C3_BonAchate>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()

    companion object {
        const val TAG = "C3_BonAchate"

        val sonDataBaseRef = C3_BonAchate.caRef

    }

    fun getOuvert_1_3_TransactionCommercial(): C3_BonAchate?
}
