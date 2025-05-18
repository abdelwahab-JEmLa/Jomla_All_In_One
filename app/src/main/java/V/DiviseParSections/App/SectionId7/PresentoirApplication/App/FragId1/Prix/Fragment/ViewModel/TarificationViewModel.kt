package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "TarificationViewModel"

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    var produitAncienDB: ArticlesBasesStatsTable? = null,
    val isLoading: Boolean = false,
    val selectedProductId: Long? = null,
    val error: String? = null
)

class TarificationViewModel(
    val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
    private val ancienRepo: _0_0_HeadSQLRepositorys,
    private val ancienClientRepository: B_ClientDataBaseRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    private val ancienRepoProduitPrixVent = uiState.value.produitAncienDB?.monPrixVent

    val ancienRepoOuvertClientId = ancienRepo.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList
        .find { it.cLeDataOuvertDuParentList == true }?.clientAcheteurID

    init {
        _uiState.value = _uiState.value.copy(isLoading = true)

        Log.d(TAG, "Initializing ViewModel - Selected product ID: ${_uiState.value.selectedProductId}")
        Log.d(TAG, "Client ID from ancien repo: $ancienRepoOuvertClientId")

        viewModelScope.launch {
            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                Log.d(TAG, "NoSQL data updated - Products: ${noSqlData.produits.size}")

                // Set the selected product ID if it's not set
                if (_uiState.value.selectedProductId == null && noSqlData.produits.isNotEmpty()) {
                    Log.d(TAG, "Setting selected product ID to: ${noSqlData.produits.first().infosId}")
                    _uiState.value = _uiState.value.copy(
                        selectedProductId = noSqlData.produits.first().infosId
                    )
                }

                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )
            }
        }
    }

    fun ajouteSiExistePas_A_ProduitInfos(
        id: Long,
        produitSelectioneDuAncienDataBase: ArticlesBasesStatsTable, ) {
        val existingProduct = convertiseurNoSqlToSqlRepository.getProduitInfos(id)

        _uiState.value.produitAncienDB = produitSelectioneDuAncienDataBase

        if (existingProduct == null) {
            Log.d(TAG, "Adding product with ID: $id, Name: ${produitSelectioneDuAncienDataBase.nomArticleFinale}")

            val newData = A_ProduitInfos(
                id = id,
                nom = produitSelectioneDuAncienDataBase.nomArticleFinale,
                needUpdate = true
            )
            convertiseurNoSqlToSqlRepository.copyAdd_A_ProduitInfos(newData)

            // Update selected product ID
            _uiState.value = _uiState.value.copy(selectedProductId = id)
        } else {
            Log.d(TAG, "Product already exists or name is null - ID: $id, Exists: ${true}, Name: $")
        }
    }

    // Improved verifierAdd_D_TarificationInfos method
    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = ancienRepoOuvertClientId ?: return

        Log.d(TAG, "Verifying D_TarificationInfos for product=$productId, client=$clientId, typeTarif=${typeTarification.infosId}")

        // This is a direct coroutine launch to ensure this operation completes
        viewModelScope.launch {
            try {
                // Get existing tarification info
                val existingTarifications = get_D_TarificationInfos(
                    idProduit = productId,
                    idClient = clientId,
                    idTypeTarification = typeTarification.infosId,
                    ancienRepoProduitPrixVent = ancienRepoProduitPrixVent
                )

                // If we don't have any tarifications, create one with a default price
                if (existingTarifications.isEmpty()) {
                    val defaultPrice = ancienRepoProduitPrixVent ?: 0.0

                    Log.d(TAG, "Creating new D_TarificationInfos: product=$productId, client=$clientId, type=${typeTarification.infosId}, price=$defaultPrice")

                    val newTarification = D_TarificationInfos(
                        vidTimestamp = System.currentTimeMillis(),
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarification.infosId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

                    // Use direct suspend function to add tarification and wait for completion
                    val result = convertiseurNoSqlToSqlRepository.addTarificationInfos(newTarification)
                    Log.d(TAG, "D_TarificationInfos add result: $result")

                    // Force refresh after adding
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                } else {
                    Log.d(TAG, "D_TarificationInfos already exists: ${existingTarifications.size} entries found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in verifierAdd_D_TarificationInfos", e)
            }
        }
    }

    // Improved method for C_TypeTarificationInfos
    fun verifierAddNew_C_TypeTarificationInfos(typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Verifying C_TypeTarificationInfos with list size: ${typeTarificationsList.size}")

                // Check if we need to create default type tarifications
                if (typeTarificationsList.isEmpty()) {
                    Log.d(TAG, "No type tarifications found. Creating defaults...")
                    createDefaultTypeTarifications()
                    return@launch
                }

                // Check for PRIX_BASE type (ID=4)
                val id: Long = 4
                val tarificationInfosHistorique = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(id)

                // Add PRIX_BASE type if it doesn't exist
                if (tarificationInfosHistorique == null) {
                    Log.d(TAG, "Creating new C_TypeTarificationInfos for PRIX_BASE")

                    val newData = C_TypeTarificationInfos(
                        id = 4,
                        entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                        nom = TypeTarificationEnum.PRIX_BASE.name,
                        keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
                    )

                    // Add all enum types to ensure we have a complete dataset
                    TypeTarificationEnum.values().forEach { enumType ->
                        val enumId = enumType.ordinal + 1L
                        val existingType = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(enumId)

                        if (existingType == null) {
                            val typeData = C_TypeTarificationInfos(
                                id = enumId,
                                entityCorrespond = enumType,
                                nom = enumType.name,
                                keyFireBase = getKeyFireBase(enumId, enumType.name)
                            )

                            convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(typeData)
                            Log.d(TAG, "Added type tarification: ${enumType.name} with ID $enumId")
                        }
                    }

                    // Finally add our PRIX_BASE type and refresh
                    convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newData)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                } else {
                    Log.d(TAG, "C_TypeTarificationInfos for PRIX_BASE already exists")
                }

                // Check for any missing type tarifications from the list and add them
                typeTarificationsList.forEach { typeTarif ->
                    val existingType = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(typeTarif.infosId)
                    if (existingType == null) {
                        Log.d(TAG, "Adding missing type tarification: ${typeTarif.infosId}")

                        // Default to ParBenifice enum for any unknown type
                        val newType = C_TypeTarificationInfos(
                            id = typeTarif.infosId,
                            entityCorrespond = TypeTarificationEnum.ParBenifice,
                            nom = "Type ${typeTarif.infosId}",
                            keyFireBase = getKeyFireBase(typeTarif.infosId, "Type ${typeTarif.infosId}")
                        )

                        convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newType)
                    }
                }

                // Final refresh
                convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            } catch (e: Exception) {
                Log.e(TAG, "Error in verifierAddNew_C_TypeTarificationInfos", e)
            }
        }
    }

    // New method to create default type tarifications
    private suspend fun createDefaultTypeTarifications() {
        Log.d(TAG, "Creating default type tarifications")

        // Create all enum types
        TypeTarificationEnum.entries.forEach { enumType ->
            val enumId = enumType.ordinal + 1L
            val existingType = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(enumId)

            if (existingType == null) {
                val typeData = C_TypeTarificationInfos(
                    id = enumId,
                    entityCorrespond = enumType,
                    nom = enumType.name,
                    keyFireBase = getKeyFireBase(enumId, enumType.name)
                )

                convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(typeData)
                Log.d(TAG, "Added default type tarification: ${enumType.name} with ID $enumId")
            }
        }

        // Refresh NoSQL data
        convertiseurNoSqlToSqlRepository.refreshNoSqlData()

        // Now ensure we have at least one tarification entry
        val clientId = ancienRepoOuvertClientId ?: return
        val productId = _uiState.value.selectedProductId ?: return

        // Default to PRIX_BASE type (4)
        createDefaultTarificationIfNeeded(clientId, productId, 4L)
    }

    fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = ancienRepoOuvertClientId ?: return
        val existingClient = convertiseurNoSqlToSqlRepository.getB_ClientInfos(clientId)

        if (existingClient == null) {
            val clientRelated = ancienClientRepository.modelDatas.find { it.id == clientId }

            // Add fallback client data if it doesn't exist in the old repository
            if (clientRelated != null) {
                Log.d(TAG, "Adding client with ID: ${clientRelated.id}, Name: ${clientRelated.nom}")

                val new = B_ClientInfos(
                    id = clientRelated.id,
                    nom = clientRelated.nom,
                    needUpdate = true
                )

                // Add client synchronously and wait for completion
                viewModelScope.launch {
                    convertiseurNoSqlToSqlRepository.copyAdd_B_ClientInfos(new)
                    Log.d(TAG, "Client added successfully")

                    // Force refresh the NoSQL data to reflect changes
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                    // Create default type tarifications for this client
                    createDefaultTypeTarifications()

                    // Double check and create default data
                    val productId = _uiState.value.selectedProductId
                    if (productId != null) {
                        createDefaultTarificationIfNeeded(clientId, productId, 4L)
                    } else {
                        Log.w(TAG, "Cannot create default tarification: No selected product ID")
                    }
                }
            } else {
                // Create a default client if one doesn't exist in the old repository
                Log.d(TAG, "Creating fallback client for ID: $clientId")

                val fallbackClient = B_ClientInfos(
                    id = clientId,
                    nom = "Client $clientId",
                    needUpdate = true
                )

                viewModelScope.launch {
                    convertiseurNoSqlToSqlRepository.copyAdd_B_ClientInfos(fallbackClient)
                    Log.d(TAG, "Fallback client added successfully")

                    // Force refresh the NoSQL data to reflect changes
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                    // Create default type tarifications
                    createDefaultTypeTarifications()

                    // Create default data for this client
                    val productId = _uiState.value.selectedProductId
                    if (productId != null) {
                        createDefaultTarificationIfNeeded(clientId, productId, 4L)
                    } else {
                        Log.w(TAG, "Cannot create default tarification: No selected product ID")
                    }
                }
            }
        } else {
            Log.d(TAG, "Client already exists with ID: $clientId")
            viewModelScope.launch {
                // Create default type tarifications if needed
                createDefaultTypeTarifications()

                // Still verify we have tarification data
                val productId = _uiState.value.selectedProductId
                if (productId != null) {
                    createDefaultTarificationIfNeeded(clientId, productId, 4L)
                } else {
                    Log.w(TAG, "Cannot create default tarification: No selected product ID")
                }
            }
        }
    }

    // Modify the createDefaultTarificationIfNeeded method in TarificationViewModel.kt
