package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application2.App.Base.Repository.ActiveCentralValues_app2
import Application4.App.A.Start.Init.Initializer_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_NewProto
import Application4.App.Modules.Wi.Module.WifiUpdateClientDisplayerStats_NewProto
import EntreApps.Shared.Models.Do
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Home.FocusedValues_NewProtoPatterns
import EntreApps.Shared.Models.Home.RepositorysMainSetter_NewProtoPatterns
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private fun collectActiveM9Compt() {
        // Si_Empty: fetch Firebase si Room vide — one-shot, coroutine separee
        viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_Si_Empty(
                dao_M9AppCompt = appDatabase.dao_M9AppCompt(),
                activeDatasFragNewProto = active_Datas,
            ).collect {}
        }
        // By_KeyId: flow Room continu — coroutine separee pour ne pas bloquer Si_Empty
        viewModelScope.launch(Dispatchers.IO) {
            FlowsFunctions_ActiveDatasFragNewProto.getFlow_active_M9Compt_By_au_Lence_Set_Compt_Ac_KeyId(
                dao_M9AppCompt = appDatabase.dao_M9AppCompt(),
                activeDatasFragNewProto = active_Datas,
            ).collect {}
        }
    }

    init {
        fragmentNavigationHandler.closeAllActiveFragments()
        collectActiveM9Compt()
        viewModelScope.launch(Dispatchers.IO) {
            androidx.compose.runtime.snapshotFlow {
                active_Datas.active_M9Compt?.next_start
            }.collect { nextStart ->
                if (nextStart == null) {
                    android.util.Log.d("DeleteInsertDebug", "  -> active_M9Compt pas encore charge, on attend...")
                    return@collect
                }
                android.util.Log.d("DeleteInsertDebug", "-> nextStart collected = $nextStart | from = ${nextStart.from} | active_M9Compt = ${active_Datas.active_M9Compt?.keyID}")
                when (nextStart) {
                    Do.StandartInit -> {
                        android.util.Log.d("DeleteInsertDebug", "  -> branch StandartInit, lancement Initializer_App4 + Initializer")
                        Initializer_App4.initializeAllRepositories(
                            context = context,
                            appDatabase = appDatabase,
                            on_Progress_Datas = { progress ->
                                _uiStateNewProtoPatterns.value =
                                    _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = progress)
                            },
                            callerScope = viewModelScope,
                        )
                        Initializer(this@A_ViewModel_NewProtoPatterns).run()
                        this.coroutineContext[kotlinx.coroutines.Job]?.cancel()
                    }
                    Do.DeleteInsertAll -> {
                        android.util.Log.d("DeleteInsertDebug", "  -> branch DeleteInsertAll -> calling deleteAllInsert_AllDatas()")
                        when (nextStart.from) {
                            Do.From.Active_Key -> deleteAllInsertKeyFilter()
                            Do.From.Ref_All_Datas, null -> deleteAllInsert_AllDatas()
                        }
                        this.coroutineContext[kotlinx.coroutines.Job]?.cancel()
                    }
                }
            }
        }
    }
    fun deleteAllInsert_AllDatas() {
        android.util.Log.d("DeleteInsertDebug", "deleteAllInsert_AllDatas() called")
        viewModelScope.launch(Dispatchers.IO) {
            fun progress(p: Float) {
                _uiStateNewProtoPatterns.value =
                    _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
            }
            try {
                val dao_M1 = appDatabase.dao_M1Produit()
                val dao_M16 = appDatabase.dao_16CategorieProduit()
                val dao_M3 = appDatabase.dao_M03CouleurProduitInfos()
                val dao_M13 = appDatabase.dao_M13TarificationInfos()
                val dao_M14 = appDatabase.dao_M14VentPeriode()
                val dao_M8 = appDatabase.dao_M8BonVent()
                val dao_M10 = appDatabase.dao_M10OperationVentCouleur()
                val dao_M9 = appDatabase.dao_M9AppCompt()

                progress(0f)
                android.util.Log.d("DeleteInsertDebug", "  M1: before deleteAll count=${dao_M1.getAll().size}")
                dao_M1.deleteAll()
                val m1List = M01Produit.ref.get().await().children.mapNotNull { it.getValue(M01Produit::class.java) }
                dao_M1.insertAll(m1List)
                android.util.Log.d("DeleteInsertDebug", "  M1: inserted ${m1List.size} rows")
                progress(1 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M16: before deleteAll count=${dao_M16.getAll().size}")
                dao_M16.deleteAll()
                val m16List = M16CategorieProduit.ref.get().await().children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                dao_M16.insertAll(m16List)
                android.util.Log.d("DeleteInsertDebug", "  M16: inserted ${m16List.size} rows")
                progress(2 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M3: before deleteAll count=${dao_M3.getAll().size}")
                dao_M3.deleteAll()
                val m3List = M3CouleurProduitInfos.ref.get().await().children.mapNotNull { it.getValue(M3CouleurProduitInfos::class.java) }
                dao_M3.insertAll(m3List)
                android.util.Log.d("DeleteInsertDebug", "  M3: inserted ${m3List.size} rows")
                progress(3 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M13: before deleteAll count=${dao_M13.getAll().size}")
                dao_M13.deleteAll()
                val m13List = M13TarificationInfos.ref.get().await().children.mapNotNull { it.getValue(M13TarificationInfos::class.java) }
                dao_M13.insertAll(m13List)
                android.util.Log.d("DeleteInsertDebug", "  M13: inserted ${m13List.size} rows")
                progress(4 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M14: before deleteAll count=${dao_M14.getAll().size}")
                dao_M14.deleteAll()
                val m14List = M14VentPeriode.ref.get().await().children.mapNotNull { it.getValue(M14VentPeriode::class.java) }
                dao_M14.insertAll(m14List)
                android.util.Log.d("DeleteInsertDebug", "  M14: inserted ${m14List.size} rows")
                progress(5 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M8: before deleteAll count=${dao_M8.getAll().size}")
                dao_M8.deleteAll()
                val m8List = M8BonVent.ref.get().await().children.mapNotNull { it.getValue(M8BonVent::class.java) }
                dao_M8.insertAll(m8List)
                android.util.Log.d("DeleteInsertDebug", "  M8: inserted ${m8List.size} rows")
                progress(6 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M10: before deleteAll count=${dao_M10.getAll().size}")
                dao_M10.deleteAll()
                val m10List = M10OperationVentCouleur.ref.get().await().children.mapNotNull { it.getValue(M10OperationVentCouleur::class.java) }
                dao_M10.insertAll(m10List)
                android.util.Log.d("DeleteInsertDebug", "  M10: inserted ${m10List.size} rows")
                progress(7 / 8f)

                android.util.Log.d("DeleteInsertDebug", "  M9: before deleteAll count=${dao_M9.getAll().size}")
                dao_M9.deleteAll()
                val m9List = Z_AppCompt.ref.get().await().children.mapNotNull { it.getValue(Z_AppCompt::class.java) }
                dao_M9.insertAll(m9List)
                android.util.Log.d("DeleteInsertDebug", "  M9: inserted ${m9List.size} rows")
                progress(1f)
                android.util.Log.d("DeleteInsertDebug", "deleteAllInsert_AllDatas() completed successfully")
                val updatedCompt = active_Datas.active_M9Compt
                if (updatedCompt != null) {
                    update_active_Compt(updatedCompt.copy(next_start = Do.StandartInit))
                    android.util.Log.d("DeleteInsertDebug", "  -> next_start reset to StandartInit, Initializer will auto-launch")
                }
            } catch (e: Exception) {
                android.util.Log.e("DeleteInsertDebug", "deleteAllInsert_AllDatas() ❌ EXCEPTION: ${e::class.simpleName}: ${e.message}", e)
                progress(1f)
            }
        }
    }

    fun deleteAllInsertKeyFilter() {
        viewModelScope.launch(Dispatchers.IO) {
            fun progress(p: Float) {
                _uiStateNewProtoPatterns.value =
                    _uiStateNewProtoPatterns.value.copy(initDatasProgressEtate = p)
            }
            try {
                // ── Step 1: M3CouleurProduitInfos ──────────────────────────────
                progress(0f)
                val dao_M3 = appDatabase.dao_M03CouleurProduitInfos()
                val allowedKeys: Set<String> =
                    M3CouleurProduitInfos.ref_listKeys_M3CouleurProduitInfos
                        .get().await()
                        .children.mapNotNull { it.key }
                        .toSet()
                val colours = M3CouleurProduitInfos.ref.get().await()
                    .children.mapNotNull { it.getValue(M3CouleurProduitInfos::class.java) }
                    .filter { it.keyID in allowedKeys }
                dao_M3.deleteAll()
                if (colours.isNotEmpty()) dao_M3.insertAll(colours)
                progress(1 / 3f)

                // ── Step 2: M01Produit (filtered by available M3 parent keys) ─
                val dao_M1 = appDatabase.dao_M1Produit()
                val m3ParentKeys: Set<String> = dao_M3.getAll()
                    .map { it.parentBProduitInfosKeyID }
                    .toSet()
                val products = M01Produit.ref.get().await()
                    .children.mapNotNull { it.getValue(M01Produit::class.java) }
                    .filter { it.keyID in m3ParentKeys }
                dao_M1.deleteAll()
                if (products.isNotEmpty()) dao_M1.insertAll(products)
                progress(2 / 3f)

                // ── Step 3: M16CategorieProduit (filtered by available M1 category IDs) ─
                val dao_M16 = appDatabase.dao_16CategorieProduit()
                val m1CategoryIds: Set<Long> = dao_M1.getAll()
                    .map { it.idParentCategorie }
                    .toSet()
                val categories = M16CategorieProduit.ref.get().await()
                    .children.mapNotNull { it.getValue(M16CategorieProduit::class.java) }
                    .filter { it.id in m1CategoryIds }
                dao_M16.deleteAll()
                if (categories.isNotEmpty()) dao_M16.insertAll(categories)
                progress(1f)
            } catch (_: Exception) {
                progress(1f)
            }
        }
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

    //────────────Setter_ViewModel_NewProtoPatterns────────────────────────────────────────────────
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
