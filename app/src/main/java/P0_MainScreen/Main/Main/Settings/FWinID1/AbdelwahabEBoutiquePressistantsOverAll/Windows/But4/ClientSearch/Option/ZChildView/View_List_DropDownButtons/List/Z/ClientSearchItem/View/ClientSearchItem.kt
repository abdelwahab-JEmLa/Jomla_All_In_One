package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.ZChildView.View_List_DropDownButtons.List.Z.ClientSearchItem.View

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But4.ClientSearch.Option.Z.ClientSearchItem.View.getTimeElapsedString
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

data class return_get_Edited_M8BonVent(
    val found_M8: M8BonVent?,
    val default_If_No_Found: M8BonVent,
)

fun get_Edited_M8BonVent(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    onShowToast: (ToastData) -> Unit
): Triple<M8BonVent?, M8BonVent, Modifier>? {
    Log.d("suit_flow", "🔍 ClientSearchItem.get_Edited_M8BonVent: Début - Client=${relative_M2Client.nom}")

    val getFocusedVars = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent
    val currentPeriod = getFocusedVars.currentActiveFocuced_M14VentPeriode

    if (currentPeriod == null) {
        Log.w("suit_flow", "⚠️ ClientSearchItem.get_Edited_M8BonVent: Aucune période active")
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
    Log.d("suit_flow", "📊 ClientSearchItem.get_Edited_M8BonVent: Période active=${currentPeriodKeyID.takeLast(4)}")

    val existingBonVent = repo8BonVent.datasValue.find { bonVent ->
        bonVent.parent_M14VentPeriod_KeyId == currentPeriodKeyID &&
                bonVent.parent_M2Client_KeyID == relative_M2Client.keyID
    }?.copy(
        its_working_for_wholesaler = true
    )

    if (existingBonVent != null) {
        Log.d("suit_flow", "✅ ClientSearchItem.get_Edited_M8BonVent: BonVent existant trouvé - keyID=${existingBonVent.keyID.takeLast(4)}, isPrinted=${existingBonVent.a_etai_imprime_au_moi_ne_foit}")
    } else {
        Log.d("suit_flow", "➕ ClientSearchItem.get_Edited_M8BonVent: Aucun BonVent existant, création d'un nouveau")
    }

    val newBonVent = M8BonVent().copy(
        parent_M9AppCompt_KeyID = getFocusedVars.currentActive_M9AppCompt?.keyID ?: "",
        parent_M14VentPeriod_KeyId = currentPeriodKeyID,
        parent_M2Client_KeyID = relative_M2Client.keyID,
        parent_M2Client_DebugInfos = relative_M2Client.nom,
        its_working_for_wholesaler = true
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
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    Log.d("suit_flow", "🎨 ClientSearchItem.Compose: Rendu pour client=${m2Client.nom}")

    val bonVentRepository = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent

    // Add toast state management
    var toastData by remember { mutableStateOf<ToastData?>(null) }

    // ⚠️ IMPORTANT: Ne pas utiliser remember ici car il cache la valeur
    // On veut que latestBonVent se recalcule à chaque changement de bonVentRepository.datasValue
    val latestBonVent = bonVentRepository.datasValue
        .filter { it.parent_M2Client_KeyID == m2Client.keyID }
        .maxByOrNull { it.creationTimestamps }
        .also { latest ->
            Log.d("suit_flow", "📊 ClientSearchItem.Compose: Dernier BonVent pour ${m2Client.nom} = ${latest?.keyID?.takeLast(4) ?: "null"}, isPrinted=${latest?.a_etai_imprime_au_moi_ne_foit}")
        }

    val bonVentResult = get_Edited_M8BonVent(
        aCentralFacade,
        m2Client
    ) { toastDataToShow -> toastData = toastDataToShow }

    val currentValues = focusedValuesGetter.active_Central_Values
    val isInDeletionList = currentValues.list_clients_por_suprime.any { it.keyID == m2Client.keyID }

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    bonVentResult?.let { (existingBonVent, newBonVent, semanticsModifier) ->
        Box(
            modifier = Modifier
                .padding(petitePaddine)
                .then(semanticsModifier)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d("suit_flow", "👆 ClientSearchItem.onClick: Click sur client=${m2Client.nom}")

                            val handleClick = {
                                if (existingBonVent != null) {
                                    Log.d("suit_flow", "🔄 ClientSearchItem.onClick: Mise à jour BonVent existant - keyID=${existingBonVent.keyID.takeLast(4)}")
                                    viewModel.aCentralFacade.repositorysMainSetter.update_M8BonVent(
                                        existingBonVent
                                    )
                                } else {
                                    Log.d("suit_flow", "➕ ClientSearchItem.onClick: Ajout nouveau BonVent - keyID=${newBonVent.keyID.takeLast(4)}")
                                    viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.add_M8BonVent(
                                        newBonVent
                                    )
                                }

                                Log.d("suit_flow", "🎯 ClientSearchItem.onClick: Définition du BonVent actif")
                                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.setIN_M9CurrentApp_onVentM8BonVentKey(
                                    existingBonVent ?: newBonVent
                                )
                            }
                            handleClick()
                            onClick()
                        }
                        .padding(12.dp)
                        .padding(end = 80.dp), // Space for floating buttons
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val currentPeriodKeyID = focusedValuesGetter.currentActiveFocuced_M14VentPeriode?.keyID

                // ⚠️ IMPORTANT: Ne pas utiliser remember ici non plus
                // On veut recalculer à chaque changement de datasValue
                val currentPeriodBonVent = if (currentPeriodKeyID != null) {
                    bonVentRepository.datasValue.find { bonVent ->
                        bonVent.parent_M14VentPeriod_KeyId == currentPeriodKeyID &&
                                bonVent.parent_M2Client_KeyID == m2Client.keyID
                    }.also { bonVent ->
                        Log.d("suit_flow", "📊 ClientSearchItem.currentPeriodBonVent: Pour ${m2Client.nom}, période=${currentPeriodKeyID.takeLast(4)}, bonVent=${bonVent?.keyID?.takeLast(4) ?: "null"}, isPrinted=${bonVent?.a_etai_imprime_au_moi_ne_foit}")
                    }
                } else {
                    Log.d("suit_flow", "⚠️ ClientSearchItem.currentPeriodBonVent: Aucune période active")
                    null
                }

                val bonVentToToggle = currentPeriodBonVent ?: latestBonVent

                if (bonVentToToggle != null) {
                    val isPrinted = bonVentToToggle.a_etai_imprime_au_moi_ne_foit
                    Log.d("suit_flow", "🖨️ ClientSearchItem.PrintButton: BonVent à toggle - keyID=${bonVentToToggle.keyID.takeLast(4)}, isPrinted ACTUEL=$isPrinted")
                    Log.d("suit_flow", "🎨 ClientSearchItem.RECOMPOSITION: Client=${m2Client.nom}, isPrinted=$isPrinted")

                    Box(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                set(value = bonVentToToggle, key = SemanticsPropertyKey(""))
                            }
                            .size(32.dp)
                    ) {
                        IconButton(
                            onClick = {
                                Log.d("suit_flow", "👆 ClientSearchItem.PrintButton.onClick: Click sur bouton d'impression")
                                Log.d("suit_flow", "📝 ClientSearchItem.PrintButton.onClick: État AVANT - keyID=${bonVentToToggle.keyID.takeLast(4)}, isPrinted=$isPrinted")

                                // Toggle the printed status
                                val updatedBonVent = bonVentToToggle.copy(
                                    a_etai_imprime_au_moi_ne_foit = !isPrinted
                                )

                                Log.d("suit_flow", "🔄 ClientSearchItem.PrintButton.onClick: État APRÈS - keyID=${updatedBonVent.keyID.takeLast(4)}, isPrinted=${updatedBonVent.a_etai_imprime_au_moi_ne_foit}")
                                Log.d("suit_flow", "➡️ ClientSearchItem.PrintButton.onClick: Appel de update_M8BonVent")

                                viewModel.aCentralFacade.repositorysMainSetter.update_M8BonVent(
                                    updatedBonVent
                                )

                                Log.d("suit_flow", "✅ ClientSearchItem.PrintButton.onClick: update_M8BonVent appelé, attente de la mise à jour du Flow")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Toggle impression: ${if (isPrinted) "Imprimé" else "Non imprimé"}",
                                tint = if (isPrinted) Color(0xFF4CAF50) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Status indicator badge
                        if (isPrinted) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .background(
                                        color = Color(0xFF4CAF50),  // Green for printed
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 10.dp.value.toInt().sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Log.d("suit_flow", "⚠️ ClientSearchItem.PrintButton: Aucun BonVent à toggle pour ${m2Client.nom}")
                }

                // Add/Remove from deletion list button
                IconButton(
                    onClick = {
                        Log.d("suit_flow", "👆 ClientSearchItem.DeletionButton.onClick: Toggle liste de suppression pour ${m2Client.nom}, état actuel=$isInDeletionList")

                        val updatedList = if (isInDeletionList) {
                            currentValues.list_clients_por_suprime.filter { it.keyID != m2Client.keyID }
                        } else {
                            currentValues.list_clients_por_suprime + m2Client
                        }
                        focusedValuesGetter.update_activeCentralValues(
                            currentValues.copy(list_clients_por_suprime = updatedList)
                        )

                        Log.d("suit_flow", "✅ ClientSearchItem.DeletionButton.onClick: Liste mise à jour, nouveau état=${!isInDeletionList}")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isInDeletionList) Icons.Default.RemoveCircle else Icons.Default.AddCircle,
                        contentDescription = if (isInDeletionList) "Retirer de la liste" else "Ajouter à la liste",
                        tint = if (isInDeletionList) Color(0xFFFF5722) else Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } ?: run {
        Log.w("suit_flow", "⚠️ ClientSearchItem.Compose: bonVentResult est null pour ${m2Client.nom} - Affichage du placeholder")

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
