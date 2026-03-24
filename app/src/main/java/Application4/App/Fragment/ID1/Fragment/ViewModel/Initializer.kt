package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

// -------------------------------------------------------------------------
// Inner initializer — replaces the external subInit extension functions
// -------------------------------------------------------------------------
class Initializer(private val AViewModel_NewProtoPatterns: A_ViewModel_NewProtoPatterns) {
    fun run() {
        collectCentralValues()
        loadAllDatasOnce()
        collectActiveM9Compt()
        collectListM16FilteredByCatalogue()
        collectListM1Produit()
        collectFilteredProductTree()
        collectM10OperationVentCouleur()
    }

    private fun collectCentralValues() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            AViewModel_NewProtoPatterns.focusedValues_NewProtoPatterns.active_Central_Values.collect { centralValues ->
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                    AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(active_Central_Values = centralValues)
            }
        }
    }

    private fun loadAllDatasOnce() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            fun progress(p: Float) {
                AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value =
                    AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
            }
            progress(1 / 9f); val clients = AViewModel_NewProtoPatterns.appDatabase.dao_M2Client().getAll()
            progress(2 / 9f); val categories = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit().getAll()
            .filter { it.catalogueParentId == AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue.id }
            progress(3 / 9f); val colors = AViewModel_NewProtoPatterns.appDatabase.dao_M3CouleurProduitInfos().getAll()
            progress(4 / 9f); val appCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt().getAll()
            progress(5 / 9f); val bonVent = AViewModel_NewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
            progress(6 / 9f); val ventPeriodes = AViewModel_NewProtoPatterns.appDatabase.dao_M14VentPeriode().getAll()
            progress(7 / 9f); val tarification = AViewModel_NewProtoPatterns.appDatabase.dao_M13TarificationInfos().getAll()
            progress(8 / 9f); val operationVentCouleurs = AViewModel_NewProtoPatterns.appDatabase.dao_M10OperationVentCouleur().getAll()
            AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value = AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                initDatasProgressEtate = 1f,
                list_Datas = List_Datas(
                    m2Client = clients,
                    m14VentPeriode = ventPeriodes,
                    m16CategorieProduit = categories,
                    m3CouleurProduit = colors,
                    m9AppCompt = appCompt,
                    m8BonVent = bonVent,
                    m13TarificationInfos = tarification,
                    m10OperationVentCouleur = operationVentCouleurs,
                )
            )
        }
    }

    private fun collectActiveM9Compt() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
                dao_M9AppCompt = AViewModel_NewProtoPatterns.appDatabase.dao_M9AppCompt(),
                activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
            ).collect()
        }
    }

    private fun collectListM16FilteredByCatalogue() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_listM16_FilteredBy_active_M21Catalogue(
                dao_M16CategorieProduit = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit(),
                active_M21Catalogue = AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue
            ).collect { AViewModel_NewProtoPatterns.active_Datas.listM16_FilteredBy_active_M21Catalogue = it }
        }
    }

    private fun collectListM1Produit() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.get_list_M1Produit(
                dao_M1Produit = AViewModel_NewProtoPatterns.appDatabase.dao_M1Produit(),
                activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
            ).collect()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectFilteredProductTree() {
        AViewModel_NewProtoPatterns.viewModelScope.launch(Dispatchers.Main) {
            snapshotFlow { AViewModel_NewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter }
                .flatMapLatest { currentFilter ->
                    channelFlow {
                        AViewModel_NewProtoPatterns._uiStateNewProtoPatterns.collectLatest { state ->
                            val allColours =
                                state.list_Datas?.m3CouleurProduit ?: return@collectLatest
                            FlowsFunctions_ActiveDatasFragNewProto
                                .getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
                                    dao_M16CategorieProduit = AViewModel_NewProtoPatterns.appDatabase.dao_16CategorieProduit(),
                                    activeCatalogue = AViewModel_NewProtoPatterns.active_Datas.active_M21Catalogue,
                                    allColours = allColours,
                                    activeDatasFragNewProto = AViewModel_NewProtoPatterns.active_Datas,
                                    activeFilter = currentFilter,
                                ).collect { send(it) }
                        }
                    }
                }
                .collect {
                    AViewModel_NewProtoPatterns.active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur = it
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
