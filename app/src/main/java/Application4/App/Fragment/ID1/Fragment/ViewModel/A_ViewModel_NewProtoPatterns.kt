package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_NewProto
import Application4.App.Modules.Wi.Module.WifiUpdateClientDisplayerStats_NewProto
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M16CategorieProduit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

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

    val wifi = WifiTransferDatas_NewProto(
        context = context,
        coroutineScope = viewModelScope,
        list_M1Produit = emptyList(),
        list_M3CouleurProduit = emptyList(),
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
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).run()
    }

    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): Boolean {
        val currentBonVent = active_Datas.activeOnVent_M8BonVent
        val isGrossist = active_Datas.currentApp_ItsWorkChezGrossisst

        if (isGrossist || currentBonVent == null || synthetic == null) return false
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) return false

        val currentClient = active_Datas.activeOnVent_M2Client
        val clientBonVents = active_Datas.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            .filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            .sortedByDescending { it.creationTimestamps }

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

    fun setActiveFocuceTariffPrixDifineur(produit: M01Produit, appCompt: M09AppCompt) =
        repositorysMainSetter_NewProtoPatterns.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            produit,
            appCompt
        )

    fun update_active_Compt(compt: M09AppCompt) = updater.update_active_Compt(compt)
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