// to prevent duplicate entries and add a proper uniqueness check

    private suspend fun createDefaultTarificationIfNeeded(clientId: Long, productId: Long, typeTarificationId: Long) {
        try {
            Log.d(TAG, "Creating default tarification - Client: $clientId, Product: $productId, Type: $typeTarificationId")

            // Ensure we have the type first
            val typeTarifExists = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(typeTarificationId)

            if (typeTarifExists == null) {
                Log.d(TAG, "Type tarification $typeTarificationId not found, creating it first")

                // Find the matching enum or default to PRIX_BASE
                val enumType = TypeTarificationEnum.entries.find {
                    it.ordinal + 1L == typeTarificationId
                } ?: TypeTarificationEnum.PRIX_BASE

                val newType = C_TypeTarificationInfos(
                    id = typeTarificationId,
                    entityCorrespond = enumType,
                    nom = enumType.name,
                    keyFireBase = getKeyFireBase(typeTarificationId, enumType.name)
                )

                convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newType)
                Log.d(TAG, "Created type tarification: ${enumType.name}")

                // Allow time for database to update
                delay(100)

                // Force refresh after adding type
                convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            }

            // Check if we already have tarification data - use current SQLite data directly
            val sqlDataList = convertiseurNoSqlToSqlRepository.sqlRepository.modelListFlow.value
            if (sqlDataList.isNotEmpty()) {
                val sqlData = sqlDataList.first()

                // Check if tarification already exists (with exact same client, product, type)
                val existingTarification = sqlData.d_TarificationInfos.find { tarif ->
                    tarif.idProduit == productId &&
                            tarif.idClient == clientId &&
                            tarif.idTypeTarification == typeTarificationId
                }

                if (existingTarification == null) {
                    Log.d(TAG, "No existing tarification found, creating default tarification entry")

                    // Create a default pricing entry
                    val defaultPrice = ancienRepoProduitPrixVent ?: 0.0
                    val timestamp = System.currentTimeMillis()

                    val newTarification = D_TarificationInfos(
                        vidTimestamp = timestamp,
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarificationId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

                    // Use direct suspend function for reliable adding
                    val result = convertiseurNoSqlToSqlRepository.addTarificationInfos(newTarification)
                    Log.d(TAG, "Default tarification add result: $result")

                    // Force refresh after adding tarification
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                } else {
                    Log.d(TAG, "Tarification data already exists for Product: $productId, Client: $clientId, Type: $typeTarificationId - skipping creation")
                }
            } else {
                Log.e(TAG, "Cannot check existing tarifications: no SQL data available")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in createDefaultTarificationIfNeeded", e)
        }
    }

    // Make this method public so it can be called from the UI
    fun createDefaultTarificationIfNeeded(clientId: Long) {
        val productId = _uiState.value.selectedProductId ?: return

        viewModelScope.launch {
            createDefaultTarificationIfNeeded(clientId, productId, 4L)
        }
    }

    fun getSqlProduitParSonNoSql(noSqlData: ProduitNoSqlDataBase.Produit): A_ProduitInfos? {
        return convertiseurNoSqlToSqlRepository.getProduitInfos(noSqlData.infosId)
    }

    fun get_C_TypeTarificationInfos(
        noSqlData: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification
    ): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(noSqlData.infosId)
    }

    fun get_D_TarificationInfos(
        idProduit: Long,
        idClient: Long,
        idTypeTarification: Long,
        ancienRepoProduitPrixVent: Double?
    ): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getTarificationInfos(
            idProduit,
            idClient,
            idTypeTarification,
            ancienRepoProduitPrixVent
        )
    }
}
