package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.DropdownItems

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Dropdown item — "دين جديد" (new credit).
 *
 * @param isActive    true when this item is in editing mode (controlled by parent)
 * @param onActivate  called when the user taps the item; parent should set isActive = true
 * @param onDismiss   called after successful commit to close the dropdown
 */
@Composable
fun DropdownItem_Credit(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    isActive: Boolean,
    onActivate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent

    // Latest New_Situation_Credit montant for this client
    val latestSituationMontant: Int? = remember(relative_M2Client.keyID, repo8BonVent.datasValue) {
        repo8BonVent.datasValue
            .filter {
                it.parent_M2Client_KeyID == relative_M2Client.keyID &&
                it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            }
            .maxByOrNull { it.creationTimestamps }
            ?.montant_principale_du_type?.toInt()
    }

    var out_val by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf(0.0) }
    var moulahadaText by remember { mutableStateOf("") }
    var showMoulahadaField by remember { mutableStateOf(false) }
    var displayedMontant by remember(latestSituationMontant) {
        mutableStateOf<Int?>(latestSituationMontant)
    }
    val focusRequester = remember { FocusRequester() }
    val moulahadaFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) focusRequester.requestFocus()
        else {
            out_val = ""
            moulahadaText = ""
            showMoulahadaField = false
        }
    }

    fun buildAndSave() {
        val currentPeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode ?: return
        val currentCompt = focusedValuesGetter.currentActive_M9AppCompt ?: return
        val baseTs = System.currentTimeMillis()

        val creditBon = M8BonVent.get_default(
            parent_M9AppCompt_KeyID = currentCompt.keyID,
            parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
            parent_M14VentPeriod_KeyId = currentPeriod.keyID,
            parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
            parent_M2Client_KeyID = relative_M2Client.keyID,
            parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
            etateActuellementEst = M8BonVent.EtateActuellementEst.Credit,
        ).copy(
            creationTimestamps = baseTs,
            credit_fait = montant,
            moulahada = moulahadaText,
        )

        val newSituation = M8BonVent.get_default(
            parent_M9AppCompt_KeyID = currentCompt.keyID,
            parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
            parent_M14VentPeriod_KeyId = currentPeriod.keyID,
            parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
            parent_M2Client_KeyID = relative_M2Client.keyID,
            parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
        ).copy(
            creationTimestamps = baseTs + 1_000L,
            montant_principale_du_type = (latestSituationMontant?.toDouble() ?: 0.0) + montant,
            moulahada = moulahadaText,
        )

        aCentralFacade.repositorysMainSetter.update_M8BonVent(creditBon)
        aCentralFacade.repositorysMainSetter.update_M8BonVent(newSituation)
        onDismiss()
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.TextIncrease,
                contentDescription = null,
                tint = Color(0xFFE53935),
            )
        },
        text = {
            if (isActive) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = out_val,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) out_val = input
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val parsed = out_val.toIntOrNull()
                                if (parsed != null) {
                                    montant = parsed.toDouble()
                                    displayedMontant = parsed
                                }
                                out_val = displayedMontant?.toString() ?: ""
                                buildAndSave()
                            }
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { showMoulahadaField = !showMoulahadaField }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "إضافة ملاحظة",
                                    tint = if (moulahadaText.isNotEmpty()) Color(0xFF43A047) else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        label = {
                            val diff = (out_val.toIntOrNull() ?: 0) + (displayedMontant ?: 0)
                            Text(
                                text = "الرصيد الجديد — $diff",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    )

                    if (showMoulahadaField) {
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = moulahadaText,
                            onValueChange = { moulahadaText = it },
                            singleLine = true,
                            label = { Text("ملاحظة", style = MaterialTheme.typography.labelSmall) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val parsed = out_val.toIntOrNull()
                                    if (parsed != null) {
                                        montant = parsed.toDouble()
                                        displayedMontant = parsed
                                    }
                                    out_val = displayedMontant?.toString() ?: ""
                                    buildAndSave()
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(moulahadaFocusRequester),
                        )
                        LaunchedEffect(showMoulahadaField) {
                            if (showMoulahadaField) moulahadaFocusRequester.requestFocus()
                        }
                    }
                }
            } else {
                Text(
                    text = "دين جديد: ${displayedMontant ?: "-"} دج",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onClick = { if (!isActive) onActivate() },
    )
}
