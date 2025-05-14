package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.TypeTarificationEnum
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel principal pour la gestion des données des tarifications
 */
class PrixViewModel(context: Context) : ViewModel() {
    
    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Message d'erreur
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Référence Firebase
    private val firebaseRef = FirebaseDatabase.getInstance().reference.child("presentoir_data")
    
    // Repositories
    private val sqlRepository = InfosSqlDataBasesRepository(context, firebaseRef)
    
    // Synchroniseur
    private val dataSynchronizer = DataSynchronizer(sqlRepository)
    
    // Exposer les StateFlows pour les données
    val produitsFlow = sqlRepository.produitRepository.modelListFlow
    val clientsFlow = sqlRepository.clientRepository.modelListFlow
    val typeTarificationsFlow = sqlRepository.typeTarificationRepository.modelListFlow
    val tarificationsFlow = sqlRepository.tarificationRepository.modelListFlow
    val produitNoSqlFlow = dataSynchronizer.noSqlDataFlow
    
    init {
        // Charger les données au démarrage
        loadAllData()
    }
    
    /**
     * Charger toutes les données depuis Firebase
     */
    fun loadAllData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                sqlRepository.loadAllDataFromFirebase()
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors du chargement des données: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Ajouter un nouveau produit
     */
    fun addProduit(nom: String, onSuccess: (A_ProduitInfos) -> Unit = {}) {
        val produit = A_ProduitInfos(nom = nom)
        sqlRepository.produitRepository.add(produit, onSuccess)
        syncToFirebase(produit)
    }
    
    /**
     * Ajouter un nouveau client
     */
    fun addClient(nom: String, typeTarificationId: Long = 0) {
        val client = B_ClientInfos(
            nom = nom,
            idActiveTypeTarificationDataBase = typeTarificationId
        )
        sqlRepository.clientRepository.add(client)
    }
    
    /**
     * Ajouter un nouveau type de tarification
     */
    fun addTypeTarification(type: TypeTarificationEnum) {
        val typeTarification = C_TypeTarificationInfos(typeTarificationEnum = type)
        sqlRepository.typeTarificationRepository.add(typeTarification)
    }
    
    /**
     * Ajouter une nouvelle tarification
     */
    fun addTarification(
        produitId: Long, 
        clientId: Long, 
        typeTarificationId: Long, 
        prix: Double,
        onSuccess: (D_TarificationInfos) -> Unit = {}
    ) {
        val tarification = D_TarificationInfos(
            vidTimestamp = System.currentTimeMillis(),
            idProduit = produitId,
            idClient = clientId,
            idTypeTarification = typeTarificationId,
            prixCurrency = prix
        )
        sqlRepository.tarificationRepository.add(tarification, onSuccess)
    }
    
    /**
     * Mettre à jour le client
     */
    fun updateClient(client: B_ClientInfos, onSuccess: (B_ClientInfos) -> Unit = {}) {
        sqlRepository.clientRepository.update(client, onSuccess)
    }
    
    /**
     * Synchroniser les données avec Firebase
     */
    private fun syncToFirebase(item: Any) {
        when(item) {
            is A_ProduitInfos -> sqlRepository.produitRepository.setAuFireBase(item)
            // Autres cas à gérer au besoin
        }
    }
}
