package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

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
    val periodesVent: SnapshotStateList<PeriodesVent> = mutableStateListOf()
)

// ViewModel for handling commands/orders
open class PeriodesViewModel(
) : ViewModel() {
    private val _uiState = MutableStateFlow(PeriodesUiState())
    open val uiState: StateFlow<PeriodesUiState> = _uiState.asStateFlow()

    companion object {
        // Function to get test data instead of static property
        fun getTestPeriodesVent(): List<PeriodesVent> {
                  //<--
                  //TODO(1): cree un test qui cree un hard data insert au room apre le collecteConvertSQlToNoSqlDataBase
        }
    }

    init {
                collecteConvertSQlToNoSqlDataBase()
    }

    private fun collecteConvertSQlToNoSqlDataBase() {
        viewModelScope.launch {
            try {

                //<--
                //TODO(1): ajout ici un collecteur de chaque daos qui ajout au periodesVent
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
            }
        }
    }






}

