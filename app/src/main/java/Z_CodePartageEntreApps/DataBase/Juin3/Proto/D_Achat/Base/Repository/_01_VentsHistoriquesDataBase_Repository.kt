package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._013_ClientTransaction
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_Achat.Base.Models._01_PeriodVentHistorique
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_VentsHistoriquesDataBase_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_PeriodVentHistorique>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun upsert_01_PeriodesVentEtReturnItVid(
        period: _01_PeriodVentHistorique,
        onSuccess: (Long) -> Unit = {}
    )

    fun notifierDataChange()

    fun addTestVals()
    fun getClientTransactionsHistoriques(idClient: Long): List<Pair<_01_PeriodVentHistorique, List<_013_ClientTransaction>>>
}
