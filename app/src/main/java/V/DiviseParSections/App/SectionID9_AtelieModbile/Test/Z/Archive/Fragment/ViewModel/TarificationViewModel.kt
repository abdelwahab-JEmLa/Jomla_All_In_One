package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.D1_Tariff
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.TypeTarificationEnum
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.getKeyFireBase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.ConvertiseurNoSqlToSqlRepositorys
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Models.C3_BonAchate
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "TarificationViewModel"

data class UiState(
    val produitsNoSqlDataBase: ProduitsNoSqlDataBase = ProduitsNoSqlDataBase(emptyList()),
    var produitAncienDB: ArticlesBasesStatsTable? =
        testDataArticlesBasesStatsTable(),
    var _C_3_BonAchateAncienDB: C3_BonAchate? =
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
            uiState.value._C_3_BonAchateAncienDB!!.clientAcheteurID
        )

        viewModelScope.launch {
            // getarticlescLeDataOuvertDuParentList()

            convertiseurNoSqlToSqlRepositorys.noSqlDataFlow.collectLatest { noSqlData ->
                val updatedProduits = noSqlData.produits.toMutableList()

                val activeProduct = updatedProduits.firstOrNull()
                activeProduct?.itsActiveOne = true

                if (activeProduct != null && activeProduct.clientAchteurs.isNotEmpty()) {
                    activeProduct.clientAchteurs[0].itsActiveOne = true
                }

                val updatedNoSqlData = noSqlData.copy(produits = updatedProduits)

                // Update the UI state with the new data
                _uiState.value = _uiState.value.copy(
                    produitsNoSqlDataBase = updatedNoSqlData,
                    isLoading = false
                )
            }
        }
    }


    fun gettypeActiveTarifications(produitsNoSqlDataBase: ProduitsNoSqlDataBase): List<ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification> {
        return uiState.value.produitsNoSqlDataBase
            .produits.find { it.itsActiveOne }
            ?.clientAchteurs?.find { it.itsActiveOne }
            ?.typeTarification ?: emptyList()
    }

    fun getSqlProduitActive(): A_ProduitInfos? {
        return convertiseurNoSqlToSqlRepositorys.getProduitInfos(
            _uiState.value.produitsNoSqlDataBase
                .produits.find {
                    it.itsActiveOne }
            !!.infosId
        )
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
    ): List<ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification> {
        val selectedProduct = _uiState.value.produitsNoSqlDataBase.produits.find { it.infosId == productId }
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

                    val newTarification = D1_Tariff(
                        vidTimestamp = timestamp,
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarificationId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

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

    fun verifierAdd_D_TarificationInfos(typeTarification: ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification) {
        val productId = _uiState.value.selectedProductId ?: return
        val clientId = _uiState.value._C_3_BonAchateAncienDB?.clientAcheteurID ?: return

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

                    val newTarification = D1_Tariff(
                        vidTimestamp = System.currentTimeMillis(),
                        idProduit = productId,
                        idClient = clientId,
                        idTypeTarification = typeTarification.infosId,
                        prixCurrency = defaultPrice,
                        needUpdate = true
                    )

                    convertiseurNoSqlToSqlRepositorys.addTarificationInfos(newTarification)
                    convertiseurNoSqlToSqlRepositorys.refreshNoSqlData()
                }
            } catch (e: Exception) {
            }
        }
    }

    fun verifierAddNew_C_TypeTarificationInfos(typeTarificationsList: List<ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification>) {
        viewModelScope.launch {
            try {
                // First, add the 4 default type tarifications if they don't exist yet
                val defaultTypeTarifications = listOf(
                    C_TypeTarificationInfos(
                        id = 1,
                        entityCorrespond = TypeTarificationEnum.ParBenifice,
                        nom = "Par Bénifice",
                        keyFireBase = getKeyFireBase(1, "Par Bénifice")
                    ),
                    C_TypeTarificationInfos(
                        id = 2,
                        entityCorrespond = TypeTarificationEnum.Historique,
                        nom = "Historique",
                        keyFireBase = getKeyFireBase(2, "Historique")
                    ),
                    C_TypeTarificationInfos(
                        id = 3,
                        entityCorrespond = TypeTarificationEnum.LeMaxPrixArrive,
                        nom = "Tariff Maximum",
                        keyFireBase = getKeyFireBase(3, "Tariff Maximum")
                    ),
                    C_TypeTarificationInfos(
                        id = 4,
                        entityCorrespond = TypeTarificationEnum.PRIX_BASE,
                        nom = TypeTarificationEnum.PRIX_BASE.name,
                        keyFireBase = getKeyFireBase(4, TypeTarificationEnum.PRIX_BASE.name)
                    )
                )

                // Add each default type if it doesn't exist yet
                defaultTypeTarifications.forEach { typeTarif ->
                    val existingType = convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(typeTarif.id)
                    if (existingType == null) {
                        convertiseurNoSqlToSqlRepositorys.copyAdd_C_TypeTarificationInfos(typeTarif)
                    }
                }

                // Process any additional type tarifications from the list
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


    fun ajouteSiExistePas_B_ClientsDataBase() {
        val clientId = _uiState.value._C_3_BonAchateAncienDB!!.clientAcheteurID ?: return
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

                    val productId = _uiState.value.selectedProductId
                    createDefaultTarificationIfNeeded(clientId, productId, 4L)
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

                    val productId = _uiState.value.selectedProductId
                    createDefaultTarificationIfNeeded(clientId, productId, 4L)
                }
            }
        } else {
            viewModelScope.launch {

                val productId = _uiState.value.selectedProductId
                createDefaultTarificationIfNeeded(clientId, productId, 4L)
            }
        }
    }

    fun createDefaultTarificationIfNeeded(clientId: Long) {
        val productId = _uiState.value.selectedProductId ?: return

        viewModelScope.launch {
            createDefaultTarificationIfNeeded(clientId, productId, 4L)
        }
    }


    fun getSqlProduitParSonNoSql(noSqlData: ProduitsNoSqlDataBase.Produit): A_ProduitInfos? {
        return convertiseurNoSqlToSqlRepositorys.getProduitInfos(noSqlData.infosId)
    }

    fun getByID_C_TypeTarificationInfos(
        noSqlDatainfosId: Long
    ): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(noSqlDatainfosId)
    }

    fun get_C_TypeTarificationInfos(
        noSqlData: ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification
    ): C_TypeTarificationInfos? {
        return convertiseurNoSqlToSqlRepositorys.getTypeTarificationInfos(noSqlData.infosId)
    }

    fun get_D_TarificationInfos(
        idProduit: Long,
        idClient: Long,
        idTypeTarification: Long,
        ancienRepoProduitPrixVent: Double?
    ): List<D1_Tariff> {
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
