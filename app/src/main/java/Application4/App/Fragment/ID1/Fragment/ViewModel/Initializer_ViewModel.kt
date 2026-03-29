package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
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
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
        }

        // FIX: M1Produit and M3CouleurProduitInfos are now loaded here (steps 1 & 2) so that
        // active_Datas is populated BEFORE initDatasProgressEtate reaches 1f.
        // Previously they were only fed by separate collect* coroutines that hadn't emitted yet
        // when the grid rendered, resulting in both lists being NULL at first composition.
        progress(1 / 9f)
        val products  = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit().getAll()
        progress(2 / 9f)
        val colours   = AViewModel_NewProtoPatterns.appDatabase.dao_M03CouleurProduitInfos().getAll()
        progress(3 / 9f)
        val clients   = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
        progress(4 / 9f)
        val categories = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAll()
        progress(5 / 9f)
        val appCompt  = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().getBy_M00_Lence_Key_Flow().first()
        progress(6 / 9f)
        val bonVent   = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
        progress(7 / 9f)
        val ventPeriodes   = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
        progress(8 / 9f)
        val tarification   = AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
        val operationVentCouleurs = AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()

        // Seed active_Datas — M1 and M3 are now included so the grid has data immediately
        seedActiveDatas(
            appCompt   = appCompt,
            bonVent    = bonVent,
            clients    = clients,
            categories = categories,
            products   = products,
            colours    = colours,
        )

        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                initDatasProgressEtate = 1f,
                list_Datas = List_Datas(
                    m2Client             = clients,
                    m14VentPeriode       = ventPeriodes,
                    m16CategorieProduit  = categories,
                    m8BonVent            = bonVent,
                    m13TarificationInfos = tarification,
                    m10OperationVentCouleur = operationVentCouleurs,
                )
            )
    }

    private fun seedActiveDatas(
        appCompt: M09AppCompt,
        bonVent: List<M8BonVent>,
        clients: List<M2Client>,
        categories: List<EntreApps.Shared.Models.M16CategorieProduit>,
        products: List<EntreApps.Shared.Models.M01Produit>,
        colours: List<EntreApps.Shared.Models.M3CouleurProduitInfos>,
    ) {
        AViewModel_NewProtoPatterns.active_Datas.active_M9Compt          = appCompt
        AViewModel_NewProtoPatterns.active_Datas.list_M8BonVent           = bonVent
        AViewModel_NewProtoPatterns.active_Datas.list_M2Client            = clients
        AViewModel_NewProtoPatterns.active_Datas.list_M16CategorieProduit = categories
        // Seed M1 & M3 so the grid is populated as soon as the loading gate opens
        AViewModel_NewProtoPatterns.active_Datas.list_M1Produit               = products
        AViewModel_NewProtoPatterns.active_Datas.list_M03CouleurProduitInfos   = colours
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
