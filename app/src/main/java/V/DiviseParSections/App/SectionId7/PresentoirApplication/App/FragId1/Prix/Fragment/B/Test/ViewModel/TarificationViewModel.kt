package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.NoSQL.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.NoSQL.Model.ProduitNoSqlDataBase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

data class UiState(
    val outputModel: ProduitNoSqlDataBase = ProduitNoSqlDataBase(emptyList()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TarificationViewModel(
    private val convertiseurNoSqlToSqlRepository: ConvertiseurNoSqlToSqlRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    init {
        viewModelScope.launch {
            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )
            }
        }
    }

    fun getSqlProduit(id: Long): A_ProduitInfos? {
        return convertiseurNoSqlToSqlRepository.getProduitInfos(id)
    }

    /**
     * Returns information about a client based on its ID
     */
    fun getSqlClient(id: Long): SqlClientInfos? {
        return convertiseurNoSqlToSqlRepository.getClientInfos(id)
    }

    /**
     * Returns information about a tarification type based on its ID
     */
    fun getSqlTypeTarification(id: Long): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(id)
    }

    /**
     * Returns all tarification entries for a specific product, client and tarification type
     */
    fun getSqlTarifications(idProduit: Long, idClient: Long, idTypeTarification: Long): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getTarificationInfos(idProduit, idClient, idTypeTarification)
    }

    /**
     * Returns the latest tarification entry for a specific product, client and tarification type
     */
    fun getLatestSqlTarification(idProduit: Long, idClient: Long, idTypeTarification: Long): D_TarificationInfos? {
        return convertiseurNoSqlToSqlRepository.getLatestTarificationInfo(idProduit, idClient, idTypeTarification)
    }

    /**
     * Returns all available products from the database
     */
    fun getAllProduits(): List<A_ProduitInfos> {
        return convertiseurNoSqlToSqlRepository.getAllProduits()
    }

    /**
     * Returns all available clients from the database
     */
    fun getAllClients(): List<SqlClientInfos> {
        return convertiseurNoSqlToSqlRepository.getAllClients()
    }

    /**
     * Returns all available tarification types from the database
     */
    fun getAllTypeTarifications(): List<C_TypeTarificationInfos> {
        return convertiseurNoSqlToSqlRepository.getAllTypeTarifications()
    }
}
