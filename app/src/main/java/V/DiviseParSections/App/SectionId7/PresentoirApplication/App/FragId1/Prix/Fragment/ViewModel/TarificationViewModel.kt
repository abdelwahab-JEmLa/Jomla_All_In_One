package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TarificationViewModel(
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
) : ViewModel() {
    private val TAG = "TarificationViewModel"
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    // Add state variables to track selected product and client
    private val _selectedProductId = mutableStateOf<Long?>(null)
    private val _selectedClientId = mutableStateOf<Long?>(null)

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
        active__3_ClientsDataBase: _3_ClientsDataBase?
    ) {
        viewModelScope.launch {
            // Update selected IDs
            _selectedProductId.value = produitDuAncienDataBase.idArticle.toLong()
            _selectedClientId.value = active__3_ClientsDataBase?.vid

            ajouteSiExistePas_A_ProduitInfos(
                produitDuAncienDataBase.idArticle.toLong(),
                produitDuAncienDataBase.nomArticleFinale
            )
            ajouteSiExistePas_B_ClientsDataBase(
                active__3_ClientsDataBase?.vid!!,
                active__3_ClientsDataBase.nom
            )

            val selectedProduct = _uiState.value.outputModel.produits.find { it.infosId == _selectedProductId.value }
            val selectedClient = selectedProduct?.clientAchteurs?.find { it.infosId == _selectedClientId.value }
            val typeTarificationsList = selectedClient?.typeTarification ?: emptyList()

            verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)

            // Check all type tarifications and add missing D_TarificationInfos
            typeTarificationsList.forEach { typeTarification ->
                verifierAdd_D_TarificationInfos(typeTarification)
            }
        }
    }

    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _selectedProductId.value ?: return
        val clientId = _selectedClientId.value ?: return

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

                    // Use the repository to add the new tarification
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



    private fun ajouteSiExistePas_B_ClientsDataBase(id: Long, nom: String) {
        val existingProduct = convertiseurNoSqlToSqlRepository.getB_ClientInfos(id)

        if (existingProduct == null) {
            val newData = B_ClientInfos(
                id = id,
                nom = nom,
                needUpdate = true
            )
            convertiseurNoSqlToSqlRepository.copyAdd_B_ClientInfos(newData)
        }

    }

    private fun ajouteSiExistePas_A_ProduitInfos(id: Long, nom: String? = null) {
        val existingProduct = convertiseurNoSqlToSqlRepository.getProduitInfos(id)

        if (existingProduct == null && nom != null) {
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
