package Application4.App.Fragment.ID1.Fragment.ViewModel

import A_Main.Shared.Module.RepositorysMainSetter_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.Setter_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import android.annotation.SuppressLint
import android.content.Context
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
    val wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
) : ViewModel() {
    val active_Datas = ActiveDatasFragNewProto()
    private val updater = Setter_ViewModel_NewProtoPatterns(this)

    val _uiStateNewProtoPatterns = MutableStateFlow(UiState_NewProtoPatterns())
    val uiState = _uiStateNewProtoPatterns.asStateFlow()

    val repositorysMainSetter_NewProtoPatterns by lazy {
        RepositorysMainSetter_NewProtoPatterns(
            appDatabase,
            context
        )
    }

    val wifiState = wifiTransferDatas_ControllerApp.state.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ProductDisplayController_NewProto()
    )

    fun disconnect() = wifiTransferDatas_ControllerApp.disconnect()

    fun sendOrderToClientDisplayerT(
        order: Wifi_Messages_Types_NewProto,
        data: Any? = null
    ) =
        wifiTransferDatas_ControllerApp.sendOrderToClientDisplayerT(order, data)

    /** Setter direct de l'expansion (produit + couleur) — met à jour le state local ET notifie le client. */
    fun updateExpandedProduitEtCouleur(
        produit: M01Produit?,
        couleur: M3CouleurProduitInfos?,
        sendToClient: Boolean = true,
    ) = wifiTransferDatas_ControllerApp.updateExpandedProduitEtCouleur(produit, couleur, sendToClient)

    init {
        fragmentNavigationHandler.closeAllActiveFragments()
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).run()
    }

    fun retryLoadingData() {
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).reload()
    }

    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): M13TarificationInfos? {
        val currentBonVent = active_Datas.activeOnVent_M8BonVent
        val isGrossist = active_Datas.currentApp_ItsWorkChezGrossisst

        if (isGrossist || currentBonVent == null || synthetic == null) return null
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) return null

        val currentClient = active_Datas.activeOnVent_M2Client
        val clientBonVents = active_Datas.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            .filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            .sortedByDescending { it.creationTimestamps }

        if (clientBonVents.firstOrNull()?.keyID != currentBonVent.keyID) return null
        if (System.currentTimeMillis() - currentBonVent.creationTimestamps >= 5 * 60 * 1000) return null

        val now = System.currentTimeMillis()
        val newTariff = synthetic.copy(
            typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
            parent_M8BonVent_KeyId = currentBonVent.keyID,
            parent_M8BonVent_DebugInfos = currentBonVent.get_DebugInfos(),
            parent_M2Client_KeyId = currentClient?.keyID ?: "null",
            parent_M2Client_DebugInfos = currentClient?.nom ?: "null",
            creationTimestamps = now,
            dernierTimeTampsSynchronisationAvecFireBase = now
        )
        update_M13TarificationInfos(newTariff)
        return newTariff
    }

    //────────────Setter_ViewModel_NewProtoPatterns────────────────────────────────────────────────
    fun update_m1Produit(new: M01Produit) = updater.update_m1Produit(new)
    fun delete_m1Produit(produit: M01Produit) = updater.delete_m1Produit(produit)

    fun update_m2(new: M2Client) = updater.update_m2(new)

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

    fun update_listM10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) =
        updater.update_listM10OperationVentCouleur(updatedList)

    fun addNew_listM10OperationVentCouleur(datas: List<M10OperationVentCouleur>?) =
        updater.addNew_listM10OperationVentCouleur(datas)

    fun update_m3couleur(couleur: M3CouleurProduitInfos) = updater.update_m3couleur(couleur)
    fun delete_m3couleur(couleur: M3CouleurProduitInfos) = updater.delete_m3couleur(couleur)
    fun delete_M10OperationVentCouleur(op: M10OperationVentCouleur) =
        updater.delete_M10OperationVentCouleur(op)
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

    /**
     * Sends an arbitrary [prefix]+[data] payload directly to the tablet over Nearby Connections.
     * Used for messages that don't fit the [sendOrderToClientDisplayerT] typed enum pattern,
     * e.g. [Wifi_Messages_Types_NewProto.Change_Filtered_Produits_Du_TabletteDisplayer].
     */
    fun sendData(prefix: String, data: String) {
        wifiTransferDatas_ControllerApp.sendData("$prefix$data")
    }

    fun update_m8(bonVent: M8BonVent) = updater.update_m8(bonVent)
}
