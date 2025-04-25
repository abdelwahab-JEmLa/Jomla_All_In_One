package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Repository

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
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
}
