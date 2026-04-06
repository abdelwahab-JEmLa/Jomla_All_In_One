package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M2Client
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class Initializer_ViewModel(private val AViewModel_NewProtoPatterns: A_ViewModel_NewProtoPatterns) {
    fun run() {
        collect_ListDatas()
        load_then_Collect_Active_DatasMutableStates()
    }
    private fun collect_ListDatas() {
        load_then_Collect_Active_Datas()
        collectListM16()
        collectListM1Produit()
        collectList_M3()
        collectList_M8BonVent()
        collectList_M2Client()
        collectList_M10OperationVentCouleur_All()
    }

    private fun load_then_Collect_Active_DatasMutableStates() {
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
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                    initDatasProgressEtate = p
                )
        }
        progress(1 / 9f)
        val products = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit().getAll()
        progress(2 / 9f)
        val colours = AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAll()
        progress(3 / 9f)
        val clients = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
        progress(4 / 9f)
        val categories = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAll()
        progress(5 / 9f)
        val appCompt =
            AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().getBy_M00_Lence_Key_Flow()
                .first()
        progress(6 / 9f)
        val bonVent = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
        progress(7 / 9f)
        val ventPeriodes = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
        progress(8 / 9f)
        val tarification =
            AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
        val operationVentCouleurs =
            AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()

        seedActiveDatas(
            appCompt = appCompt,
            bonVent = bonVent,
            clients = clients,
            categories = categories,
            products = products,
            colours = colours,
            allOperations = operationVentCouleurs,
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
        appCompt: M09AppCompt,
        bonVent: List<M8BonVent>,
        clients: List<M2Client>,
        categories: List<M16CategorieProduit>,
        products: List<M01Produit>,
        colours: List<M3CouleurProduitInfos>,
        allOperations: List<EntreApps.Shared.Models.M10OperationVentCouleur>,
    ) {
        AViewModel_NewProtoPatterns.active_Datas.active_M9Compt = appCompt
        AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = bonVent
        AViewModel_NewProtoPatterns.active_Datas.list_M2Client = clients
        AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit = categories
        AViewModel_NewProtoPatterns.active_Datas.list_M1Produit = products
        AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = colours
        AViewModel_NewProtoPatterns.active_Datas.list_M10OperationVentCouleur = allOperations
        AViewModel_NewProtoPatterns.active_Datas.its_Panie_Mode = appCompt.its_Panie_Mode_Au_Lence_Boutique
    }

    private suspend fun collectActiveM9Compt() {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt(),
            activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
        ).collect { }
    }

    private fun collectListM16() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit = it
                }
        }
    }

    private fun collectListM1Produit() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_list_M1Produit(
                dao_M1Produit = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit(),
                activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
            ).collect { }
        }
    }

    private fun collectList_M3() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos = it
                }
        }
    }

    private fun collectList_M8BonVent() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent = it
                }
        }
    }

    private fun collectList_M2Client() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M2Client = it
                }
        }
    }

    /** Keeps active_Datas.list_M10OperationVentCouleur in sync with the Room table.
     *  This powers the Echatillants filter which needs ALL operations, not just the active bon. */
    private fun collectList_M10OperationVentCouleur_All() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAllFlow()
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_M10OperationVentCouleur = it
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
                        AViewModel_NewProtoPatterns.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
                            filtered
                    }

                    emittedKey == null -> Unit
                    emittedKey != AViewModel_NewProtoPatterns.active_Datas.lastKnownBonVentKey -> {
                        AViewModel_NewProtoPatterns.active_Datas.lastKnownBonVentKey = emittedKey
                        AViewModel_NewProtoPatterns.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
                            emptyList()
                    }
                }
            }
        }
    }
}
