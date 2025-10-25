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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Add this composable to handle the editable versement/credit display
@Composable
fun EditableAmountField(
    label: String,
    amount: Double,
    onAmountChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(String.format("%.2f", amount)) }

    if (isEditing) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                textValue = newValue
            },
            label = { Text(label) },
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
                    }
                }
            ),
            modifier = modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
            )
        )
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

// Add this composable for the print toggle button
@Composable
fun PrintVersementToggle(
    shouldPrint: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (shouldPrint) {
                Color.Green.copy(alpha = 0.3f)
            } else {
                Color.Gray.copy(alpha = 0.3f)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onToggle(!shouldPrint) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = if (shouldPrint) "إلغاء طباعة الدفع" else "طباعة الدفع",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (shouldPrint) "طباعة الدفع ✓" else "طباعة الدفع",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = if (shouldPrint) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// Enhanced version with both TODOs implemented
@Composable
fun View_MainItem_CreditOuVersemment_Enhanced(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter,
    relative_M8BonVent: M8BonVent,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Track local state for the editable fields
    var localVersementFait by remember { mutableStateOf(relative_M8BonVent.versement_fait) }
    var localCreditFait by remember { mutableStateOf(relative_M8BonVent.credit_fait) }
    var localPrintToggle by remember { mutableStateOf(relative_M8BonVent.affiche_le_verssement_au_prochen_print) }

    val isVersement = relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Versemment
    val isCredit = relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Credit ||
            relative_M8BonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isVersement) 140.dp else 130.dp),
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
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = relative_M8BonVent.etateActuellementEst.nomArabe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = relative_M8BonVent.keyID.takeLast(4),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Editable amount field - changes based on transaction type
            if (isVersement) {
                EditableAmountField(
                    label = "مبلغ الدفع",
                    amount = localVersementFait,
                    onAmountChange = { newAmount ->
                        localVersementFait = newAmount
                        // Update the database
                        val updatedBonVent = relative_M8BonVent.copy(
                            versement_fait = newAmount
                        )
                        repositorysMainSetter.update_M8BonVent(updatedBonVent)
                        Toast.makeText(
                            context,
                            "تم تحديث مبلغ الدفع",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Print toggle button for versement
                PrintVersementToggle(
                    shouldPrint = localPrintToggle,
                    onToggle = { shouldPrint ->
                        localPrintToggle = shouldPrint
                        // Update the database
                        val updatedBonVent = relative_M8BonVent.copy(
                            affiche_le_verssement_au_prochen_print = shouldPrint
                        )
                        repositorysMainSetter.update_M8BonVent(updatedBonVent)
                        Toast.makeText(
                            context,
                            if (shouldPrint) "سيتم طباعة الدفع" else "لن يتم طباعة الدفع",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } else if (isCredit) {
                EditableAmountField(
                    label = "مبلغ القرض",
                    amount = localCreditFait,
                    onAmountChange = { newAmount ->
                        localCreditFait = newAmount
                        // Update the database
                        val updatedBonVent = relative_M8BonVent.copy(
                            credit_fait = newAmount
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

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamp
            Text(
                text = "الوقت: ${DatesHandler().getDateAndTimStringAvecSeconds(relative_M8BonVent.creationTimestamps).time}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
