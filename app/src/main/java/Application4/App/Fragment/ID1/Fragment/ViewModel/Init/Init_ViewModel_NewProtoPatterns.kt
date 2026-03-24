package Application4.App.Fragment.ID1.Fragment.ViewModel.Init

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Archive.List_Datas
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.FlowsFunctions_ActiveDatasFragNewProto
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
       //<--
       //TODO(1): //TODO(1): fait warp que ca soit sub classe ini in vm 
fun ViewModel_NewProtoPatterns.subInit() {
    subInit_collectCentralValues()
    subInit_loadAllDatasOnce()
    subInit_collectActiveM9Compt()
    subInit_collectListM16FilteredByCatalogue()
    subInit_collectListM1Produit()
    subInit_collectFilteredProductTree()
    subInit_collectM10OperationVentCouleur()
}

private fun ViewModel_NewProtoPatterns.subInit_collectCentralValues() {
    viewModelScope.launch(Dispatchers.IO) {
        focusedValues_NewProtoPatterns.active_Central_Values.collect { centralValues ->
            _uiStateNewProtoPatterns.value =
                _uiStateNewProtoPatterns.value.copy(active_Central_Values = centralValues)
        }
    }
}

private fun ViewModel_NewProtoPatterns.subInit_loadAllDatasOnce() {
    viewModelScope.launch(Dispatchers.IO) {
        fun progress(p: Float) {
            _uiStateNewProtoPatterns.value =
                _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
        }
        progress(1 / 9f); val clients = appDatabase.dao_M2Client().getAll()
        progress(2 / 9f); val categories = appDatabase.dao_16CategorieProduit().getAll()
        .filter { it.catalogueParentId == active_Datas.active_M21Catalogue.id }
        progress(3 / 9f); val colors = appDatabase.dao_M3CouleurProduitInfos().getAll()
        progress(4 / 9f); val appCompt = appDatabase.dao_M9AppCompt().getAll()
        progress(5 / 9f); val bonVent = appDatabase.dao_M8BonVent().getAll()
        progress(6 / 9f); val ventPeriodes = appDatabase.dao_M14VentPeriode().getAll()
        progress(7 / 9f); val tarification = appDatabase.dao_M13TarificationInfos().getAll()
        progress(8 / 9f); val operationVentCouleurs = appDatabase.dao_M10OperationVentCouleur().getAll()
        _uiStateNewProtoPatterns.value = _uiStateNewProtoPatterns.value.copy(
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

private fun ViewModel_NewProtoPatterns.subInit_collectActiveM9Compt() {
    viewModelScope.launch(Dispatchers.IO) {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt = appDatabase.dao_M9AppCompt(),
            activeDatasFragNewProto = active_Datas,
        ).collect()
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectListM16FilteredByCatalogue() {
    viewModelScope.launch(Dispatchers.IO) {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_listM16_FilteredBy_active_M21Catalogue(
            dao_M16CategorieProduit = appDatabase.dao_16CategorieProduit(),
            active_M21Catalogue = active_Datas.active_M21Catalogue
        ).collect { active_Datas.listM16_FilteredBy_active_M21Catalogue = it }
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectListM1Produit() {
    viewModelScope.launch(Dispatchers.IO) {
        FlowsFunctions_ActiveDatasFragNewProto.get_list_M1Produit(
            dao_M1Produit = appDatabase.dao_M1Produit(),
            activeDatasFragNewProto = active_Datas,
        ).collect()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun ViewModel_NewProtoPatterns.subInit_collectFilteredProductTree() {
    viewModelScope.launch(Dispatchers.Main) {
        snapshotFlow { active_Datas.affiche_produits_Ou_On_TagPrioriter }
            .flatMapLatest { currentFilter ->
                kotlinx.coroutines.flow.channelFlow {
                    _uiStateNewProtoPatterns.collectLatest { state ->
                        val allColours = state.list_Datas?.m3CouleurProduit ?: return@collectLatest
                        FlowsFunctions_ActiveDatasFragNewProto
                            .getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
                                dao_M16CategorieProduit = appDatabase.dao_16CategorieProduit(),
                                activeCatalogue = active_Datas.active_M21Catalogue,
                                allColours = allColours,
                                activeDatasFragNewProto = active_Datas,
                                activeFilter = currentFilter,
                            ).collect { send(it) }
                    }
                }
            }
            .collect {
                active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur = it
            }
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectM10OperationVentCouleur() {
    viewModelScope.launch(Dispatchers.IO) {
        FlowsFunctions_ActiveDatasFragNewProto.getFlow_listM10OperationVentCouleur_By_active_Central_Values(
            dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur(),
            dao_M9AppCompt = appDatabase.dao_M9AppCompt()
        ).collect { (emittedKey, filtered) ->
            when {
                filtered.isNotEmpty() -> {
                    active_Datas.lastKnownBonVentKey = emittedKey
                    active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = filtered
                }
                emittedKey == null -> Unit
                emittedKey != active_Datas.lastKnownBonVentKey -> {
                    active_Datas.lastKnownBonVentKey = emittedKey
                    active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = emptyList()
                }
            }
        }
    }
}
