package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs.But1_Floating_ClientsListDialog.But1_Floating_ClientsListDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.filterClientsBasedOnMode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.LoadingProgressOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.views.MapView

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun A_MapClients_A2FragID_1(
    modifier: Modifier = Modifier,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    viewModel: MapClientsViewModel = koinViewModel(),
    onUpdateLongAppSetting: () -> Unit = {},
    onClear: () -> Unit = {},
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
) {

    val uiState by viewModel.uiState.collectAsState()
    val progress = uiState.mainLoadingProgress

    var isTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isTimeout = true
    }

    // Clean up resources when fragment is disposed
    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanupResources()
        }
    }


    // Certains comptes (M09AppCompt.lance_dialoge_client_et_ne_lance_pas_le_map_pour_ressources,
    // togglé depuis But1_Floating_ClientsListDialog) n'ont besoin que de la
    // liste des clients à l'ouverture de ce fragment, jamais de la carte
    // elle-même. Pour eux on évite complètement de composer MapContent —
    // pas de tuiles OSM à télécharger, pas de GPS tracking, pas de calcul
    // des marqueurs — et on ouvre directement le dialogue liste par-dessus
    // un écran neutre, ce qui économise réseau/CPU/batterie tant que
    // l'utilisateur n'a pas explicitement besoin de voir la carte.
    val skipMapForResources = viewModel.active_Datas.active_M9Compt
        ?.lance_dialoge_client_et_ne_lance_pas_le_map_pour_ressources == true

    Box(modifier = modifier.fillMaxSize()) {
        when {
            progress < 1.0f && !isTimeout -> LoadingProgressOverlay(progress = progress)
            skipMapForResources -> DirectClientsListDialog(
                viewModel = viewModel,
                fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                onDismiss = onClear,
            )
            else -> MapContent(
                viewModel = viewModel,
                fragmentNavigationHandler_NewProto=
                    fragmentNavigationHandler_NewProto,
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClear = onClear,
                wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
            )
        }
    }
}

/**
 * Alternative allégée à MapContent, utilisée quand
 * M09AppCompt.lance_dialoge_client_et_ne_lance_pas_le_map_pour_ressources est
 * actif sur le compte courant : ouvre But1_Floating_ClientsListDialog
 * directement, sans jamais créer/attacher de vraie carte osmdroid.
 *
 * Le MapView ci-dessous n'est ni rendu (pas d'AndroidView) ni initialisé
 * (pas de tuiles, pas de setMultiTouchControls, pas de LocationTracker) — il
 * n'existe que pour satisfaire la signature de But1_Floating_ClientsListDialog
 * (utilisé par son raccourci "Centrer sur la carte (3km)", sans effet ici
 * puisque la carte n'est jamais affichée).
 *
 * La liste de clients passée au dialogue vient de filterClientsBasedOnMode,
 * qui ne dépend que du mode actif (pas de la position de la carte) —
 * contrairement à getClientsCurrentlyVisibleOnMap utilisé par MapContent, qui
 * filtre en plus par proximité au centre de la carte.
 *
 * Fermer ce dialogue (bouton "Fermer" ou tap en dehors) revient à quitter le
 * fragment puisqu'il n'y a rien d'autre à afficher derrière : onDismiss est
 * donc relié à onClear plutôt qu'à un état local.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun DirectClientsListDialog(
    viewModel: MapClientsViewModel,
    fragmentNavigationHandler_NewProto: FragmentNavigationHandler_NewProto,
    onUpdateLongAppSetting: () -> Unit,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val currentFilterMode = viewModel.active_Datas.filter_marqueClient_enum_entries
        ?: MapClientsViewModel.VisibleClientsNow.showAll
    val clients = remember(currentFilterMode, viewModel.mapReloadTrigger) {
        filterClientsBasedOnMode(viewModel, currentFilterMode)
    }

    But1_Floating_ClientsListDialog(
        mapView = mapView,
        clients = clients,
        viewModel = viewModel,
        fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
        list_M13TarificationInfos = viewModel.aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue,
        onDismiss = onDismiss,
    )

    // Cliquer une ligne client en mode Standart déclenche performClickOnMarqueAction
    // (A_B_MarkersHandler.kt) qui pose activeOnVentM2ClientInfos / uiState.
    // markerStatusDialogActiveM2Client exactement comme un tap sur un marqueur —
    // mais comme ce mode n'affiche jamais MapContent, ce dialogue de statut
    // (et le dropdown qu'il contient, ex. CustomStatusDropdownMenu dans
    // A0_MarkerStatusDialog.kt) ne s'affichait jamais. On reproduit donc ici
    // la même logique shouldShowMarkerDialog / MarkerStatusDialog que
    // A_MapContent.kt, pour que le dialogue s'affiche par-dessus
    // But1_Floating_ClientsListDialog même sans carte.
    val uiState by viewModel.uiState.collectAsState()
    val markerStatusDialogActiveM2Client = uiState.markerStatusDialogActiveM2Client
    val focusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val activeOnVentM2ClientInfos = focusedValuesGetter.activeOnVentM2ClientInfos
    val currentClickOnMarqueMode = viewModel.active_Datas.active_M9Compt?.click_On_Marque
        ?: ActiveCentralValues.Click_On_Marque.Standart
    val shouldShowMarkerDialog = (activeOnVentM2ClientInfos != null || markerStatusDialogActiveM2Client != null) &&
            currentClickOnMarqueMode != ActiveCentralValues.Click_On_Marque.ADD_Au_Ciblage_Clients

    if (shouldShowMarkerDialog) {
        MarkerStatusDialog(
            wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
            fragmentNavigationHandler_NewProto = fragmentNavigationHandler_NewProto,
            viewModel = viewModel,
            relative_M2Client = activeOnVentM2ClientInfos ?: markerStatusDialogActiveM2Client,
            markerStatusDialogActiveM2Client = markerStatusDialogActiveM2Client,
            onUpdateLongAppSetting = onUpdateLongAppSetting,
            // Pas de vraie carte ici (mapView n'est jamais attaché/rendu) : il
            // n'y a donc aucun marqueur GPS à repositionner ou à retirer visuellement.
            onClickToEditeMarquerPosition = { },
            onRemoveMark = { },
            on_dissmiss_dialog_avec_enleve_focuse_bon = {
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
                    .desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(markerStatusDialogActiveM2Client = null)
                )
            },
        )
    }
}
