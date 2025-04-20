package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

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

open class PeriodeVenteViewModel(
    private val repository: _01_PeriodesVent_Repository
) : ViewModel() {
    // Direct access to repository data
    val periodesVente: SnapshotStateList<_01_PeriodesVent> get() = repository.modelDatasSnapList

    // UI state flows
    private val _uiState = MutableStateFlow(0)
    val uiState: StateFlow<Int> = _uiState.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<_01_PeriodesVent?>(null)
    val selectedPeriode: StateFlow<_01_PeriodesVent?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Store a map of product keys to their last known quantities
    private val _productQuantities = HashMap<String, Int>()

    // Flow to notify about any product changes (includes product key and new quantity)
    private val _productChanges = MutableStateFlow<Pair<String, Int>?>(null)
    val productChanges: StateFlow<Pair<String, Int>?> = _productChanges.asStateFlow()

    // Track last update time to throttle UI updates
    private var lastUpdateTime = 0L

    init {
        initViewModel()
    }

    private fun initViewModel() {
        loadPeriodesVente()
        observeRepoProgress()
        startPeriodicUpdates()
    }

    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                updateUiState()
                checkAllProductChanges()
            }
        }
    }

    // Check for changes in all products across all periods and vendors
    private fun checkAllProductChanges() {
        // Flat map all products from all periods and vendors
        val allProducts = mutableListOf<Pair<String, Produit>>()

        periodesVente.forEach { periode ->
            periode.vendeurs.forEach { vendeur ->
                vendeur.produits.forEach { produit ->
                    allProducts.add(produit.keyID to produit)
                }
            }
        }

        // Check each product for changes
        allProducts.forEach { (key, product) ->
            val previousQuantity = _productQuantities[key] ?: run {
                // First time seeing this product, store its current quantity
                _productQuantities[key] = product.quantity
                return@forEach
            }

            // Check if quantity has changed
            if (previousQuantity != product.quantity) {
                // Update stored quantity
                _productQuantities[key] = product.quantity

                // Notify about the change
                _productChanges.value = key to product.quantity
                Log.d(TAG, "Product $key quantity changed: $previousQuantity -> ${product.quantity}")
            }
        }

        // Clean up products that no longer exist
        val currentKeys = allProducts.map { it.first }.toSet()
        val keysToRemove = _productQuantities.keys.filter { it !in currentKeys }
        keysToRemove.forEach { _productQuantities.remove(it) }
    }

    // For backward compatibility or specific monitoring
    fun checkMonitoredProduct(productKey: String) {
        findProductByKey(periodesVente, productKey)?.let { product ->
            val previousQuantity = _productQuantities[productKey] ?: run {
                // First time seeing this product
                _productQuantities[productKey] = product.quantity
                return
            }

            if (previousQuantity != product.quantity) {
                _productQuantities[productKey] = product.quantity
                _productChanges.value = productKey to product.quantity
                Log.d(TAG, "Monitored product $productKey quantity updated: $previousQuantity -> ${product.quantity}")
            }
        }
    }

    // Force UI update with throttling
    private fun updateUiState() {
        val now = System.currentTimeMillis()
        if (now - lastUpdateTime > 300) {
            lastUpdateTime = now
            _uiState.value += 1
        }
    }

    private fun observeRepoProgress() {
        viewModelScope.launch {
            repository.progressRepo.collect { progress ->
                _isLoading.value = progress < 1.0f
                if (progress >= 1.0f) {
                    updateUiState()
                }
            }
        }
    }

    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshData()
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

    // Get the current quantity for a specific product
    fun getProductQuantity(productKey: String): Int {
        return _productQuantities[productKey] ?: findProductByKey(periodesVente, productKey)?.quantity ?: 0
    }
}

// Helper function to find a product by its key in the list of periods
fun findProductByKey(periods: List<_01_PeriodesVent>, productKey: String): Produit? {
    // Extract period, vendor, and product IDs from the key
    val parts = productKey.split("->")
    if (parts.size < 3) return null

    val periodKey = "${parts[0]}->${parts[1]}"

    // Find the period
    val period = periods.find { it.keyID == periodKey } ?: return null

    // Extract vendor info from the product key
    val vendorPart = parts[2]
    val vendorId = vendorPart.substringBefore("(").toLongOrNull() ?: return null

    // Find the vendor by ID
    val vendor = period.vendeurs.find { it.id == vendorId } ?: return null

    // Extract product info from the product key
    if (parts.size < 4) return null
    val productPart = parts[3]
    val productId = productPart.substringBefore("(").toLongOrNull() ?: return null

    // Find and return the product
    return vendor.produits.find { it.id == productId }
}
