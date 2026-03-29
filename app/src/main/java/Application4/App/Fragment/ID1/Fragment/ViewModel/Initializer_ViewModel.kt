package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class Initializer_ViewModel(private val AViewModel_NewProtoPatterns: A_ViewModel_NewProtoPatterns) {
    fun run() {
        collect_ListDatas()
        load_then_Collect_Active_DatasMutableStates()
    }

    private fun collect_ListDatas() {
        load_then_Collect_Active_Datas()
        collectListM16FilteredByCatalogue()
        collectListM1Produit()
        collectList_M3()
        collectList_M8BonVent()
        collectList_M2Client()
    }

    private fun load_then_Collect_Active_DatasMutableStates() {
        collectFilteredProductTree()
        collectM10OperationVentCouleur()
    }

    private fun load_then_Collect_Active_Datas() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            loadAllDatasOnce()
            collectActiveM9Compt()
        }
    }

    private suspend fun loadAllDatasOnce() {
        fun progress(p: Float) {
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
        }
        progress(1 / 8f); val clients = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
        progress(2 / 8f); val categories = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAll()
            .filter { it.catalogueParentId == AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue.id }
        progress(3 / 8f); val appCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().getAll()
        progress(4 / 8f); val bonVent = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
        progress(5 / 8f); val ventPeriodes = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
        progress(6 / 8f); val tarification = AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
        progress(7 / 8f); val operationVentCouleurs = AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()

        seedActiveDatas(
            appCompt = appCompt,
            bonVent = bonVent,
            clients = clients,
        )

        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                initDatasProgressEtate = 1f,
                list_Datas = List_Datas(
                    m2Client = clients,
                    m14VentPeriode = ventPeriodes,
                    m16CategorieProduit = categories,
                    m8BonVent = bonVent,
                    m13TarificationInfos = tarification,
                    m10OperationVentCouleur = operationVentCouleurs,
                )
            )
    }

    private fun seedActiveDatas(
        appCompt: List<Z_AppCompt>,
        bonVent: List<M8BonVent>,
        clients: List<M2Client>,
    ) {
        val centralKey = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
        AViewModel_NewProtoPatterns.active_Datas.active_M9Compt =
            appCompt.find { it.keyID == centralKey && it.keyID.isNotBlank() }
        AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = bonVent
        AViewModel_NewProtoPatterns.active_Datas.list_M2Client = clients
    }

    private suspend fun collectActiveM9Compt() {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt(),
            activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
        ).collect { }
    }

    private fun collectListM16FilteredByCatalogue() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_listM16_FilteredBy_active_M21Catalogue(
                dao_M16CategorieProduit = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit(),
                active_M21Catalogue = AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue
            ).collect {
                AViewModel_NewProtoPatterns.active_Datas.listM16_FilteredBy_active_M21Catalogue = it
            }
        }
    }

    private fun collectListM1Produit() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_list_M1Produit(
                dao_M1Produit = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit(),
                activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
            ).collect { /* side-effect: active_Datas.list_M1Produit updated via onEach */ }
        }
    }

    // Keeps active_Datas.list_M03CouleurProduitInfos live via a flow so that
    // collectFilteredProductTree always sees up-to-date colours without going through UiState.
    private fun collectList_M3() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = it
                }
        }
    }

    // Keeps active_Datas.list_M8BonVent live so that derived getters
    // (activeOnVent_M8BonVent, filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod, …) stay fresh.
    private fun collectList_M8BonVent() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = it
                }
        }
    }

    // Keeps active_Datas.list_M2Client live so that activeOnVent_M2Client stays fresh.
    private fun collectList_M2Client() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M2Client = it
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectFilteredProductTree() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.Main) {
            snapshotFlow { AViewModel_NewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter }
                .flatMapLatest { currentFilter ->
                    channelFlow {
                        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.collectLatest {
                            FlowsFunctions_ActiveDatasFragNewProto
                                .getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
                                    dao_M16CategorieProduit = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit(),
                                    activeCatalogue = AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue,
                                    activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
                                    activeFilter = currentFilter,
                                ).collect { send(it) }
                        }
                    }
                }
                .collect { tree ->
                    AViewModel_NewProtoPatterns.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur = tree
                }
        }
    }

    private fun collectM10OperationVentCouleur() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_listM10OperationVentCouleur_By_active_Central_Values(
                dao_M10OperationVentCouleur = AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur(),
                dao_M9AppCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt()
            ).collect { (emittedKey, filtered) ->
                when {
                    filtered.isNotEmpty() -> {
                        AViewModel_NewProtoPatterns.active_Datas.lastKnownBonVentKey = emittedKey
                        AViewModel_NewProtoPatterns.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = filtered
                    }
                    emittedKey == null -> Unit
                    emittedKey != AViewModel_NewProtoPatterns.active_Datas.lastKnownBonVentKey -> {
                        AViewModel_NewProtoPatterns.active_Datas.lastKnownBonVentKey = emittedKey
                        AViewModel_NewProtoPatterns.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = emptyList()
                    }
                }
            }
        }
    }
}
