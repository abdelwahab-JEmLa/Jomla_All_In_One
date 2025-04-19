package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentNoSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentRoomSQl
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.StateFlow

interface _01_PeriodesVent_Repository {
    var modelDatasSnapList: SnapshotStateList<_01_PeriodesVentNoSQl>

    val progressRepo: StateFlow<Float>
    suspend fun refreshData()
    suspend fun addPeriode(periode: _01_PeriodesVentRoomSQl)
}
