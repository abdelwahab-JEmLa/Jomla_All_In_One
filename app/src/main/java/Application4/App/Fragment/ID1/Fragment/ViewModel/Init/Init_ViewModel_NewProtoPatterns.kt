package Application4.App.Fragment.ID1.Fragment.ViewModel.Init

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.ActiveDatasFragNewProtoFlows
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.List_Datas
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG_VM_INIT = "ViewModel_NewProto"

// FIX TODO(1): extracted from ViewModel_NewProtoPatterns.init so each coroutine
// launch lives here and the ViewModel constructor stays readable.
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
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 1 / 9f)
        val clients = appDatabase.dao_M2Client().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 2 / 9f)
        val categories = appDatabase.dao_16CategorieProduit().getAll()
            .filter { it.catalogueParentId == active_Datas.active_M21Catalogue.id }
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 3 / 9f)
        val colors = appDatabase.dao_M3CouleurProduitInfos().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 4 / 9f)
        val appCompt = appDatabase.dao_M9AppCompt().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 5 / 9f)
        val bonVent = appDatabase.dao_M8BonVent().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 6 / 9f)
        val ventPeriodes = appDatabase.dao_M14VentPeriode().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 7 / 9f)
        val tarification = appDatabase.dao_M13TarificationInfos().getAll()
        _uiStateNewProtoPatterns.value =
            _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = 8 / 9f)
        val operationVentCouleurs = appDatabase.dao_M10OperationVentCouleur().getAll()
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
        active_Datas.get_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
            dao_M9AppCompt = appDatabase.dao_M9AppCompt()
        ).collect()
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectListM16FilteredByCatalogue() {
    viewModelScope.launch(Dispatchers.IO) {
        ActiveDatasFragNewProtoFlows.getFlow_listM16_FilteredBy_active_M21Catalogue(
            dao_M16CategorieProduit = appDatabase.dao_16CategorieProduit(),
            active_M21Catalogue = active_Datas.active_M21Catalogue
        ).collect { filteredCategories ->
            active_Datas.listM16_FilteredBy_active_M21Catalogue = filteredCategories
        }
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectListM1Produit() {
    viewModelScope.launch(Dispatchers.IO) {
        ActiveDatasFragNewProtoFlows.get_list_M1Produit(
            dao_M1Produit = appDatabase.dao_M1Produit(),
            activeDatasFragNewProto = active_Datas,
        ).collect()
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectFilteredProductTree() {
    viewModelScope.launch(Dispatchers.IO) {
        _uiStateNewProtoPatterns.collect { state ->
            val allColours = state.list_Datas?.m3CouleurProduit ?: return@collect
            ActiveDatasFragNewProtoFlows.getFlow_list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur(
                dao_M16CategorieProduit = appDatabase.dao_16CategorieProduit(),
                activeCatalogue = active_Datas.active_M21Catalogue,
                allColours = allColours,
                activeDatasFragNewProto = active_Datas,
            ).collect { tree ->
                active_Datas.list_filter_Priorite_M21Catalogues_To_M16Categories_To_M1Products_To_M03Couleur =
                    tree
            }
        }
    }
}

private fun ViewModel_NewProtoPatterns.subInit_collectM10OperationVentCouleur() {
    viewModelScope.launch(Dispatchers.IO) {
        ActiveDatasFragNewProtoFlows.getFlow_listM10OperationVentCouleur_By_active_Central_Values(
            dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur(),
            dao_M9AppCompt = appDatabase.dao_M9AppCompt()
        ).collect { (emittedKey, filtered) ->
            Log.d(TAG_VM_INIT, "M10OperationVentCouleur flow emitted: key=$emittedKey, count=${filtered.size}")
            when {
                filtered.isNotEmpty() -> {
                    Log.d(TAG_VM_INIT, "  → storing ${filtered.size} operations for bonVentKey=$emittedKey")
                    active_Datas.lastKnownBonVentKey = emittedKey
                    active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
                        filtered
                }
                emittedKey == null -> {
                    Log.d(TAG_VM_INIT, "  → emittedKey=null, no active bon de vente yet — skipping")
                }
                emittedKey != active_Datas.lastKnownBonVentKey -> {
                    Log.d(TAG_VM_INIT, "  → new bonVentKey detected ($emittedKey), resetting operations list")
                    active_Datas.lastKnownBonVentKey = emittedKey
                    active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
                        emptyList()
                }
                else -> {
                    Log.d(TAG_VM_INIT, "  → filtered is empty but key matches lastKnown — no update")
                }
            }
        }
    }
}
