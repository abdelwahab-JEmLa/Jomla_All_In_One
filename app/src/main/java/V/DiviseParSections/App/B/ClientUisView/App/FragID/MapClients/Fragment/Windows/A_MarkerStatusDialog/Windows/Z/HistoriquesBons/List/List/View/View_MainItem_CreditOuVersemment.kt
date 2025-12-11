package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditableAmountField(
    label: String,
    amount: Double,
    onAmountChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    if (isEditing) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        textValue = newValue
                    }
                },
                label = { Text("${label}: ${String.format("%.2f", amount)} دج") },
                placeholder = { Text("أدخل المبلغ الجديد") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val newAmount = textValue.toDoubleOrNull()
                        if (newAmount != null && newAmount >= 0) {
                            onAmountChange(newAmount)
                            isEditing = false
                            textValue = ""
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    val newAmount = textValue.toDoubleOrNull()
                    if (newAmount != null && newAmount >= 0) {
                        onAmountChange(newAmount)
                        isEditing = false
                        focusManager.clearFocus()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "تأكيد",
                    tint = Color.Green
                )
            }

            IconButton(
                onClick = {
                    textValue = String.format("%.2f", amount)
                    isEditing = false
                    focusManager.clearFocus()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إلغاء",
                    tint = Color.Red
                )
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    } else {
        Row(
            modifier = modifier
                .clickable { isEditing = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label: ${String.format("%.2f", amount)} دج",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "تعديل",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun View_MainItem_CreditOuVersemment_Enhanced(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter,
    relative_M8BonVent: M8BonVent,
) {            //<--
//TODO(1): pk le affiche si  demande_Versemet_si_Type_est_regle et comme c regle normalemen t quand demande_Versemet_si_Type_est_regle ca veut dire non regle
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    var localVersementFait by remember { mutableStateOf(relative_M8BonVent.versement_fait) }
    var localCreditFait by remember { mutableStateOf(relative_M8BonVent.credit_fait) }
    var localDemandeVersement by remember { mutableStateOf(relative_M8BonVent.demande_Versemet_si_Type) }
    var localDemandeVersementRegle by remember { mutableStateOf(relative_M8BonVent.demande_Versemet_si_Type_est_regle) }
    var localPrintToggle by remember { mutableStateOf(relative_M8BonVent.affiche_le_verssement_au_prochen_print) }

    val isVersement = relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Versemment
    val isCredit = relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Credit ||
            relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
    val isDemandeVersement = relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Demande_Versemet

    val previousCommandeBon = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        .filter { bon ->
            bon.parent_M2Client_KeyID == relative_M8BonVent.parent_M2Client_KeyID &&
                    bon.parent_M14VentPeriod_KeyId == relative_M8BonVent.parent_M14VentPeriod_KeyId &&
                    bon.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT &&
                    bon.creationTimestamps < relative_M8BonVent.creationTimestamps
        }
        .maxByOrNull { it.creationTimestamps }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isVersement || isDemandeVersement) 220.dp else 140.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = relative_M8BonVent.etateActuellementEst.color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color.White
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = relative_M8BonVent.etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = relative_M8BonVent.keyID.takeLast(4),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isVersement -> {
                    EditableAmountField(
                        label = "مبلغ الدفع",
                        amount = localVersementFait,
                        onAmountChange = { newAmount ->
                            localVersementFait = newAmount

                            val updatedBonVent = relative_M8BonVent.copy(
                                versement_fait = newAmount,
                                cUn_Versement_duBonVentKey = previousCommandeBon?.keyID ?: ""
                            )
                            repositorysMainSetter.update_M8BonVent(updatedBonVent)

                            Toast.makeText(
                                context,
                                if (previousCommandeBon != null) {
                                    "تم تحديث مبلغ الدفع وربطه بالطلبية ${previousCommandeBon.keyID.takeLast(4)}"
                                } else {
                                    "تم تحديث مبلغ الدفع (لم يتم العثور على طلبية سابقة)"
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                isCredit -> {
                    EditableAmountField(
                        label = "مبلغ القرض",
                        amount = localCreditFait,
                        onAmountChange = { newAmount ->
                            localCreditFait = newAmount
                            val updatedBonVent = relative_M8BonVent.copy(
                                credit_fait = newAmount,
                                cUn_Credit_duBonVentKey = previousCommandeBon?.keyID ?: ""
                            )
                            repositorysMainSetter.update_M8BonVent(updatedBonVent)
                            Toast.makeText(
                                context,
                                "تم تحديث مبلغ القرض",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
                isDemandeVersement -> {
                    EditableAmountField(
                        label = "طلب الدفع",
                        amount = localDemandeVersement,
                        onAmountChange = { newAmount ->
                            localDemandeVersement = newAmount
                            val updatedBonVent = relative_M8BonVent.copy(
                                demande_Versemet_si_Type = newAmount
                                , demande_Versemet_si_Type_est_regle = true
                            )
                            repositorysMainSetter.update_M8BonVent(updatedBonVent)
                            Toast.makeText(
                                context,
                                "تم تحديث طلب الدفع",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Toggle button for payment status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (localDemandeVersementRegle) Color.White else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (localDemandeVersementRegle) "تم التسديد" else "لم يتم التسديد",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = if (localDemandeVersementRegle) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Switch(
                            checked = localDemandeVersementRegle,
                            onCheckedChange = { isChecked ->
                                localDemandeVersementRegle = isChecked
                                val updatedBonVent = relative_M8BonVent.copy(
                                    demande_Versemet_si_Type_est_regle = isChecked
                                )
                                repositorysMainSetter.update_M8BonVent(updatedBonVent)
                                Toast.makeText(
                                    context,
                                    if (isChecked) "تم وضع علامة التسديد" else "تم إلغاء علامة التسديد",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color.Green,
                                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                                uncheckedTrackColor = Color.Gray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الوقت: ${DatesHandler().getDateAndTimStringAvecSeconds(relative_M8BonVent.creationTimestamps).time}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Red
                )
            },
            title = {
                Text(
                    text = "تأكيد الحذف",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "هل أنت متأكد من حذف هذه المعاملة؟",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "رقم المعاملة: ${relative_M8BonVent.keyID.takeLast(6)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "النوع: ${relative_M8BonVent.etateActuellementEst.nomArabe}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لا يمكن التراجع عن هذا الإجراء",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repositorysMainSetter.delete_M8BonVent(relative_M8BonVent)
                        Toast.makeText(
                            context,
                            "تم حذف المعاملة بنجاح",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("حذف نهائي", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("إلغاء", style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }
}
