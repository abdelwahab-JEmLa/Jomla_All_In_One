package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
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
import org.koin.compose.koinInject


data class Found_Or_Default_M8BonVent(
    val found: M8BonVent?,
    val default_If_No_Found: M8BonVent,
)

fun get_Found_Or_Default_M8BonVent(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    relative_Etate: M8BonVent.EtateActuellementEst,
    onShowToast: (ToastData) -> Unit = {}
): Found_Or_Default_M8BonVent {
    val getFocusedVars = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent
    val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode
    val currentPeriodKeyID = currentPeriod!!.keyID
    val relative_M2Client_KeyID = relative_M2Client.keyID

    val found_M8 =
        M8BonVent.find_By_MainValuesKeys_Depuit_List(
            data_List = repo8BonVent.datasValue,
            parent_M14VentPeriod_DebugInfos = currentPeriodKeyID,
            parent_M2Client_KeyID = relative_M2Client_KeyID,
            relative_Etate = relative_Etate,
        )

    val defaultEdited_M8BonVent = M8BonVent.get_default(
        parent_M9AppCompt_KeyID = getFocusedVars.currentM9AppCompt?.keyID ?: "",
        parent_M9AppCompt_DebugInfos = getFocusedVars.currentM9AppCompt?.get_DebugInfos() ?: "",
        parent_M14VentPeriod_DebugInfos = currentPeriod.keyID,
        parent_M14VentPeriod_KeyId = currentPeriod.get_DebugInfos(),
        parent_M2Client_KeyID = relative_M2Client.keyID,
        parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
        etateActuellementEst = relative_Etate
    )

    return Found_Or_Default_M8BonVent(
        found_M8,
        defaultEdited_M8BonVent,
    )
}

@Composable
fun M8BonVent.EtateActuellementEst.ButtonAutreEtates(
    aCentralFacade: ACentralFacade = koinInject(),
    viewModel: MapClientsViewModel,
    clickedClient: Long,
) {
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    val context = LocalContext.current
    val relative_Etate = this
    val relative_M2Client =
        aCentralFacade.repositorysMainGetter.repo2Client.datasValue.find { it.id == clickedClient }!!

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    FilledTonalButton(
        modifier = Modifier
            .getSemanticsTag(
                get_Found_Or_Default_M8BonVent(
                    aCentralFacade,
                    relative_M2Client,
                    relative_Etate = relative_Etate,
                ).default_If_No_Found, "def"
            ),
        onClick = {
            val found_Or_Default_M8BonVent = get_Found_Or_Default_M8BonVent(
                aCentralFacade,
                relative_M2Client,
                relative_Etate = relative_Etate,
            ) { toastData = it }

            if (found_Or_Default_M8BonVent.found != null) {
                viewModel.aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    found_Or_Default_M8BonVent.found
                )
            } else {
                viewModel.aCentralFacade.repositorysMainSetter
                    .addNew_M8BonVent(found_Or_Default_M8BonVent.default_If_No_Found)
            }


            if (relative_Etate == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
            }
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
