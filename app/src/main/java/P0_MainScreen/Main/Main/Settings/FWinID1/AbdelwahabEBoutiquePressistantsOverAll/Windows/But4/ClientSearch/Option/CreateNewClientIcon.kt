package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Modules.Helper.M1.LocationTracker.Module.LocationTracker
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import EntreApps.Shared.Models.M8BonVent
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import org.koin.compose.koinInject

@SuppressLint("DefaultLocale")
@Composable
fun CreateNewClientIcon(
    searchQuery: String,
    locationTracker: LocationTracker?,
    defaultId8BonVent: M8BonVent,
    onClientSelectedToToast: (M2Client) -> Unit,
    onResetSearchMode: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    isFournisseurMode: Boolean = false, // New parameter
    repositorysMainSetter: RepositorysMainSetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
) {
    val currentLocation = locationTracker?.getCurrentPosition()

    val newClient = M2Client(
        keyID = M2Client.generePushKey(),
        creationTimestamps = System.currentTimeMillis(),
        nom = searchQuery.ifEmpty { " Person ${M2Client.generePushKey().takeLast(4)}" },
        title = searchQuery.ifEmpty {
            if (isFournisseurMode) "Nouveau Fournisseur" else "Nouveau Client"
        },
        latitude = 36.720027701275505,
        longitude = 3.1436710147865483,
        caMarqueGpsEstOuvert = currentLocation != null,
        its_Fournisseur = isFournisseurMode, // Set based on toggle state
        snippet = currentLocation?.let {
            "Lat: ${String.format("%.6f", it.latitude)}, Lng: ${
                String.format(
                    "%.6f",
                    it.longitude
                )
            }"
        } ?: "Position non disponible",
        edite_Exact_Gps_est_fait = true,
        parentComptCreateurKEyID = focusedValuesGetter.currentActive_M9AppCompt?.keyID ?: ""
    )

    val addedDefaultOnVentID8BonVentEtAdd = defaultId8BonVent.copy(
        creationTimestamps = System.currentTimeMillis(),
        parent_M2Client_KeyID = newClient.keyID,
        parent_M2Client_DebugInfos = newClient.nom,
        its_working_for_wholesaler = true
    )

    val updatedAppCompt = viewModel.getterFocusedVarsHandlerFacade.currentActive_M9AppCompt?.copy(
        onVentM8BonVentKey = addedDefaultOnVentID8BonVentEtAdd.keyID,
        onVentM8BonVentDebugInfos = addedDefaultOnVentID8BonVentEtAdd.get_DebugInfos()
    )

    IconButton(
        onClick = {
            repositorysMainSetter.upsert_M2Client(newClient)
            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.upsert_M8BonVent_Et_Focuce_Le_Au_M9CurrCompt(
                addedDefaultOnVentID8BonVentEtAdd,
                updatedAppCompt
            )
            onClientSelectedToToast(newClient)
            onResetSearchMode()
        },
        modifier = Modifier.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("Debug new M8BonVent"), addedDefaultOnVentID8BonVentEtAdd)
            set(SemanticsPropertyKey("Debug currentM9AppCompt avec new M8BonVent"), updatedAppCompt)
            set(SemanticsPropertyKey("Debug isFournisseurMode"), isFournisseurMode)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = if (isFournisseurMode) "Créer nouveau fournisseur" else "Créer nouveau client",
            tint = if (isFournisseurMode) Color(0xFFFF9800) else Color(0xFF2196F3)
        )
    }
}
