package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.PeriodeVente
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.PeriodeVenteRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

open class PeriodeVenteViewModel(private val repository: PeriodeVenteRepository) : ViewModel() {

    private val _periodesVente = MutableStateFlow<List<PeriodeVente>>(emptyList())
    val periodesVente: StateFlow<List<PeriodeVente>> = _periodesVente.asStateFlow()

    private val _selectedPeriode = MutableStateFlow<PeriodeVente?>(null)
    val selectedPeriode: StateFlow<PeriodeVente?> = _selectedPeriode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPeriodesVente()

        // Create test data when initialized
        viewModelScope.launch {
          //  createTestDataIfEmpty()
        }
    }

    private fun loadPeriodesVente() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllPeriodeVentes().collectLatest { periodes ->
                _periodesVente.value = periodes
                _isLoading.value = false

                // Select the first period if available and nothing is currently selected
                if (_selectedPeriode.value == null && periodes.isNotEmpty()) {
                    _selectedPeriode.value = periodes.first()
                }
            }
        }
    }

    fun selectPeriode(periode: PeriodeVente) {
        _selectedPeriode.value = periode
    }

    suspend fun createPeriodeVente(dateDebut: String, tempDebut: String) {
        repository.createPeriodeVente(dateDebut, tempDebut)
    }

    suspend fun addVendeurToPeriode(periodeId: String, nomVendeur: String) {
        repository.addVendeurToPeriodeVente(periodeId, nomVendeur)
    }

    suspend fun addProduitToVendeur(periodeId: String, vendeurId: String, nomProduit: String, quantity: Int) {
        repository.addProduitToVendeur(periodeId, vendeurId, nomProduit, quantity)
    }

    suspend fun updateProduitQuantity(periodeId: String, vendeurId: String, produitId: String, newQuantity: Int) {
        repository.updateProduitQuantity(periodeId, vendeurId, produitId, newQuantity)
    }

    suspend fun deletePeriode(periodeId: String) {
        repository.deletePeriodeVente(periodeId)
        if (_selectedPeriode.value?.keyID == periodeId) {
            _selectedPeriode.value = _periodesVente.value.firstOrNull { it.keyID != periodeId }
        }
    }

    private suspend fun createTestDataIfEmpty() {
        // Check if data exists
        val currentPeriodes = _periodesVente.value
        if (currentPeriodes.isEmpty()) {
            // Create test data
            val periodeId = UUID.randomUUID().toString()
            val periode = PeriodeVente().apply {
                keyID = periodeId
                dateDebutDeCettePeriode = "2025_04_19"
                tempDebutDeCettePeriode = "09:00"
            }

            val vendeur1Id = UUID.randomUUID().toString()
            val vendeur1 = Vendeur().apply {
                keyID = vendeur1Id
                startIndex = 0
                nom = "Jean Dupont"
            }

            val vendeur2Id = UUID.randomUUID().toString()
            val vendeur2 = Vendeur().apply {
                keyID = vendeur2Id
                startIndex = 1
                nom = "Marie Martin"
            }

            // Add products for Jean
            val produit1 = Produit().apply {
                keyID = UUID.randomUUID().toString()
                startIndex = 0
                nom = "Pommes"
                quantity = 50
            }

            val produit2 = Produit().apply {
                keyID = UUID.randomUUID().toString()
                startIndex = 1
                nom = "Poires"
                quantity = 30
            }

            vendeur1.produits.add(produit1)
            vendeur1.produits.add(produit2)

            // Add products for Marie
            val produit3 = Produit().apply {
                keyID = UUID.randomUUID().toString()
                startIndex = 0
                nom = "Tomates"
                quantity = 40
            }

            val produit4 = Produit().apply {
                keyID = UUID.randomUUID().toString()
                startIndex = 1
                nom = "Carottes"
                quantity = 60
            }

            vendeur2.produits.add(produit3)
            vendeur2.produits.add(produit4)

            // Add vendeurs to period
            periode.vendeurs.add(vendeur1)
            periode.vendeurs.add(vendeur2)

            // Save to database
            repository.realm.write {
                copyToRealm(periode)
            }

            // Create a second test period
            val periode2Id = UUID.randomUUID().toString()
            val periode2 = PeriodeVente().apply {
                keyID = periode2Id
                dateDebutDeCettePeriode = "2025_04_20"
                tempDebutDeCettePeriode = "10:30"
            }

            val vendeur3Id = UUID.randomUUID().toString()
            val vendeur3 = Vendeur().apply {
                keyID = vendeur3Id
                startIndex = 0
                nom = "Sophie Bernard"
            }

            // Add products for Sophie
            val produit5 = Produit().apply {
                keyID = UUID.randomUUID().toString()
                startIndex = 0
                nom = "Fraises"
                quantity = 25
            }

            vendeur3.produits.add(produit5)
            periode2.vendeurs.add(vendeur3)

            // Save second period
            repository.realm.write {
                copyToRealm(periode2)
            }
        }
    }
}
