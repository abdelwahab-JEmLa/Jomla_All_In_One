package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_VentsHistoriquesDataBase_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_VentsHistoriquesDataBase>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun upsert_01_PeriodesVentEtReturnItVid(
        period: _01_VentsHistoriquesDataBase,
        onSuccess: (Long) -> Unit = {}
    )

    fun notifierDataChange()

    fun addTestVals()
    fun getCurrentDateString(): String
    fun getCurrentTimeString(): String

    companion object {
        fun getCurrentTimeString(): String {
            return _01_VentsHistoriquesDataBase_RepositoryImpl().getCurrentTimeString()
        }

        fun getCurrentDateString(): String {
            return _01_VentsHistoriquesDataBase_RepositoryImpl().getCurrentDateString()
        }
    }
}
