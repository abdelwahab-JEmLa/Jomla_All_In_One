package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModel_DataBaseInitFactory_1Produit(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    data class UiState(
        val value: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
