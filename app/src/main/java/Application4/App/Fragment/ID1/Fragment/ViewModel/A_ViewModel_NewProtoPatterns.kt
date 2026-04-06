package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.ProductDisplayController_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Modules.Base.AppDatabase
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
) : ViewModel() {
    val active_Datas = ActiveDatasFragNewProto()
    private val updater = Setter_ViewModel_NewProtoPatterns(this)

    val _uiStateNewProtoPatterns = MutableStateFlow(UiState_NewProtoPatterns())
    val uiState = _uiStateNewProtoPatterns.asStateFlow()

    val repositorysMainSetter_NewProtoPatterns by lazy { RepositorysMainSetter_NewProtoPatterns(appDatabase, context) }

    val wifi = WifiTransferDatas_ControllerApp(
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

    fun sendOrderToClientDisplayerT(
        order: Wifi_Messages_Types_NewProto,
        data: Any? = null
    ) =
        wifi.sendOrderToClientDisplayerT(order, data)

    /** Setter direct de l'expansion (produit + couleur) — met à jour le state local ET notifie le client. */
    fun updateExpandedProduitEtCouleur(
        produit: M01Produit?,
        couleur: M3CouleurProduitInfos?,
        sendToClient: Boolean = true,
    ) = wifi.updateExpandedProduitEtCouleur(produit, couleur, sendToClient)

    init {
        fragmentNavigationHandler.closeAllActiveFragments()
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).run()
    }

    fun retryLoadingData() {
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).run()
    }

    /**
     * Attempts to persist a one-off [Edited_Pour_Client] tariff for [produit] on the
     * current bon-vent.  Returns the newly created tariff on success, null otherwise
     * (grossist mode, no active bon-vent, one already exists, bon too old, etc.).
     */
    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): M13TarificationInfos? {
        val currentBonVent = active_Datas.activeOnVent_M8BonVent
        val isGrossist = active_Datas.currentApp_ItsWorkChezGrossisst

        if (isGrossist || currentBonVent == null || synthetic == null) {
            android.util.Log.d(
                "TariffFix",
                "[maybeCreate] SKIP — isGrossist=$isGrossist bonVent=${currentBonVent?.keyID} synthetic=${synthetic?.keyID}"
            )
            return null
        }
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) {
            android.util.Log.d(
                "TariffFix",
                "[maybeCreate] SKIP — Edited_Pour_Client already exists for produit=${produit.keyID} bonVent=${currentBonVent.keyID}"
            )
            return null
        }

        val currentClient = active_Datas.activeOnVent_M2Client
        val clientBonVents = active_Datas.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            .filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            .sortedByDescending { it.creationTimestamps }

        if (clientBonVents.firstOrNull()?.keyID != currentBonVent.keyID) {
            android.util.Log.d(
                "TariffFix",
                "[maybeCreate] SKIP — bonVent is not the latest for client=${currentClient?.keyID}"
            )
            return null
        }
        val age = System.currentTimeMillis() - currentBonVent.creationTimestamps
        if (age >= 5 * 60 * 1000) {
            android.util.Log.d(
                "TariffFix",
                "[maybeCreate] SKIP — bonVent too old (${age / 1000}s) bonVent=${currentBonVent.keyID}"
            )
            return null
        }

        val now = System.currentTimeMillis()
        val newTariff = synthetic.copy(
            // ── BUG FIX: was missing — copy kept Prix_Progressive_Editable ──
            typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
            parent_M8BonVent_KeyId = currentBonVent.keyID,
            parent_M8BonVent_DebugInfos = currentBonVent.get_DebugInfos(),
            parent_M2Client_KeyId = currentClient?.keyID ?: "null",
            parent_M2Client_DebugInfos = currentClient?.nom ?: "null",
            creationTimestamps = now,
            dernierTimeTampsSynchronisationAvecFireBase = now
        )
        android.util.Log.d(
            "TariffFix",
            "[maybeCreate] CREATED Edited_Pour_Client keyID=${newTariff.keyID} prix=${newTariff.prixCurrency} produit=${produit.keyID} bonVent=${currentBonVent.keyID}"
        )
        update_M13TarificationInfos(newTariff)
        return newTariff
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

    fun update_listM10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) =
        updater.update_listM10OperationVentCouleur(updatedList)

    fun addNew_listM10OperationVentCouleur(datas: List<M10OperationVentCouleur>?) =
        updater.addNew_listM10OperationVentCouleur(datas)

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
