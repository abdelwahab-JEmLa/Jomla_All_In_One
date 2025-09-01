package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ID3RecordingButton(
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    isRecording: Boolean,
    showLabels: Boolean,
    displayTime: String,
    onClickPourAfficheDialoge: () -> Unit = {}
) {
    val buttonBackgroundColor =
        if (isRecording) Color(0xFFFF9800) else Color(0xFF8BC34A)
    val enable = true
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                if (enable) {
                    onClickPourAfficheDialoge()
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor,
        ) {
            val iconColor = Color.Black

            Icon(
                imageVector = if (isRecording) Icons.Default.PlayArrow else Icons.Default.Stop,
                contentDescription = null,
                tint = iconColor
            )
        }

        if (showLabels) {
            // Use the pre-cached value
            val focusedValuesGetter =
                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            val filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible =
                focusedValuesGetter.filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible

            val filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT =
                focusedValuesGetter.filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT

            val remain_M2Client_Size =
                filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible.size +
                        filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT.size

            val warped_displayTime = if (!focusedValuesGetter.its_Developing_Mode) {
                displayTime
            } else {
                ""
            }

            Text(
                "$warped_displayTime | بقي $remain_M2Client_Size زبون",           //<--
                //TODO(1): pk ca afficeh 1 meme si remain == 0
                modifier = Modifier
                    .getSemanticsTag(focusedValuesGetter.its_Developing_Mode,"focusedValuesGetter.its_Developing_Mode")

                    .getSemanticsTag(
                        nomVal = "parent_M9AppCompt_KeyID",
                        data = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.parent_M9AppCompt_KeyID
                    )
                    .getSemanticsTag(
                        nomVal = "filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible",
                        data = filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible
                    )
                    .background(if (enable) buttonBackgroundColor else Color.Gray)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
