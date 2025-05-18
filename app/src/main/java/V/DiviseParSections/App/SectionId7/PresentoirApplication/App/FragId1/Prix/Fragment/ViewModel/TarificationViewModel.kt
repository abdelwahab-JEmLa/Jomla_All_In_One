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
    var selectedClientId: Long? = null,
    val error: String? = null
)

class TarificationViewModel(
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
    private val ancienClientRepository: B_ClientDataBaseRepository,
) : ViewModel() {
    private val TAG = "TarificationViewModel"
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

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

    fun verifierAddNewDatasSiExistPas(
        produitDuAncienDataBase: ArticlesBasesStatsTable,
    ) {
        viewModelScope.launch {
            // Update selected IDs
            _uiState.value = _uiState.value.copy(
                selectedProductId = produitDuAncienDataBase.idArticle.toLong()
            )

            ajouteSiExistePas_A_ProduitInfos(
                produitDuAncienDataBase.idArticle.toLong(),
                produitDuAncienDataBase.nomArticleFinale
            )

            ajouteSiExistePas_B_ClientsDataBase()

            // Wait for data to refresh after client addition
            convertiseurNoSqlToSqlRepository.refreshNoSqlData()

            val selectedProduct =
                _uiState.value.outputModel.produits.find { it.infosId == _uiState.value.selectedProductId }
            val selectedClient =
                selectedProduct?.clientAchteurs?.find { it.infosId == _uiState.value.selectedClientId }
            val typeTarificationsList = selectedClient?.typeTarification ?: emptyList()

            verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

            // Check all type tarifications and upsert missing D_TarificationInfos
            typeTarificationsList.forEach { typeTarification ->
                verifierAdd_D_TarificationInfos(typeTarification)
            }
        }
    }

    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = _uiState.value.selectedClientId ?: return

        // Get existing tarification info
        val existingTarifications = get_D_TarificationInfos(
            idProduit = productId,
            idClient = clientId,
            idTypeTarification = typeTarification.infosId
        )

        // Rule for adding if not available
        if (existingTarifications.isEmpty() && typeTarification.PrixsCurrency.isNotEmpty()) {
            // Take the most recent price from NoSQL data
            val mostRecentPrice = typeTarification.PrixsCurrency.maxByOrNull { it.vidTimestamp }

            mostRecentPrice?.let { price ->
                val newTarification = D_TarificationInfos(
                    vidTimestamp = price.vidTimestamp,
                    idProduit = productId,
                    idClient = clientId,
                    idTypeTarification = typeTarification.infosId,
                    prixCurrency = price.valeur,
                    needUpdate = true
                )

                // Add to the repository
                viewModelScope.launch {
                    val currentData = convertiseurNoSqlToSqlRepository
                        .noSqlDataFlow.value

                    // Use the repository to upsert the new tarification
                    convertiseurNoSqlToSqlRepository.copyAdd_D_TarificationInfos(newTarification)
                }
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

    private fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = _uiState.value.selectedClientId ?: return
        val existingProduct = convertiseurNoSqlToSqlRepository.getB_ClientInfos(clientId)

        if (existingProduct == null) {
            val clientRelated = ancienClientRepository.modelDatas.find { it.id == clientId }

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

                    // Double check if client was added
                    val clientAfterAdd = convertiseurNoSqlToSqlRepository.getB_ClientInfos(clientId)
                    if (clientAfterAdd == null) {
                        Log.e(TAG, "Failed to upsert client with ID: $clientId")
                    } else {
                        Log.d(TAG, "Successfully added client with ID: $clientId")

                        // Create default type tarification and entry for this client-product pair
                        val productId = _uiState.value.selectedProductId
                        if (productId != null) {
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
                            Log.d(TAG, "Added default tarification for client-product pair")
                        }
                    }

                    // Force refresh the NoSQL data to reflect changes
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                }
            } else {
                Log.e(TAG, "ClientRelated not found in ancienClientRepository for ID: $clientId")
            }
        } else {
            Log.d(TAG, "Client already exists with ID: $clientId")
        }
    }

    private fun ajouteSiExistePas_A_ProduitInfos(id: Long, nom: String? = null) {
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
        idTypeTarification: Long
    ): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getTarificationInfos(
            idProduit,
            idClient,
            idTypeTarification
        )
    }
}
