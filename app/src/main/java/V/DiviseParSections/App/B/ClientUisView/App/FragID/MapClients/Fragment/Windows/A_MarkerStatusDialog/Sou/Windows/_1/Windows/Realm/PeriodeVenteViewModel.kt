package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent_Repository
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PeriodeVenteViewModel"

open class PeriodeVenteViewModel(             //<--
//TODO(1): diminue lateille et complicite du code sans changer son fonctionement
    //-->
    //TODO(): refactore et donne moi cette function _01_PeriodesVent_RepositoryImpl avec les modification naissaissaire

    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    // Utilisons directement la SnapshotStateList du repository au lieu de créer une copie
    val periodesVente: SnapshotStateList<_01_PeriodesVent>
        get() = repository.modelDatasSnapList

    // UI state trigger to force recomposition
    private val _uiState = MutableStateFlow(0)
    val uiState: StateFlow<Int> = _uiState.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    // Add a specific state holder for the monitored product
    private val _monitoredProductQuantity = MutableStateFlow(0)
    val monitoredProductQuantity: StateFlow<Int> = _monitoredProductQuantity.asStateFlow()

    // Keep track of the last update time to throttle UI updates
    private var lastUpdateTime = 0L

    init {
        loadPeriodesVente()
        observeRepoProgress()
        startProductMonitoring()
        startMonitoredProductChecker()

    }
    private fun startMonitoredProductChecker() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                checkMonitoredProduct("2025_04_19->11:00->2(Vendeur 2)->2(Produit 2)")
            }
        }
    }

    // Add a function to check and update the monitored product
    fun checkMonitoredProduct(productKey: String) {
        viewModelScope.launch {
            val product = findProductByKey(periodesVente, productKey)
            if (product != null && product.quantity != _monitoredProductQuantity.value) {
                _monitoredProductQuantity.value = product.quantity
                Log.d(TAG, "Monitored product quantity updated to: ${product.quantity}")
            }
        }
    }

    private fun startProductMonitoring() {
        viewModelScope.launch {
            while (true) {
                // Check for updates every 500ms
                delay(500)
                updateUiState()
            }
        }
    }

    // Force UI update
    private fun updateUiState() {
        // Don't update too frequently
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime > 300) {
            lastUpdateTime = now
            _uiState.value += 1
            Log.d(TAG, "Forcing UI update: ${_uiState.value}")
        }
    }

    private fun observeRepoProgress() {
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                _isLoading.value = progress < 1.0f
                if (progress >= 1.0f) {
                    // Force UI update when data is loaded
                    updateUiState()
                }
            }
        }
    }

    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
            // Force UI update after loading
            updateUiState()
            _isLoading.value = false
        }
    }

    fun selectPeriode(periode: _01_PeriodesVent) {
        _selectedPeriode.value = periode
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
            updateUiState()
            _isLoading.value = false
        }
    }
}
