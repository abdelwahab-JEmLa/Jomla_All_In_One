package Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_VentsHistoriquesDataBase_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_VentsHistoriquesDataBase>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun notifieDataChange()

    fun addTestVals()
}
