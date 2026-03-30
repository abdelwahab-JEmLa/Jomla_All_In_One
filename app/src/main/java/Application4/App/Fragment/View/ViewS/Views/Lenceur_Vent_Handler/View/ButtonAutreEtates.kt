package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View
     /*
import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M2Client
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel

@Composable
fun M8BonVent.EtateActuellementEst.ButtonAutreEtates_NP(
    modifier: Modifier = Modifier,
    mapClientsViewModel: MapClientsViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = mapClientsViewModel.aCentralFacade,
    onClick: (M8BonVent) -> Unit = {},
    viewModel: ViewModel_NewProtoPatterns,
    uiState: UiState_NewProtoPatterns,
) {
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    val context = LocalContext.current
    val relative_Etate = this

    val relative_M2Client = uiState.active_Central_Values.activeOnVent_M2Client

    if (relative_M2Client == null) {
        toastData = ToastData(
            message = "Client non trouvé",
        )
        return
    }

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    FilledTonalButton(
        modifier = Modifier,
        onClick = {
            val found_Or_Default_M8BonVent = get_Found_Or_Default_M8BonVent(
                aCentralFacade,
                relative_M2Client,
                etateActuellementEst = relative_Etate,
            )

            if (found_Or_Default_M8BonVent.found != null) {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    found_Or_Default_M8BonVent.found
                )
                onClick(found_Or_Default_M8BonVent.found)
            } else {
                aCentralFacade.repositorysMainSetter
                    .addNew_M8BonVent(found_Or_Default_M8BonVent.default_If_No_Found)
                onClick(found_Or_Default_M8BonVent.default_If_No_Found)
            }

            (relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME)
                .ifTrue {
                    val lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT =
                        uiState.list_Datas?.m8BonVent?.lastOrNull {
                            it.parent_M2Client_KeyID == relative_M2Client.keyID
                                    && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                        }
                    if (lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT != null
                        && lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT.confirmeCommande_TimeTamp == 0L
                    ) {
                        aCentralFacade.repositorysMainSetter.update_M8BonVent(
                            lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT.copy(
                                confirmeCommande_TimeTamp = System.currentTimeMillis()
                            )
                        )
                    }
                }

            aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()

        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    relative_Etate.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    relative_Etate.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = relative_Etate.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(relative_Etate.nomArabe)
        }
    }
}


data class Found_Or_Default_M8BonVent(
    val found: M8BonVent?,
    val default_If_No_Found: M8BonVent,
)

fun get_Found_Or_Default_M8BonVent(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    etateActuellementEst: M8BonVent.EtateActuellementEst?=null,
    onShowToast: (ToastData) -> Unit = {}
): Found_Or_Default_M8BonVent {
    val getFocusedVars = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent
    val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode
    val currentPeriodKeyID = currentPeriod!!.keyID
    val relative_M2Client_KeyID = relative_M2Client.keyID

    val found_M8 =
        M8BonVent.find_By_MainValuesKeys_Depuit_List(
            data_List
                .find { data ->
                    val match_MainValuesKeys =
                        data.parent_M14VentPeriod_KeyId == parent_M14VentPeriod_KeyId
                                && data.parent_M2Client_KeyID == parent_M2Client_KeyID
                                && data.etateActuellementEst == relative_Etate
                    match_MainValuesKeys
                }
        )

    val defaultEdited_M8BonVent = M8BonVent.get_default(
        parent_M9AppCompt_KeyID = getFocusedVars.currentActive_M9AppCompt?.keyID ?: "",
        parent_M9AppCompt_DebugInfos = getFocusedVars.currentActive_M9AppCompt?.get_DebugInfos() ?: "",
        parent_M14VentPeriod_KeyId = currentPeriod.keyID ,
        parent_M14VentPeriod_DebugInfos =currentPeriod.get_DebugInfos(),
        parent_M2Client_KeyID = relative_M2Client.keyID,
        parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
        etateActuellementEst = etateActuellementEst
    )

    return Found_Or_Default_M8BonVent(
        found_M8,
        defaultEdited_M8BonVent,
    )
}
                    */
