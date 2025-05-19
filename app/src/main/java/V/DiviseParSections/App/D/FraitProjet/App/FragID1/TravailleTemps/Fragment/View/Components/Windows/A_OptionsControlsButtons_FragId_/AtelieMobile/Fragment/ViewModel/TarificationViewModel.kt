package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.D_TarificationInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.TypeTarificationEnum
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.getKeyFireBase
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepositorys
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
    var produitAncienDB: ArticlesBasesStatsTable? =
        testDataArticlesBasesStatsTable(),
    var _1_3_TransactionCommercialAncienDB: _1_3_TransactionCommercial? =
        testData_1_3_TransactionCommercial(),
    val selectedProductId: Long = testDataArticlesBasesStatsTable().idArticle.toLong(),

    val isLoading: Boolean = false,
    val error: String? = null,
    val isInitialSetupComplete: Boolean = false,
    val isTarificationTypesProcessed: Boolean = false
)

class TarificationViewModel(
    val appDatabase: AppDatabase,

    private val convertiseurNoSqlToSqlRepositorys: ConvertiseurNoSqlToSqlRepositorys,
    private val ancienRepo: _0_0_HeadSQLRepositorys,
    private val ancienClientRepository: B_ClientDataBaseRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    private val ancienRepoProduitPrixVent = uiState.value.produitAncienDB?.monPrixVent


    init {
        _uiState.value = _uiState.value.copy(isLoading = true)
        performInitialSetup()
        processTarificationTypes(
            uiState.value.selectedProductId,
            uiState.value._1_3_TransactionCommercialAncienDB!!.clientAcheteurID
        )

        viewModelScope.launch {
          //  getarticlescLeDataOuvertDuParentList()

            convertiseurNoSqlToSqlRepositorys.noSqlDataFlow.collectLatest { noSqlData ->
                _uiState.value = _uiState.value.copy(
                    outputModel = noSqlData,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun getarticlescLeDataOuvertDuParentList(): Unit {
        val articlescLeDataOuvertDuParentList = appDatabase.articlesBasesStatsModelDao()
            .getAll().find {
                it.cLeDataOuvertDuParentList
            }

        _uiState.value = _uiState.value.copy(
            produitAncienDB = articlescLeDataOuvertDuParentList
        )
    }

    fun performInitialSetup() {
        if (!_uiState.value.isInitialSetupComplete) {
            viewModelScope.launch {
                ajouteSiExistePas_A_ProduitInfos()
                ajouteSiExistePas_B_ClientsDataBase()
                convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()

                _uiState.value = _uiState.value.copy(isInitialSetupComplete = true)
            }
        }
    }

    fun processTarificationTypes(selectedClientId: Long, selectedProductId: Long) {
        if (!_uiState.value.isTarificationTypesProcessed) {
            viewModelScope.launch {
                val typeTarificationsList =
                    getTypeTarificationsList(selectedClientId, selectedProductId)
                val sqlDataList = convertiseurNoSqlToSqlRepositorys.sqlRepository.modelListFlow.value

                val existingTarifications = if (sqlDataList.isNotEmpty()) {
                    val sqlData = sqlDataList.first()
                    sqlData.d_TarificationInfos.filter {
                        it.idClient == selectedClientId && it.idProduit == selectedProductId
                    }
                } else emptyList()

                if (existingTarifications.isNotEmpty()) {
                    verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                } else if (typeTarificationsList.isNotEmpty()) {
                    verifierAddNew_C_TypeTarificationInfos(typeTarificationsList)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()

                    typeTarificationsList.forEach { typeTarification ->
                        verifierAdd_D_TarificationInfos(typeTarification)
                    }

                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                } else if (selectedClientId > 0 && existingTarifications.isEmpty()) {
                    createDefaultTarificationIfNeeded(selectedClientId)
                } else if (selectedClientId > 0) {
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                }

                _uiState.value = _uiState.value.copy(isTarificationTypesProcessed = true)
            }
        }
    }

    private fun getTypeTarificationsList(
        clientId: Long,
        productId: Long
    ): List<ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification> {
        val selectedProduct = _uiState.value.outputModel.produits.find { it.infosId == productId }
        val selectedClient = selectedProduct?.clientAchteurs?.find { it.infosId == clientId }
        return selectedClient?.typeTarification ?: emptyList()
    }


    private suspend fun createDefaultTarificationIfNeeded(
        clientId: Long,
        productId: Long,
        typeTarificationId: Long
    ) {
        try {
            val typeTarifExists =
                convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(typeTarificationId)

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

                convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(newType)

                delay(100)
                convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
            }

            val sqlDataList = convertiseurNoSqlToSqlRepositorys.sqlRepository.modelListFlow.value
            if (sqlDataList.isNotEmpty()) {
                val sqlData = sqlDataList.first()

                val existingTarification = sqlData.d_TarificationInfos.find { tarif ->
                    tarif.idProduit == productId &&
                            tarif.idClient == clientId &&
                            tarif.idTypeTarification == typeTarificationId
                }

                if (existingTarification == null) {
                    val defaultPrice =
                        if (ancienRepoProduitPrixVent != null && ancienRepoProduitPrixVent > 0.0) {
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

                    val result =
                        convertiseurNoSqlToSqlRepositorys.addTarificationInfos(newTarification)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                } else {
                    if (existingTarification.prixCurrency == 0.0) {
                        val updatedPrice =
                            if (ancienRepoProduitPrixVent != null && ancienRepoProduitPrixVent > 0.0) {
                                ancienRepoProduitPrixVent
                            } else {
                                10.0
                            }

                        val updatedTarification = existingTarification.copy(
                            prixCurrency = updatedPrice,
                            vidTimestamp = System.currentTimeMillis(),
                            needUpdate = true
                        )

                        val updatedTarifications =
                            sqlData.d_TarificationInfos.toMutableList().apply {
                                remove(existingTarification)
                                add(updatedTarification)
                            }

                        val updatedData = sqlData.copy(
                            d_TarificationInfos = updatedTarifications
                        )

                        convertiseurNoSqlToSqlRepositorys.sqlRepository.upsert(updatedData)
                        convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    fun ajouteSiExistePas_A_ProduitInfos() {
        val id = uiState.value.produitAncienDB!!.idArticle.toLong()

        val existingProduct = convertiseurNoSqlToSqlRepositorys
            .getProduitInfos(id)

        if (existingProduct == null) {
            val newData = A_ProduitInfos(
                id = id,
                nom = uiState.value.produitAncienDB!!.nomArticleFinale,
                needUpdate = true
            )
            convertiseurNoSqlToSqlRepositorys.copyAdd_A_ProduitInfos(newData)

            _uiState.value = _uiState.value.copy(selectedProductId = id)
        }
    }

    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = _uiState.value._1_3_TransactionCommercialAncienDB?.clientAcheteurID ?: return

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

                    val result =
                        convertiseurNoSqlToSqlRepositorys.addTarificationInfos(newTarification)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
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
                val tarificationInfosHistorique =
                    convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(id)

                if (tarificationInfosHistorique == null) {
                    val newData = C_TypeTarificationInfos(
                        id = 4,
                        entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                        nom = TypeTarificationEnum.PRIX_BASE.name,
                        keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
                    )

                    TypeTarificationEnum.values().forEach { enumType ->
                        val enumId = enumType.ordinal + 1L
                        val existingType =
                            convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(enumId)

                        if (existingType == null) {
                            val typeData = C_TypeTarificationInfos(
                                id = enumId,
                                entityCorrespond = enumType,
                                nom = enumType.name,
                                keyFireBase = getKeyFireBase(enumId, enumType.name)
                            )

                            convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(
                                typeData
                            )
                        }
                    }

                    convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(newData)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                }

                typeTarificationsList.forEach { typeTarif ->
                    val existingType =
                        convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(typeTarif.infosId)
                    if (existingType == null) {
                        val newType = C_TypeTarificationInfos(
                            id = typeTarif.infosId,
                            entityCorrespond = TypeTarificationEnum.ParBenifice,
                            nom = "Type ${typeTarif.infosId}",
                            keyFireBase = getKeyFireBase(
                                typeTarif.infosId,
                                "Type ${typeTarif.infosId}"
                            )
                        )

                        convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(newType)
                    }
                }

                convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun createDefaultTypeTarifications() {
        TypeTarificationEnum.entries.forEach { enumType ->
            val enumId = enumType.ordinal + 1L
            val existingType = convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(enumId)

            if (existingType == null) {
                val typeData = C_TypeTarificationInfos(
                    id = enumId,
                    entityCorrespond = enumType,
                    nom = enumType.name,
                    keyFireBase = getKeyFireBase(enumId, enumType.name)
                )

                convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(typeData)
            }
        }

        convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()

        val clientId = _uiState.value._1_3_TransactionCommercialAncienDB!!.clientAcheteurID
        val productId = _uiState.value.selectedProductId ?: return

        createDefaultTarificationIfNeeded(clientId, productId, 4L)
    }

    fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = _uiState.value._1_3_TransactionCommercialAncienDB!!.clientAcheteurID ?: return
        val existingClient = convertiseurNoSqlToSqlRepositorys.getB_ClientInfos(clientId)

        if (existingClient == null) {
            val clientRelated = ancienClientRepository.modelDatas.find { it.id == clientId }

            if (clientRelated != null) {
                val new = B_ClientInfos(
                    id = clientRelated.id,
                    nom = clientRelated.nom,
                    needUpdate = true
                )

                viewModelScope.launch {
                    convertiseurNoSqlToSqlRepositorys.copyAdd_B_ClientInfos(new)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
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
                    convertiseurNoSqlToSqlRepositorys.copyAdd_B_ClientInfos(fallbackClient)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
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
        return convertiseurNoSqlToSqlRepositorys.getProduitInfos(noSqlData.infosId)
    }

    fun get_C_TypeTarificationInfos(
        noSqlData: ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification
    ): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(noSqlData.infosId)
    }

    fun get_D_TarificationInfos(
        idProduit: Long,
        idClient: Long,
        idTypeTarification: Long,
        ancienRepoProduitPrixVent: Double?
    ): List<D_TarificationInfos> {
        return convertiseurNoSqlToSqlRepositorys.getTarificationInfos(
            idProduit,
            idClient,
            idTypeTarification,
            ancienRepoProduitPrixVent
        )
    }

    fun resetProcessFlags() {
        _uiState.value = _uiState.value.copy(
            isInitialSetupComplete = false,
            isTarificationTypesProcessed = false
        )
    }
}
