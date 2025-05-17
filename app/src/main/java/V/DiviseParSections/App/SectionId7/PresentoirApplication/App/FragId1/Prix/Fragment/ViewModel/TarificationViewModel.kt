package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
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
        currentClient: B_ClientsDataBase?
    ) {
        viewModelScope.launch {
            ajouteSiExistePas_A_ProduitInfos(
                produitDuAncienDataBase.idArticle.toLong(),
                produitDuAncienDataBase.nomArticleFinale
            )
            ajouteSiExistePas_B_ClientsDataBase(
                currentClient?.id!!,
                produitDuAncienDataBase.nomArticleFinale
            )
        }
    }

    private fun ajouteSiExistePas_B_ClientsDataBase(id: Long, nom: String) {
        val existingProduct = convertiseurNoSqlToSqlRepository.getB_ClientInfos(id)

        if (existingProduct == null ) {
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

    fun getSql_TypeTarification(
        noSqlData: ProduitNoSqlDataBase.Produit.ClientAchteur
    ): B_ClientInfos? {
        return B_ClientInfos()
    }


    fun getSql_TypeTarification(
        noSqlData: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification
    ): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(noSqlData.infosId)
    }

    fun getSqlTarifications(
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
