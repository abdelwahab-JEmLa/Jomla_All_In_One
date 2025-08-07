package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.ButtonAutreEtates
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

// Replace your existing Credit button section in AfficheurRegleOuvert with this:
@Composable
fun AfficheurRegleOuvert(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    relative_Client: M2Client?,
    onPourEdite_Gps_Client: (M2Client) -> Unit,
    extracted: (M8BonVent) -> Unit,
    extracted_2: () -> Unit,
) {
    val clientId = relative_Client?.id ?: 0L

    // Local state for credit payment dialog

    fun getLatestTransactionForClient(clientId: Long): M8BonVent? {
        return uiState
            .c3_TransactionCommercialList
            .filter { it.parent_M2Client_OldLongID == clientId }
            .maxByOrNull { it.creationTimestamps }
    }

    val latestTransaction = relative_Client?.let { getLatestTransactionForClient(it.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("test")
        ) {
            val activeCompt = viewModel.getter.repo9AppCompt.currentAppCompt

            activeCompt?.let { activeCompt ->
                val relatedClientactiveTransaction =
                    viewModel.bProto_ClientsDataBase.find {
                        it.id == activeCompt.vid
                    }
                Text(
                    text = "ماهو تقرير الزبون السابق ${
                        relative_Client?.nom
                            ?: relatedClientactiveTransaction?.nom
                            ?: ""
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "وقت البدء: ${latestTransaction?.heurDebutInString ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "الحالة الحالية: ${latestTransaction?.etateActuellementEst?.nomArabe ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )

                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                    .ButtonAutreEtates(
                        viewModel = viewModel,
                        clickedClient = clientId,
                    ) { relative_M8BonVent ->
                    }


                val lastCommande_Transaction =
                    repositorysMainGetter.repo8BonVent.datasValue.lastOrNull {
                        it.parent_M2Client_KeyID == relative_Client?.keyID
                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }

                Card(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(value = lastCommande_Transaction, key = SemanticsPropertyKey(""))
                        },
                ) {
                    M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                        .ButtonAutreEtates(
                            viewModel = viewModel,
                            clickedClient = clientId,
                        ) { relative_M8BonVent ->
                        }
                }
                // Credit button with local dialog handling
                M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                    .ButtonAutreEtates(
                        viewModel = viewModel,
                        clickedClient = clientId,
                    ) { relative_M8BonVent ->
                        // Show the dropdown payment input
                        extracted(relative_M8BonVent)
                        extracted_2()
                    }

                TextButton(
                    onClick = {
                        viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق الفاتورة مع عدم وضع اي تقرير")
                }
            }
        }
    }


}

@Composable
fun CreditPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var versementText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إدخال مبلغ الدفع")
        },
        text = {
            Column {
                Text(
                    text = "أدخل مبلغ الدفع للمعاملة الائتمانية",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = versementText,
                    onValueChange = { versementText = it },
                    label = { Text("مبلغ الدفع") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                                else Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Toggle dropdown"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick amount selection dropdown
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val quickAmounts = listOf(100.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0)
                    quickAmounts.forEach { amount ->
                        DropdownMenuItem(
                            text = { Text("${amount.toInt()} دج") },
                            onClick = {
                                versementText = amount.toString()
                                expanded = false
                            }
                        )
                    }

                    // Custom amount option
                    DropdownMenuItem(
                        text = { Text("مبلغ مخصص") },
                        onClick = {
                            versementText = ""
                            expanded = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = versementText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    }
                },
                enabled = versementText.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("تأكيد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
