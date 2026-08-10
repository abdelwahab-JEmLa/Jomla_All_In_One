package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.DropdownItems

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Dropdown item — "دين + تسديد" (new credit & versement combined).
 */
@Composable
fun DropdownItem_CreditEtVersement(
    aCentralFacade: ACentralFacade,
    relative_M2Client: M2Client,
    isActive: Boolean,
    onActivate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent

    val latestSituationMontant: Int? = remember(relative_M2Client.keyID, repo8BonVent.datasValue) {
        repo8BonVent.datasValue
            .filter {
                it.parent_M2Client_KeyID == relative_M2Client.keyID &&
                it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            }
            .maxByOrNull { it.creationTimestamps }
            ?.montant_principale_du_type?.toInt()
    }

    var out_credit by remember { mutableStateOf("") }
    var out_versement by remember { mutableStateOf("") }
    var moulahadaText by remember { mutableStateOf("") }
    var showMoulahadaField by remember { mutableStateOf(false) }

    val creditFocusRequester = remember { FocusRequester() }
    val moulahadaFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) creditFocusRequester.requestFocus()
        else {
            out_credit = ""
            out_versement = ""
            moulahadaText = ""
            showMoulahadaField = false
        }
    }

    fun buildAndSave() {
        val currentPeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode ?: return
        val currentCompt = focusedValuesGetter.currentActive_M9AppCompt ?: return
        val baseTs = System.currentTimeMillis()

        val creditVal = out_credit.toDoubleOrNull() ?: 0.0
        val versementVal = out_versement.toDoubleOrNull() ?: 0.0

        if (creditVal > 0.0) {
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
                credit_fait = creditVal,
                moulahada = moulahadaText,
            )
            aCentralFacade.repositorysMainSetter.update_M8BonVent(creditBon)
        }

        if (versementVal > 0.0) {
            val versementBon = M8BonVent.get_default(
                parent_M9AppCompt_KeyID = currentCompt.keyID,
                parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
                parent_M14VentPeriod_KeyId = currentPeriod.keyID,
                parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
                parent_M2Client_KeyID = relative_M2Client.keyID,
                parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
                etateActuellementEst = M8BonVent.EtateActuellementEst.Versemment,
            ).copy(
                creationTimestamps = baseTs + 1_000L,
                versement_fait = versementVal,
                moulahada = moulahadaText,
            )
            aCentralFacade.repositorysMainSetter.update_M8BonVent(versementBon)
        }

        val newSituation = M8BonVent.get_default(
            parent_M9AppCompt_KeyID = currentCompt.keyID,
            parent_M9AppCompt_DebugInfos = currentCompt.get_DebugInfos(),
            parent_M14VentPeriod_KeyId = currentPeriod.keyID,
            parent_M14VentPeriod_DebugInfos = currentPeriod.get_DebugInfos(),
            parent_M2Client_KeyID = relative_M2Client.keyID,
            parent_M2Client_DebugInfos = relative_M2Client.get_DebugInfos(),
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
        ).copy(
            creationTimestamps = baseTs + 2_000L,
            montant_principale_du_type = (latestSituationMontant?.toDouble() ?: 0.0) + creditVal - versementVal,
            moulahada = moulahadaText,
        )

        aCentralFacade.repositorysMainSetter.update_M8BonVent(newSituation)
        onDismiss()
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.TextIncrease,
                contentDescription = null,
                tint = Color(0xFFFF9800),
            )
        },
        text = {
            if (isActive) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = out_credit,
                        onValueChange = { input -> if (input.all { it.isDigit() }) out_credit = input },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        label = { Text("مبلغ الدين الجديد", style = MaterialTheme.typography.labelSmall) },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(creditFocusRequester),
                    )

                    Spacer(Modifier.height(4.dp))

                    OutlinedTextField(
                        value = out_versement,
                        onValueChange = { input -> if (input.all { it.isDigit() }) out_versement = input },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { buildAndSave() }),
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
                        label = { Text("مبلغ التسديد", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (showMoulahadaField) {
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = moulahadaText,
                            onValueChange = { moulahadaText = it },
                            singleLine = true,
                            label = { Text("ملاحظة", style = MaterialTheme.typography.labelSmall) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { buildAndSave() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(moulahadaFocusRequester),
                        )
                        LaunchedEffect(showMoulahadaField) {
                            if (showMoulahadaField) moulahadaFocusRequester.requestFocus()
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    val finalSituation = (latestSituationMontant ?: 0) + (out_credit.toIntOrNull() ?: 0) - (out_versement.toIntOrNull() ?: 0)
                    Text(
                        text = "الرصيد النهائي: $finalSituation دج",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = { buildAndSave() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Text("حفظ", style = MaterialTheme.typography.labelMedium)
                    }
                }
            } else {
                Text(
                    text = "دين + تسديد معاً",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onClick = { if (!isActive) onActivate() },
    )
}
