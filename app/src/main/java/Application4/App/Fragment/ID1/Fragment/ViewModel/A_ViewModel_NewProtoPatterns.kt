package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application4.App.A.Start.Init.Initializer_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_NewProto
import Application4.App.Modules.Wi.Module.WifiUpdateClientDisplayerStats_NewProto
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Home.FocusedValues_NewProtoPatterns
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@SuppressLint("StaticFieldLeak")
class A_ViewModel_NewProtoPatterns(
    private val context: Context,
    val appDatabase: AppDatabase,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    val repositorysMainSetter_NewProtoPatterns: RepositorysMainSetter_NewProtoPatterns = RepositorysMainSetter_NewProtoPatterns(
        appDatabase = appDatabase,
        context = context
    ),
) : ViewModel() {
    val active_Datas = ActiveDatasFragNewProto()
    private val updater = Setter_ViewModel_NewProtoPatterns(this)

    val _uiStateNewProtoPatterns = MutableStateFlow(UiState_NewProtoPatterns())
    val uiState = _uiStateNewProtoPatterns.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeOnVent_M8BonVent_flow: StateFlow<M8BonVent?> by lazy {
        appDatabase.dao_M9AppCompt()
            .getFlow_ByKeyID(M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId)
            .flatMapLatest { activeCompt ->
                val onVentKey =
                    activeCompt?.onVentM8BonVentKey?.takeIf { it.isNotBlank() && it != "null" }
                if (onVentKey == null) flowOf(null)
                else appDatabase.dao_M8BonVent().getFlow_ByKeyID(onVentKey)
            }
            .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)
    }

    val focusedValues_NewProtoPatterns: FocusedValues_NewProtoPatterns =
        FocusedValues_NewProtoPatterns(
            list_Datas = _uiStateNewProtoPatterns.map { it.list_Datas }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = null
                )
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
        update_activeCentralValues(
            focusedValues_NewProtoPatterns.active_Central_Values.value.copy(
                expanded_M3CouleurProduitInfos = updated.expanded_M3CouleurProduitInfos,
                expanded_M1Produit = updated.expanded_M1Produit,
                hide_prix_lence_vent_buttons = updated.hide_prix_lence_vent_buttons,
            )
        )

    val wifi = WifiTransferDatas_NewProto(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
        onGetActiveCentralValues = ::getActiveCentralValues,
        onUpdateActiveCentralValues = ::updateActiveCentralValues,
    )

    val wifiState = wifi.state.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ProductDisplayController_NewProto()
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() {
        wifi.startAsClient(); wifi.updateTypePhone(isHost = false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() {
        wifi.startAsHost(); wifi.updateTypePhone(isHost = true)
    }

    fun disconnect() = wifi.disconnect()

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) =
        wifi.sendOrderToClientDisplayer(orderName, data)

    fun sendOrderToClientDisplayerT(
        order: WifiUpdateClientDisplayerStats_NewProto,
        data: Any? = null
    ) =
        wifi.sendOrderToClientDisplayerT(order, data)

    init {
        fragmentNavigationHandler.closeAllActiveFragments()
        viewModelScope.launch {
            M00CentralParametresOfAllApps.get_Default().load_Initializer_App4.ifTrue {
                Initializer_App4.initializeAllRepositories(
                    context = context,
                    appDatabase = appDatabase,
                    on_Progress_Datas = { progress ->
                        _uiStateNewProtoPatterns.update { state ->
                            state.copy(
                                initDatasProgressEtate = progress,
                                active_Central_Values = state.active_Central_Values.copy(
                                    mainInitDataBaseProgressEtate = progress
                                )
                            )
                        }
                    },
                    callerScope = viewModelScope,
                )
            }
        }
        Initializer(this).run()
    }

    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): Boolean {
        val centralValues = _uiStateNewProtoPatterns.value.active_Central_Values
        val currentBonVent = centralValues.activeOnVent_M8BonVent
        val isGrossist = centralValues.activeCompt?.travailleChezGrossisst3Ali == true

        if (isGrossist || currentBonVent == null || synthetic == null) return false
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) return false

        val currentClient = centralValues.activeOnVent_M2Client
        val clientBonVents = centralValues.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            ?.filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            ?.sortedByDescending { it.creationTimestamps } ?: emptyList()

        if (clientBonVents.firstOrNull()?.keyID != currentBonVent.keyID) return false
        if (System.currentTimeMillis() - currentBonVent.creationTimestamps >= 5 * 60 * 1000) return false

        update_M13TarificationInfos(
            synthetic.copy(
                parent_M8BonVent_KeyId = currentBonVent.keyID,
                parent_M8BonVent_DebugInfos = currentBonVent.get_DebugInfos(),
                parent_M2Client_KeyId = currentClient?.keyID ?: "null",
                parent_M2Client_DebugInfos = currentClient?.nom ?: "null",
                creationTimestamps = System.currentTimeMillis(),
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        )
        return true
    }

    //────────────Setter_ViewModel_NewProtoPatterns─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    fun update_m1Produit(new: M01Produit) = updater.update_m1Produit(new)
    fun delete_m1Produit(produit: M01Produit) = updater.delete_m1Produit(produit)
    fun update_activeCentralValues(new: ActiveCentralValues) =
        focusedValues_NewProtoPatterns.update_activeCentralValues(new)

    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) =
        updater.deleteInsertFireBase_listKeys_M3CouleurProduitInfos(keys, onSuccess)

    fun updateTariffForProductOperations(produitKeyID: String, newTariff: M13TarificationInfos) =
        repositorysMainSetter_NewProtoPatterns.updateTariffForProductOperations(
            produitKeyID,
            newTariff
        )

    fun setActiveFocuceTariffPrixDifineur(produit: M01Produit, appCompt: Z_AppCompt) =
        repositorysMainSetter_NewProtoPatterns.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            produit,
            appCompt
        )

    fun update_active_Compt(compt: Z_AppCompt) = updater.update_active_Compt(compt)
    fun update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(updatedList: List<M10OperationVentCouleur>?) =
        updater.update_listM10OperationVentCouleur_FilteredBy_activeM8BonVent(updatedList)

    fun update_m3couleur(couleur: M3CouleurProduitInfos) = updater.update_m3couleur(couleur)
    fun update_depot_count(
        couleur: M3CouleurProduitInfos,
        newDepotCount: Int,
        onSuccess: () -> Unit = {}
    ) =
        updater.update_depot_count(couleur, newDepotCount, onSuccess)

    fun update_M13TarificationInfos(tariff: M13TarificationInfos) =
        updater.update_M13TarificationInfos(tariff)

    fun insert_M16CategorieProduit(new: M16CategorieProduit) =
        updater.insert_M16CategorieProduit(new)

    fun update_m16CategorieProduit(new: M16CategorieProduit) =
        updater.update_m16CategorieProduit(new)

    override fun onCleared() {
        super.onCleared()
    }
}
