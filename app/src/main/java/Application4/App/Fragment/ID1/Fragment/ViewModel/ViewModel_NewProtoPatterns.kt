package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application2.App.Base.Modules.ProductDisplayController
import Application2.App.Base.Modules.WifiTransferDatas
import Application2.App.Base.Modules.WifiUpdateClientDisplayerStats_app2
import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_NewProto
import Application4.App.Modules.Wi.Module.WifiUpdateClientDisplayerStats_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Home.CentraleMainGetter_NewProtoPattern
import EntreApps.Shared.Models.Home.FocusedValues_NewProtoPatterns
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState_NewProtoPatterns(
    val grpList_cataloguesWithCategoriesAndProducts: List<Pair<M21CataloguesCategorie, List<Pair<M16CategorieProduit, List<Pair<M01Produit, List<M3CouleurProduitInfos>>>>>>> = emptyList(),
    val active_Central_Values: ActiveCentralValues = ActiveCentralValues.get_Default(),
    val list_Datas: List_Datas? = null,
    val initDatasProgressEtate: Float = 0f,
) {
    val list_M1Produit: List<M01Produit> get() = list_Datas?.m1Produit ?: emptyList()
    val list_M16CategorieProduit: List<M16CategorieProduit> get() = list_Datas?.m16CategorieProduit ?: emptyList()
    val list_M3CouleurProduit: List<M3CouleurProduitInfos> get() = list_Datas?.m3CouleurProduit ?: emptyList()
    val list_M13TarificationInfos: List<M13TarificationInfos> get() = list_Datas?.m13TarificationInfos ?: emptyList()
}

data class List_Datas(
    val m1Produit: List<M01Produit> = emptyList(),
    val m2Client: List<M2Client> = emptyList(),
    val m14VentPeriode: List<M14VentPeriode> = emptyList(),
    val m16CategorieProduit: List<M16CategorieProduit> = emptyList(),
    val m3CouleurProduit: List<M3CouleurProduitInfos> = emptyList(),
    val m9AppCompt: List<Z_AppCompt> = emptyList(),
    val m8BonVent: List<M8BonVent> = emptyList(),
    val m10OperationVentCouleur: List<M10OperationVentCouleur> = emptyList(),
    val m13TarificationInfos: List<M13TarificationInfos> = emptyList(),
)

class ActiveDatasFragNewProto {
    var listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state: List<M10OperationVentCouleur>?
            by mutableStateOf(null)

    var lastKnownBonVentKey: String? = null

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun get_listM10OperationVentCouleur_By_active_Central_Values(
            dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
            dao_M9AppCompt: Dao_M9AppCompt,
        ): Flow<Pair<String?, List<M10OperationVentCouleur>>> =
            dao_M9AppCompt.getFlow_ByKeyID(
                M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            ).flatMapLatest { activeCompt ->
                val onVentKey = activeCompt?.onVentM8BonVentKey
                    ?.takeIf { it.isNotBlank() && it != "null" }

                if (onVentKey == null) flowOf(null to emptyList())
                else dao_M10OperationVentCouleur
                    .getFlow_ListM10OperationVentCouleur_Of_Active_M8Bon_Key(onVentKey)
                    .map { list ->
                        val filtered = list.filter { it.parent_M8BonVent_KeyId == onVentKey }

                        onVentKey to filtered
                    }
            }
    }
}

