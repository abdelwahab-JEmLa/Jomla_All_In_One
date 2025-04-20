package Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_VentsHistoriquesDataBase_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_VentsHistoriquesDataBase>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun notifieDataChange()

    companion object {
        private val _01_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("01_DataPrototype-04-19")

        val sonDataBaseRef = _01_HeadOfRepositorys_RepositoryRef
            .child("_01_VentsHistoriquesDataBase")
    }
}
