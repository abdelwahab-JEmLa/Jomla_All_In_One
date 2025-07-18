package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
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


@Composable
fun M8BonVent.EtateActuellementEst.ButtonAutreEtates(
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    clickedClient: Long,
) {
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    val context = LocalContext.current
    val relative_Etate = this
    val relative_M2Client =
        aCentralFacade.repoMainGetter.repo2Client.datasValue.find { it.id == clickedClient }!!

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    val found_Or_Default_M8BonVent = get_Found_Or_Default_M8BonVent(
        aCentralFacade,
        relative_M2Client,
        relative_Etate = relative_Etate,
    )

    FilledTonalButton(
        modifier = Modifier,
        onClick = {

            if (found_Or_Default_M8BonVent.found != null) {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    found_Or_Default_M8BonVent.found
                )
            } else {
                aCentralFacade.repositorysMainSetter
                    .addNew_M8BonVent(found_Or_Default_M8BonVent.default_If_No_Found)
            }

            if (relative_Etate == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
            }

         /*   val target =
                found_Or_Default_M8BonVent.found ?: found_Or_Default_M8BonVent.default_If_No_Found
            val tar_Updated = focusedValuesGetter.active_Central_Values.copy(
                active_M8BonVent_Pour_ReplayVocale = target
            )
            focusedValuesGetter.update_activeCentralValues(tar_Updated)          */
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