@SuppressLint("StaticFieldLeak")
class ViewModel_NewProtoPatterns(
    private val context: Context,
    val appDatabase: AppDatabase,
    private val fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    val repositorysMainSetter_NewProtoPatterns: RepositorysMainSetter_NewProtoPatterns = RepositorysMainSetter_NewProtoPatterns(
        appDatabase = appDatabase,
        context = context
    ),
) : ViewModel() {
    val _uiStateNewProtoPatterns = MutableStateFlow(UiState_NewProtoPatterns())
    val uiState = _uiStateNewProtoPatterns.asStateFlow()
    val active_Datas = ActiveDatasFragNewProto()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeOnVent_M8BonVent_flow: StateFlow<M8BonVent?> by lazy {
        appDatabase.dao_M9AppCompt()
            .getFlow_ByKeyID(M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId)
            .flatMapLatest { activeCompt ->
                val onVentKey = activeCompt?.onVentM8BonVentKey
                    ?.takeIf { it.isNotBlank() && it != "null" }
                if (onVentKey == null) flowOf(null)
                else appDatabase.dao_M8BonVent().getFlow_ByKeyID(onVentKey)
            }
            .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)
    }

    val focusedValues_NewProtoPatterns: FocusedValues_NewProtoPatterns =
        FocusedValues_NewProtoPatterns(
            list_Datas = _uiStateNewProtoPatterns.map { it.list_Datas }
                .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)
        )

    val centraleMainGetter_NewProtoPattern: CentraleMainGetter_NewProtoPattern =
        CentraleMainGetter_NewProtoPattern(
            context, appDatabase,
            on_Progress_Datas = { progress ->
                _uiStateNewProtoPatterns.update { state ->
                    state.copy(
                        initDatasProgressEtate = progress,
                        active_Central_Values = state.active_Central_Values.copy(mainInitDataBaseProgressEtate = progress)
                    )
                }
            }
        )

    private fun getActiveCentralValues(): ActiveCentralValues_app2 {
        val cv = _uiStateNewProtoPatterns.value.active_Central_Values
        return ActiveCentralValues_app2(
            expanded_M3CouleurProduitInfos = cv.expanded_M3CouleurProduitInfos,
            expanded_M1Produit = cv.expanded_M1Produit,
            hide_prix_lence_vent_buttons = cv.hide_prix_lence_vent_buttons,
        )
    }

    private fun updateActiveCentralValues(updated: ActiveCentralValues_app2) =
        update_activeCentralValues(_uiStateNewProtoPatterns.value.active_Central_Values.copy(
            expanded_M3CouleurProduitInfos = updated.expanded_M3CouleurProduitInfos,
            expanded_M1Produit = updated.expanded_M1Produit,
            hide_prix_lence_vent_buttons = updated.hide_prix_lence_vent_buttons,
        ))

    val wifi = WifiTransferDatas_NewProto(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
        onGetActiveCentralValues = ::getActiveCentralValues,
        onUpdateActiveCentralValues = ::updateActiveCentralValues,
    )

    val wifiState = wifi.state.stateIn(viewModelScope, SharingStarted.Eagerly,
        ProductDisplayController_NewProto())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() { wifi.startAsClient(); wifi.updateTypePhone(isHost = false) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() { wifi.startAsHost(); wifi.updateTypePhone(isHost = true) }

    fun disconnect() = wifi.disconnect()
    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) = wifi.sendOrderToClientDisplayer(orderName, data)
    fun sendOrderToClientDisplayerT(order: WifiUpdateClientDisplayerStats_NewProto, data: Any? = null) = wifi.sendOrderToClientDisplayerT(order, data)

    init {
        fragmentNavigationHandler.closeAllActiveFragments()

        viewModelScope.launch(Dispatchers.IO) {
            focusedValues_NewProtoPatterns.active_Central_Values.collect { centralValues ->
                _uiStateNewProtoPatterns.update { it.copy(active_Central_Values = centralValues) }
            }
        }

        centraleMainGetter_NewProtoPattern

        viewModelScope.launch(Dispatchers.IO) {
            val products          = appDatabase.dao_M1Produit().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 1 / 9f) }
            val clients           = appDatabase.dao_M2Client().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 2 / 9f) }
            val categories        = appDatabase.dao_16CategorieProduit().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 3 / 9f) }
            val colors            = appDatabase.dao_M3CouleurProduitInfos().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 4 / 9f) }
            val appCompt          = appDatabase.dao_M9AppCompt().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 5 / 9f) }
            val bonVent           = appDatabase.dao_M8BonVent().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 6 / 9f) }
            val ventPeriodes      = appDatabase.dao_M14VentPeriode().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 7 / 9f) }
            val tarification      = appDatabase.dao_M13TarificationInfos().getAll()
            _uiStateNewProtoPatterns.update { it.copy(initDatasProgressEtate = 8 / 9f) }
            val operationVentCouleurs = appDatabase.dao_M10OperationVentCouleur().getAll()
            _uiStateNewProtoPatterns.update {
                it.copy(
                    initDatasProgressEtate  = 1f,
                    list_Datas = List_Datas(
                        m1Produit               = products,
                        m2Client                = clients,
                        m14VentPeriode          = ventPeriodes,
                        m16CategorieProduit     = categories,
                        m3CouleurProduit        = colors,
                        m9AppCompt              = appCompt,
                        m8BonVent               = bonVent,
                        m13TarificationInfos    = tarification,
                        m10OperationVentCouleur = operationVentCouleurs,
                    )
                )
            }
        }

        viewModelScope.launch(Dispatchers.Main) {

            ActiveDatasFragNewProto.get_listM10OperationVentCouleur_By_active_Central_Values(
                dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur(),
                dao_M9AppCompt = appDatabase.dao_M9AppCompt()
            ).collect { (emittedKey, filtered) ->

                when {

                    filtered.isNotEmpty() -> {
                        active_Datas.lastKnownBonVentKey = emittedKey
                        active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = filtered

                    }

                    emittedKey == null -> {

                    }

                    emittedKey != active_Datas.lastKnownBonVentKey -> {
                        active_Datas.lastKnownBonVentKey = emittedKey
                        active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = emptyList()

                    }

                    else -> {

                    }
                }
            }
        }
    }

    fun update_M13TarificationInfos(tariff: M13TarificationInfos) {
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m13TarificationInfos = current.m13TarificationInfos.filter { it.keyID != tariff.keyID } + tariff
            ))
        }
        repositorysMainSetter_NewProtoPatterns.update_M13TarificationInfos(tariff)
    }

    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): Boolean {
        val centralValues  = _uiStateNewProtoPatterns.value.active_Central_Values
        val currentBonVent = centralValues.activeOnVent_M8BonVent
        val isGrossist     = centralValues.activeCompt?.travailleChezGrossisst3Ali == true

        if (isGrossist || currentBonVent == null || synthetic == null) return false
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) return false

        val currentClient  = centralValues.activeOnVent_M2Client
        val clientBonVents = centralValues.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            ?.filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            ?.sortedByDescending { it.creationTimestamps } ?: emptyList()

        if (clientBonVents.firstOrNull()?.keyID != currentBonVent.keyID) return false
        if (System.currentTimeMillis() - currentBonVent.creationTimestamps >= 5 * 60 * 1000) return false

        update_M13TarificationInfos(synthetic.copy(
            parent_M8BonVent_KeyId                       = currentBonVent.keyID,
            parent_M8BonVent_DebugInfos                  = currentBonVent.get_DebugInfos(),
            parent_M2Client_KeyId                        = currentClient?.keyID ?: "null",
            parent_M2Client_DebugInfos                   = currentClient?.nom ?: "null",
            creationTimestamps                           = System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase  = System.currentTimeMillis()
        ))
        return true
    }

    fun updateTariffForProductOperations(produitKeyID: String, newTariff: M13TarificationInfos) =
        repositorysMainSetter_NewProtoPatterns.updateTariffForProductOperations(produitKeyID, newTariff)

    fun setActiveFocuceTariffPrixDifineur(produit: M01Produit, appCompt: Z_AppCompt) =
        repositorysMainSetter_NewProtoPatterns.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(produit, appCompt)

    fun update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(updatedList: List<M10OperationVentCouleur>?) {

        active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state = updatedList

        upsert_M10OperationVentCouleur(updatedList)
    }

    private fun upsert_M10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) {
        val allTariffs = _uiStateNewProtoPatterns.value.list_Datas?.m13TarificationInfos
        updatedList?.forEach { operation ->

            val tariff = allTariffs?.find { it.keyID == operation.parentM13TarificationKeyID }
                ?: allTariffs?.filter { it.parent_M1Produit_KeyId == operation.parent_M1Produit_KeyId }
                    ?.maxByOrNull { it.creationTimestamps }
            if (tariff == null) {

                return@forEach
            }
            val opToSave = if (tariff.keyID != operation.parentM13TarificationKeyID) {

                operation.copy(parentM13TarificationKeyID = tariff.keyID, parentM13TarificationDebugInfos = tariff.getDebugInfos())
            } else {

                operation
            }
            repositorysMainSetter_NewProtoPatterns.upsert_M10OperationVentCouleur(opToSave, tariff)
        }
    }

    fun update_m3couleur(couleur: M3CouleurProduitInfos) {
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m3CouleurProduit = current.m3CouleurProduit.map { if (it.keyID == couleur.keyID) couleur else it }
            ))
        }
        repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(couleur)
    }

    fun update_depot_count(couleur: M3CouleurProduitInfos, newDepotCount: Int, onSuccess: () -> Unit = {}) {
        val updated = couleur.copy(
            count_Don_Depot = newDepotCount,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m3CouleurProduit = current.m3CouleurProduit.map { if (it.keyID == updated.keyID) updated else it }
            ))
        }
        repositorysMainSetter_NewProtoPatterns.update_M3CouleurProduitInfos(data = updated, onSuccess = onSuccess)
    }

    fun update_m16CategorieProduit(new: M16CategorieProduit) {
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m16CategorieProduit = current.m16CategorieProduit.map { if (it.keyID == new.keyID) new else it }
            ))
        }
        repositorysMainSetter_NewProtoPatterns.update_M16CategorieProduit(new)
    }

    fun update_m1Produit(new: M01Produit) {
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m1Produit = current.m1Produit.map { if (it.keyID == new.keyID) new else it }
            ))
        }
        repositorysMainSetter_NewProtoPatterns.update_M1Produit(new)
    }

    fun delete_m1Produit(produit: M01Produit) {
        _uiStateNewProtoPatterns.update { state ->
            val current = state.list_Datas ?: List_Datas()
            state.copy(list_Datas = current.copy(
                m1Produit = current.m1Produit.filter { it.keyID != produit.keyID }
            ))
        }
        repositorysMainSetter_NewProtoPatterns.delete_M1Produit(produit)
    }

    fun update_activeCentralValues(new: ActiveCentralValues) =
        focusedValues_NewProtoPatterns.update_activeCentralValues(new)

    override fun onCleared() = super.onCleared()
}
