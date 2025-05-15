package com.example.clientjetpack.Components.ViewModel
  /*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo._InfosSqlDataBases_GroupeRepositorysImp
import com.example.clientjetpack.ID3.Test.DataBase.FireBase.ConvertiseurNoSqlToSqlRepository
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.D_TarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.TypeTarificationEnum
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrixViewModel(
    val sqlRepository = _InfosSqlDataBases_GroupeRepositorysImp    //->
    //TODO(FIXME):Fix erreur annotation is required on a value parameter
    //Classifier '_InfosSqlDataBases_GroupeRepositorysImp' does not have a companion object, and thus must be initialized her
) : ViewModel() {
    
    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Message d'erreur
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Référence Firebase
    private val firebaseRef = FirebaseDatabase.getInstance().reference.child("presentoir_data")
    
    // Repositories

    // Synchroniseur
    private val convertiseurNoSqlToSqlRepository = ConvertiseurNoSqlToSqlRepository(sqlRepository)
    
    // Exposer les StateFlows pour les données
    val produitsFlow = sqlRepository.produitRepository.modelListFlow
    val clientsFlow = sqlRepository.clientRepository.modelListFlow
    val typeTarificationsFlow = sqlRepository.typeTarificationRepository.modelListFlow
    val tarificationsFlow = sqlRepository.tarificationRepository.modelListFlow
    val produitNoSqlFlow = convertiseurNoSqlToSqlRepository.noSqlDataFlow
    
    init {
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
                            
                               */
