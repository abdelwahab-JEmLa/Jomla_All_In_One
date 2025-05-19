package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepository
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
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
    val appDatabase: AppDatabase,

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

        viewModelScope.launch {

            getarticlescLeDataOuvertDuParentList()

            convertiseurNoSqlToSqlRepository.noSqlDataFlow.collectLatest { noSqlData ->
                if (_uiState.value.selectedProductId == null && noSqlData.produits.isNotEmpty()) {
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

    private suspend fun getarticlescLeDataOuvertDuParentList (): Unit {
        val articlescLeDataOuvertDuParentList = appDatabase.articlesBasesStatsModelDao()
            .getAll().find {
            it.cLeDataOuvertDuParentList
        }
        _uiState.value.produitAncienDB  = articlescLeDataOuvertDuParentList
    }

    private suspend fun createDefaultTarificationIfNeeded(clientId: Long, productId: Long, typeTarificationId: Long) {
        try {
            val typeTarifExists = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(typeTarificationId)

            if (typeTarifExists == null) {
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

                delay(100)
                convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            }

            val sqlDataList = convertiseurNoSqlToSqlRepository.sqlRepository.modelListFlow.value
            if (sqlDataList.isNotEmpty()) {
                val sqlData = sqlDataList.first()

                val existingTarification = sqlData.d_TarificationInfos.find { tarif ->
                    tarif.idProduit == productId &&
                            tarif.idClient == clientId &&
                            tarif.idTypeTarification == typeTarificationId
                }

                if (existingTarification == null) {
                    val defaultPrice = if (ancienRepoProduitPrixVent != null && ancienRepoProduitPrixVent > 0.0) {
                        ancienRepoProduitPrixVent
                    } else {
                        val productPricing = sqlData.d_TarificationInfos
                            .filter { it.idProduit == productId && it.prixCurrency > 0 }
                            .maxByOrNull { it.vidTimestamp }?.prixCurrency

                        productPricing ?: 10.0
                    }

                    val timestamp = System.currentTimeMillis()

                    val newTarification = D_TarificationInfos(
                        vidTimestamp = timestamp,
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarificationId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

                    val result = convertiseurNoSqlToSqlRepository.addTarificationInfos(newTarification)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                } else {
                    if (existingTarification.prixCurrency == 0.0) {
                        val updatedPrice = if (ancienRepoProduitPrixVent != null && ancienRepoProduitPrixVent > 0.0) {
                            ancienRepoProduitPrixVent
                        } else {
                            10.0
                        }

                        val updatedTarification = existingTarification.copy(
                            prixCurrency = updatedPrice,
                            vidTimestamp = System.currentTimeMillis(),
                            needUpdate = true
                        )

                        val updatedTarifications = sqlData.d_TarificationInfos.toMutableList().apply {
                            remove(existingTarification)
                            add(updatedTarification)
                        }

                        val updatedData = sqlData.copy(
                            d_TarificationInfos = updatedTarifications
                        )

                        convertiseurNoSqlToSqlRepository.sqlRepository.upsert(updatedData)
                        convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    fun ajouteSiExistePas_A_ProduitInfos() {
        val id = uiState.value.produitAncienDB!!.idArticle.toLong()

        val existingProduct = convertiseurNoSqlToSqlRepository
            .getProduitInfos(id)

        if (existingProduct == null) {
            val newData = A_ProduitInfos(
                id = id,
                nom = uiState.value.produitAncienDB!!.nomArticleFinale,
                needUpdate = true
            )
            convertiseurNoSqlToSqlRepository.copyAdd_A_ProduitInfos(newData)

            _uiState.value = _uiState.value.copy(selectedProductId = id)
        }
    }

    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = ancienRepoOuvertClientId ?: return

        viewModelScope.launch {
            try {
                val existingTarifications = get_D_TarificationInfos(
                    idProduit = productId,
                    idClient = clientId,
                    idTypeTarification = typeTarification.infosId,
                    ancienRepoProduitPrixVent = ancienRepoProduitPrixVent
                )

                if (existingTarifications.isEmpty()) {
                    val defaultPrice = ancienRepoProduitPrixVent ?: 0.0

                    val newTarification = D_TarificationInfos(
                        vidTimestamp = System.currentTimeMillis(),
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarification.infosId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

                    val result = convertiseurNoSqlToSqlRepository.addTarificationInfos(newTarification)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun verifierAddNew_C_TypeTarificationInfos(typeTarificationsList: List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification>) {
        viewModelScope.launch {
            try {
                if (typeTarificationsList.isEmpty()) {
                    createDefaultTypeTarifications()
                    return@launch
                }

                val id: Long = 4
                val tarificationInfosHistorique = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(id)

                if (tarificationInfosHistorique == null) {
                    val newData = C_TypeTarificationInfos(
                        id = 4,
                        entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                        nom = TypeTarificationEnum.PRIX_BASE.name,
                        keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
                    )

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
                        }
                    }

                    convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newData)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                }

                typeTarificationsList.forEach { typeTarif ->
                    val existingType = convertiseurNoSqlToSqlRepository.getTypeTarificationInfos(typeTarif.infosId)
                    if (existingType == null) {
                        val newType = C_TypeTarificationInfos(
                            id = typeTarif.infosId,
                            entityCorrespond = TypeTarificationEnum.ParBenifice,
                            nom = "Type ${typeTarif.infosId}",
                            keyFireBase = getKeyFireBase(typeTarif.infosId, "Type ${typeTarif.infosId}")
                        )

                        convertiseurNoSqlToSqlRepository.copyAdd_C_TypeTarificationInfos(newType)
                    }
                }

                convertiseurNoSqlToSqlRepository.refreshNoSqlData()
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun createDefaultTypeTarifications() {
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
            }
        }

        convertiseurNoSqlToSqlRepository.refreshNoSqlData()

        val clientId = ancienRepoOuvertClientId ?: return
        val productId = _uiState.value.selectedProductId ?: return

        createDefaultTarificationIfNeeded(clientId, productId, 4L)
    }

    fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = ancienRepoOuvertClientId ?: return
        val existingClient = convertiseurNoSqlToSqlRepository.getB_ClientInfos(clientId)

        if (existingClient == null) {
            val clientRelated = ancienClientRepository.modelDatas.find { it.id == clientId }

            if (clientRelated != null) {
                val new = B_ClientInfos(
                    id = clientRelated.id,
                    nom = clientRelated.nom,
                    needUpdate = true
                )

                viewModelScope.launch {
                    convertiseurNoSqlToSqlRepository.copyAdd_B_ClientInfos(new)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                    createDefaultTypeTarifications()

                    val productId = _uiState.value.selectedProductId
                    if (productId != null) {
                        createDefaultTarificationIfNeeded(clientId, productId, 4L)
                    }
                }
            } else {
                val fallbackClient = B_ClientInfos(
                    id = clientId,
                    nom = "Client $clientId",
                    needUpdate = true
                )

                viewModelScope.launch {
                    convertiseurNoSqlToSqlRepository.copyAdd_B_ClientInfos(fallbackClient)
                    convertiseurNoSqlToSqlRepository.refreshNoSqlData()
                    createDefaultTypeTarifications()

                    val productId = _uiState.value.selectedProductId
                    if (productId != null) {
                        createDefaultTarificationIfNeeded(clientId, productId, 4L)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                createDefaultTypeTarifications()

                val productId = _uiState.value.selectedProductId
                if (productId != null) {
                    createDefaultTarificationIfNeeded(clientId, productId, 4L)
                }
            }
        }
    }

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
