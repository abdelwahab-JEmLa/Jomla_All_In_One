package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.Z.ClientSearchItem.View

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.getTimeElapsedString
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

data class return_get_Edited_M8BonVent(
    val found_M8 :M8BonVent?,
    val default_If_No_Found :M8BonVent,
)
fun get_Edited_M8BonVent(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    onShowToast: (ToastData) -> Unit
): Triple<M8BonVent?, M8BonVent, Modifier>? {
    val getFocusedVars = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent
    val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode

    if (currentPeriod == null) {
        onShowToast(
            ToastData(
                message = "Aucune période de vente active trouvée",
                type = ToastType.ERROR,
                duration = 4000L
            )
        )
        return null
    }

    val currentPeriodKeyID = currentPeriod.keyID

    val existingBonVent = repo8BonVent.datasValue.find { bonVent ->
        bonVent.parent_M14VentPeriod_KeyId == currentPeriodKeyID &&
                bonVent.parent_M2Client_KeyID == relative_M2Client.keyID
    }

    val newBonVent = M8BonVent().copy(
        parent_M9AppCompt_KeyID = getFocusedVars.active_Current_M9AppCompt?.keyID ?: "",
        parent_M14VentPeriod_KeyId = currentPeriodKeyID,
        parent_M2Client_KeyID = relative_M2Client.keyID,
        parent_M2Client_DebugInfos = relative_M2Client.nom,
    )

    val semanticsModifier = Modifier.getSemanticsTag(nomVal = "newBonVent", data = newBonVent)

    return Triple(existingBonVent, newBonVent, semanticsModifier)
}

@SuppressLint("DefaultLocale")
@Composable
fun ClientSearchItem(
    aCentralFacade: ACentralFacade = koinInject(),
    m2Client: M2Client,
    onClick: () -> Unit,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
) {
    val bonVentRepository = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent

    // Add toast state management
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    val latestBonVent = remember(m2Client.keyID, bonVentRepository.datasValue) {
        bonVentRepository.datasValue
            .filter { it.parent_M2Client_KeyID == m2Client.keyID }
            .maxByOrNull { it.creationTimestamps }
    }

    val bonVentResult = get_Edited_M8BonVent(
        aCentralFacade,
        m2Client
    ) { toastDataToShow -> toastData = toastDataToShow }

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    bonVentResult?.let { (existingBonVent, newBonVent, semanticsModifier) ->
        ElevatedCard(
            modifier = Modifier
                .padding(petitePaddine)
                .then(semanticsModifier)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val handleClick = {
                            if (existingBonVent != null) {
                                viewModel.aCentralFacade.repositorysMainSetter.update_M8BonVent(
                                    existingBonVent
                                )
                            } else {
                                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.add_M8BonVent(
                                    newBonVent
                                )
                            }

                            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.setIN_M9CurrentApp_onVentM8BonVentKey(
                                existingBonVent ?: newBonVent
                            )
                        }
                        handleClick()
                        onClick()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = Color(
                                latestBonVent?.etateActuellementEst?.color
                                    ?: m2Client.actuelleEtat.color
                            ),
                            shape = CircleShape
                        )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = m2Client.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    latestBonVent?.let {
                        Text(
                            text = "Dernière commande: ${getTimeElapsedString(it.creationTimestamps)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    if (m2Client.caMarqueGpsEstOuvert && m2Client.latitude != 0.0 && m2Client.longitude != 0.0) {
                        Text(
                            text = "📍 ${
                                String.format(
                                    "%.4f",
                                    m2Client.latitude
                                )
                            }, ${String.format("%.4f", m2Client.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Row {
                    Icon(
                        imageVector = m2Client.clientTypeMode.icon,
                        contentDescription = null,
                        tint = m2Client.clientTypeMode.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = m2Client.clientTypeMode.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    } ?: run {
        // Optional: Show a placeholder or error state when bonVentResult is null
        ElevatedCard(
            modifier = Modifier
                .padding(petitePaddine)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = Color.Gray,
                            shape = CircleShape
                        )
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = m2Client.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Période de vente inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
