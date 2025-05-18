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
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val selectedProductId: Long? = null,
    val error: String? = null
)

class TarificationViewModel(
    val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
    private val ancienRepo: _0_0_HeadSQLRepositorys,
    private val ancienClientRepository: B_ClientDataBaseRepository,
) : ViewModel() {
    private val TAG = "TarificationViewModel"
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    private val ancienRepoProduitPrixVent = ancienRepo.repositorys_Model
        ._2_1_ProduitsDataBase_Repository.modelDatasSnapList
        .find { it.vid == _uiState.value.selectedProductId }?.monPrixVent

    val ancienRepoOuverClientId = ancienRepo.repositorys_Model
        .repository_1_3_TransactionCommercial.modelDatasSnapList
        .find { it.cLeDataOuvertDuParentList == true }?.clientAcheteurID

    init {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )
            }
        }
    }

    fun ajouteSiExistePas_A_ProduitInfos(id: Long, nom: String? = null) {
        val existingProduct = convertiseurNoSqlToSqlRepository.getProduitInfos(id)

        if (existingProduct == null && nom != null) {
            Log.d(TAG, "Adding product with ID: $id, Name: $nom")

            val newData = A_ProduitInfos(
                id = id,
                nom = nom,
                needUpdate = true
            )
            convertiseurNoSqlToSqlRepository.copyAdd_A_ProduitInfos(newData)
        }
    }


    // Fix for verifierAdd_D_TarificationInfos method in TarificationViewModel.kt
    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = ancienRepoOuverClientId ?: return

        // Get existing tarification info
        val existingTarifications = get_D_TarificationInfos(
            idProduit = productId,
            idClient = clientId,
            idTypeTarification = typeTarification.infosId,
            ancienRepoProduitPrixVent = ancienRepoProduitPrixVent
        )

        // Rule for adding if not available
        if (existingTarifications.isEmpty() && typeTarification.PrixsCurrency.isNotEmpty()) {
            val defaultPrice = ancienRepoProduitPrixVent ?: 0.0 // Use default value if null

            val newTarification = D_TarificationInfos(
                vidTimestamp = System.currentTimeMillis(),
                idProduit = productId,
                idClient = clientId,
                idTypeTarification = typeTarification.infosId,
                prixCurrency = defaultPrice,
                needUpdate = true
            )

            // Add to the repository
            viewModelScope.launch {
                convertiseurNoSqlToSqlRepository.addTarificationInfos(newTarification) // Using the renamed suspend function
            }
        }
    }

    fun verifierAddNew_C_TypeTarificationInfos(typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>) {
        val id: Long = 4
        val tarificationInfosHistorique =
            convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(id)

        if (
            typeTarificationsList.isEmpty() || tarificationInfosHistorique == null
        ) {
            val newData =
                C_TypeTarificationInfos(
                    id = 4,
                    entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                    nom = TypeTarificationEnum.PRIX_BASE.name,
                    keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
                )
            convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newData)
        }
    }

    fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = ancienRepoOuverClientId ?: return
        val existingProduct = convertiseurNoSqlToSqlRepository.getB_ClientInfos(clientId)

        if (existingProduct == null) {
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
                    // Force refresh the NoSQL data to reflect changes
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                    // Double check and create default data
                    createDefaultTarificationIfNeeded(clientId)
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
                    // Force refresh the NoSQL data to reflect changes
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()

                    // Create default data for this client
                    createDefaultTarificationIfNeeded(clientId)
                }
            }
        } else {
            Log.d(TAG, "Client already exists with ID: $clientId")
            viewModelScope.launch {
                // Still verify we have tarification data
                createDefaultTarificationIfNeeded(clientId)
            }
        }
    }

    // Add this helper method to create default tarification for client-product pair
    private fun createDefaultTarificationIfNeeded(clientId: Long) {
        val productId = _uiState.value.selectedProductId ?: return

        // Check if we already have tarification data
        val existingTarifications = get_D_TarificationInfos(
            idProduit = productId,
            idClient = clientId,
            idTypeTarification = 4L,
            ancienRepoProduitPrixVent = ancienRepoProduitPrixVent // PRIX_BASE
        )

        if (existingTarifications.isEmpty()) {
            Log.d(TAG, "Creating default tarification for client $clientId and product $productId")

            // Create a default pricing entry
            val defaultTypeTarification = 4L // PRIX_BASE
            val defaultPrice = 0.0
            val timestamp = System.currentTimeMillis()

            val newTarification = D_TarificationInfos(
                vidTimestamp = timestamp,
                idProduit = productId,
                idClient = clientId,
                idTypeTarification = defaultTypeTarification,
                prixCurrency = defaultPrice,
                needUpdate = true
            )

            convertiseurNoSqlToSqlRepository.copyAdd_D_TarificationInfos(newTarification)
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


    // Fix for get_D_TarificationInfos method in TarificationViewModel.kt
    fun get_D_TarificationInfos(
        idProduit: Long,
        idClient: Long,
        idTypeTarification: Long,
        ancienRepoProduitPrixVent: Double? // We'll keep this parameter but not pass it to the repository
    ): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getTarificationInfos(
            idProduit,
            idClient,
            idTypeTarification ,
            ancienRepoProduitPrixVent
        )
    }
}
