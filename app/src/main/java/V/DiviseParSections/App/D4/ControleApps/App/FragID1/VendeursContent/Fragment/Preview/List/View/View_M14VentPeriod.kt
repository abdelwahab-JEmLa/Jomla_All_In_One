package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun View_M14VentPeriod(
    viewModel: ViewModel_M14VentPeriod,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    relative_M14VentPeriode: M14VentPeriode,
    relative_M9AppCompt: Z_AppCompt?,
) {
    // State for showing delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State for editing fields
    var editingField by remember { mutableStateOf<String?>(null) }
    var editingValue by remember { mutableStateOf("") }

    // Focus requester for auto-focus
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun M14VentPeriode.delete(): Unit {
        repositorysMainSetter.delete(this)
    }

    fun M14VentPeriode.handel_Clavie_Donne(): Unit {
        repositorysMainSetter.update_M14VentPeriode(this)
    }

    // Function to start editing a field
    fun startEditing(fieldName: String, currentValue: Double) {
        editingField = fieldName
        editingValue = currentValue.toString()
    }

    // Function to save edited value
    fun saveEditedValue() {
        val newValue = editingValue.toDoubleOrNull() ?: 0.0
        val updatedPeriode = when (editingField) {
            "credit_vents" -> relative_M14VentPeriode.copy(credit_Vents_Totale = newValue)
            "cash_vents" -> relative_M14VentPeriode.copy(cash_Vents_Totale = newValue)
            "credit_achats" -> relative_M14VentPeriode.copy(credit_achats_Totale = newValue)
            "cash_achats" -> relative_M14VentPeriode.copy(cash_achats_Totale = newValue)
            else -> relative_M14VentPeriode
        }
        updatedPeriode.handel_Clavie_Donne()
        editingField = null
        keyboardController?.hide()
    }

    // Auto-focus when editing starts
    LaunchedEffect(editingField) {
        if (editingField != null) {
            focusRequester.requestFocus()
        }
    }

    val active_M14VentPeriode = relative_M9AppCompt?.current_OnVent_M14VentPeriode_KeyID
    val active = (active_M14VentPeriode ?: "") == relative_M14VentPeriode.keyID

    val backgroundColor = when {
        active -> Color.Red
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette période de vente ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        relative_M14VentPeriode.delete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (relative_M9AppCompt != null) {
                    aCentralFacade.repositorysMainSetter.update_M9AppCompt(
                        relative_M9AppCompt.copy(
                            current_OnVent_M14VentPeriode_KeyID = relative_M14VentPeriode.keyID,
                            current_OnVent_M14VentPeriode_DebugInfos = relative_M14VentPeriode.get_DebugInfos()
                        )
                    )
                }
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (active) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete button with icon
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "m14VentPeriode: ${relative_M14VentPeriode.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )


        }

        Text(
            text = "Heure de début: $heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Financial data display/editing section
        Column {
            // Credit Ventes
            if (editingField == "credit_vents") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = { editingValue = it },
                    label = { Text("Crédit Ventes Total") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { saveEditedValue() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            } else {
                Text(
                    text = "Crédit Ventes Total: ${relative_M14VentPeriode.credit_Vents_Totale}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        startEditing("credit_vents", relative_M14VentPeriode.credit_Vents_Totale)
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Cash Ventes
            if (editingField == "cash_vents") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = { editingValue = it },
                    label = { Text("Cash Ventes Total") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { saveEditedValue() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            } else {
                Text(
                    text = "Cash Ventes Total: ${relative_M14VentPeriode.cash_Vents_Totale}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        startEditing("cash_vents", relative_M14VentPeriode.cash_Vents_Totale)
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Credit Achats
            if (editingField == "credit_achats") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = { editingValue = it },
                    label = { Text("Crédit Achats Total") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { saveEditedValue() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            } else {
                Text(
                    text = "Crédit Achats Total: ${relative_M14VentPeriode.credit_achats_Totale}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        startEditing("credit_achats", relative_M14VentPeriode.credit_achats_Totale)
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Cash Achats
            if (editingField == "cash_achats") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = { editingValue = it },
                    label = { Text("Cash Achats Total") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { saveEditedValue() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            } else {
                Text(
                    text = "Cash Achats Total: ${relative_M14VentPeriode.cash_achats_Totale}",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        startEditing("cash_achats", relative_M14VentPeriode.cash_achats_Totale)
                    }
                )
            }
        }
    }
}
