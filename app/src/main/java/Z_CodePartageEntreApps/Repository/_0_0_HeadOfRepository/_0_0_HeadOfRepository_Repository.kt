package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepository

import _0_0_HeadOfRepository
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _0_0_HeadOfRepositorys_Repository {
    var modelDatasSnapList: SnapshotStateList<_0_0_HeadOfRepository>

    val progressRepo: MutableStateFlow<Float>
        get() {
            // This will be implemented in the implementation class to calculate
            // the combined progress from all repositories
            return MutableStateFlow(0f)
        }

    suspend fun ensureDataIsInitialized()

    companion object {
        const val TAG = "_0_0_HeadOfRepositorys"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
    }
}
