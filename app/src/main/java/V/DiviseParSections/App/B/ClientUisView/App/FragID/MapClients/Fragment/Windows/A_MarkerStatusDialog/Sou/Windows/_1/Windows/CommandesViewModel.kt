package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentNoSQl
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class for representing UI state
data class PeriodesUiState(
    val a01PeriodesVent: SnapshotStateList<_01_PeriodesVentNoSQl> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class PeriodesViewModel(
    val appDatabase: AppDatabase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeriodesUiState())
    open val uiState: StateFlow<PeriodesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // This is fine now because getCount() is suspend
            val count = appDatabase._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao().getCount()
            if (count == 0) {
                insertTestData()
            }
        }
        collecteConvertSQlToNoSqlDataBase()
    }


}
