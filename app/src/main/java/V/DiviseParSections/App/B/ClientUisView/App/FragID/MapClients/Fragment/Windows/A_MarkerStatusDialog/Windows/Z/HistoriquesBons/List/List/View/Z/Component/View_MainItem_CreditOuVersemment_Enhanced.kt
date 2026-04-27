package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Z.Component

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.EditableAmountField
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@SuppressLint("AutoboxingStateCreation")
@Composable
fun View_MainItem_CreditOuVersemment_Enhanced(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter,
    relative_M8BonVent: M8BonVent,
    max_height: Dp = 100.dp
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoulahadaEdit by remember { mutableStateOf(false) }

    var localVersementFait by remember(relative_M8BonVent.dernierTimeTampsSynchronisationAvecFireBase) { mutableStateOf(relative_M8BonVent.versement_fait) }
    var localCreditFait by remember(relative_M8BonVent.dernierTimeTampsSynchronisationAvecFireBase) {
        mutableStateOf(relative_M8BonVent.credit_fait) }
    var localDemandeVersement by remember(relative_M8BonVent.dernierTimeTampsSynchronisationAvecFireBase) { mutableStateOf(relative_M8BonVent.demande_Versemet_si_Type) }
    var localDemandeVersementRegle by remember(relative_M8BonVent.dernierTimeTampsSynchronisationAvecFireBase) { mutableStateOf(relative_M8BonVent.demande_Versemet_si_Type_est_regle) }
    var localPrintToggle by remember { mutableStateOf(relative_M8BonVent.affiche_le_verssement_au_prochen_print) }
    var localMoulahada by remember(relative_M8BonVent.dernierTimeTampsSynchronisationAvecFireBase)  { mutableStateOf(relative_M8BonVent.moulahada) }

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
        modifier = Modifier.Companion
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = relative_M8BonVent.etateActuellementEst.color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.Companion.fillMaxWidth()) {
            Column(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
            ) {
                // Title + ID — leave leading space so the floating delete button doesn't overlap
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(start = 40.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = relative_M8BonVent.etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Companion.Bold,
                        color = Color.Companion.White
                    )
                    Spacer(modifier = Modifier.Companion.width(6.dp))
                    Text(
                        text = relative_M8BonVent.keyID.takeLast(4),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Companion.White.copy(alpha = 0.8f)
                    )
                }

                // Amount field immediately below the title (no extra spacer)
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
                                        "تم تحديث مبلغ الدفع وربطه بالطلبية ${
                                            previousCommandeBon.keyID.takeLast(
                                                4
                                            )
                                        }"
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
                                    demande_Versemet_si_Type = newAmount,
                                    demande_Versemet_si_Type_est_regle = true
                                )
                                repositorysMainSetter.update_M8BonVent(updatedBonVent)
                                Toast.makeText(
                                    context,
                                    "تم تحديث طلب الدفع",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )

                        Spacer(modifier = Modifier.Companion.height(8.dp))

                        // Toggle button for payment status
                        Row(
                            modifier = Modifier.Companion
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Companion.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.Companion.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (localDemandeVersementRegle) Color.Companion.White else Color.Companion.White.copy(
                                        alpha = 0.5f
                                    ),
                                    modifier = Modifier.Companion.size(20.dp)
                                )
                                Spacer(modifier = Modifier.Companion.width(8.dp))
                                Text(
                                    text = if (localDemandeVersementRegle) "تم التسديد" else "لم يتم التسديد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Companion.White,
                                    fontWeight = if (localDemandeVersementRegle) FontWeight.Companion.Bold else FontWeight.Companion.Normal
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
                                    checkedThumbColor = Color.Companion.White,
                                    checkedTrackColor = Color.Companion.Green,
                                    uncheckedThumbColor = Color.Companion.White.copy(alpha = 0.7f),
                                    uncheckedTrackColor = Color.Companion.Gray
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.Companion.height(4.dp))

                Text(
                    text = "الوقت: ${DatesHandler().getDateAndTimStringAvecSeconds(relative_M8BonVent.creationTimestamps).time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Companion.White.copy(alpha = 0.8f)
                )

                // Moulahada (Notes) Section — always visible
                Spacer(modifier = Modifier.Companion.height(8.dp))

                if (showMoulahadaEdit) {
                    Row(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = localMoulahada,
                            onValueChange = { localMoulahada = it },
                            label = { Text("ملاحظات") },
                            placeholder = { Text("أدخل الملاحظات") },
                            modifier = Modifier.Companion.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Companion.White.copy(alpha = 0.9f),
                                unfocusedContainerColor = Color.Companion.White.copy(alpha = 0.7f)
                            ),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.Companion.width(4.dp))

                        IconButton(
                            onClick = {
                                val updatedBonVent = relative_M8BonVent.copy(
                                    moulahada = localMoulahada
                                )
                                repositorysMainSetter.update_M8BonVent(updatedBonVent)
                                showMoulahadaEdit = false
                                Toast.makeText(
                                    context,
                                    "تم تحديث الملاحظات",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "حفظ",
                                tint = Color.Companion.Green
                            )
                        }

                        IconButton(
                            onClick = {
                                localMoulahada = relative_M8BonVent.moulahada
                                showMoulahadaEdit = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إلغاء",
                                tint = Color.Companion.Red
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .clickable { showMoulahadaEdit = true }
                            .padding(8.dp),
                        verticalAlignment = Alignment.Companion.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل الملاحظات",
                            tint = Color.Companion.White.copy(alpha = 0.7f),
                            modifier = Modifier.Companion.size(16.dp)
                        )
                        Spacer(modifier = Modifier.Companion.width(8.dp))
                        Text(
                            text = "ملاحظات: $localMoulahada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Companion.White,
                            fontWeight = FontWeight.Companion.Medium
                        )
                    }
                }
            }
            // Floating delete button — overlaid at top-start, takes no layout space
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.Companion.align(Alignment.Companion.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = Color.Companion.White
                )
            }
        } // end Box
    } // end Card

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color.Companion.Red
                )
            },
            title = {
                Text(
                    text = "تأكيد الحذف",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Companion.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "هل أنت متأكد من حذف هذه المعاملة؟",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.Companion.height(8.dp))
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
                    Spacer(modifier = Modifier.Companion.height(4.dp))
                    Text(
                        text = "لا يمكن التراجع عن هذا الإجراء",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Companion.Red,
                        fontWeight = FontWeight.Companion.Medium
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
                        containerColor = Color.Companion.Red
                    )
                ) {
                    Text(
                        "حذف نهائي",
                        color = Color.Companion.White,
                        fontWeight = FontWeight.Companion.Bold
                    )
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
