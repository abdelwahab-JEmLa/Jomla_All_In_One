package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.B.List.UI.QuantityButton_T1
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun QuantityGrid_Carton(
    old_quantity: Int,
    on_Dismiss_Confirme_New_Quantity: (Int) -> Unit,
    setIN_Vent_Its_Quantity_Represent: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent,
    quantite_Boit_Par_Carton: Int,
) {
    var showExtendedRange by remember { mutableStateOf(false) }
    var showManualInput by remember { mutableStateOf(false) }
    var manualInputValue by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val basicQuantities = remember {
        listOf(
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            14,
            15,
            20,
            21,
            22,
            23,
            24,
            25,
            30,
            40,
            50
        )
    }

    val extendedQuantities = remember {
        (0..50).toList()
    }

    val quantities = if (showExtendedRange) extendedQuantities else basicQuantities

    // Auto-focus the text field when manual input is shown
    LaunchedEffect(showManualInput) {
        if (showManualInput) {
            focusRequester.requestFocus()
        }
    }

    fun handleManualInput() {
        val inputQuantity = manualInputValue.toIntOrNull()
        if (inputQuantity != null && inputQuantity >= 0) {
            val finalQuantity = when (setIN_Vent_Its_Quantity_Represent) {
                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
                    inputQuantity * quantite_Boit_Par_Carton
                }
                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
                    inputQuantity
                }
            }
            on_Dismiss_Confirme_New_Quantity(finalQuantity)
        }
        showManualInput = false
        manualInputValue = ""
        keyboardController?.hide()
    }

    Column {
        // Control Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (showExtendedRange) "All Numbers (0-50)" else "Quick Select",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Manual Input Button
                OutlinedButton(
                    onClick = {
                        showManualInput = !showManualInput
                        if (showManualInput) {
                            // Initialize with current quantity for editing
                            val currentDisplayQuantity = when (setIN_Vent_Its_Quantity_Represent) {
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
                                    old_quantity / quantite_Boit_Par_Carton
                                }
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
                                    old_quantity
                                }
                            }
                            manualInputValue = if (currentDisplayQuantity > 0) currentDisplayQuantity.toString() else ""
                        }
                    },
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Manual input",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Enter",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Expand/Collapse Button
                OutlinedButton(
                    onClick = { showExtendedRange = !showExtendedRange },
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = if (showExtendedRange) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showExtendedRange) "Show less" else "Show more",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showExtendedRange) "Less" else "More",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Manual Input Field
        if (showManualInput) {
            OutlinedTextField(
                value = manualInputValue,
                onValueChange = {
                    // Only allow numeric input
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        manualInputValue = it
                    }
                },
                label = {
                    Text(
                        text = when (setIN_Vent_Its_Quantity_Represent) {
                            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton ->
                                "Enter quantity (cartons)"
                            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit ->
                                "Enter quantity (units)"
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        handleManualInput()
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
                supportingText = {
                    val displayQuantity = manualInputValue.toIntOrNull() ?: 0
                    val finalQuantity = when (setIN_Vent_Its_Quantity_Represent) {
                        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton -> {
                            displayQuantity * quantite_Boit_Par_Carton
                        }
                        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit -> {
                            displayQuantity
                        }
                    }
                    if (displayQuantity > 0) {
                        Text(
                            text = when (setIN_Vent_Its_Quantity_Represent) {
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton ->
                                    "= $finalQuantity units total"
                                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit ->
                                    "Final quantity: $finalQuantity"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        // Quantity Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(
                if (showExtendedRange) 280.dp
                else if (showManualInput) 160.dp
                else 200.dp
            )
        ) {
            items(quantities.size) { index ->
                val quantityNumber = quantities[index]
                QuantityButton_T1(
                    newQuantity = quantityNumber,
                    modifier = Modifier.fillMaxWidth(),
                    isSelected = quantityNumber == old_quantity,
                    setIN_Vent_Its_Quantity_Represent = setIN_Vent_Its_Quantity_Represent,
                    quantite_Boit_Par_Carton = quantite_Boit_Par_Carton,
                    onClick = on_Dismiss_Confirme_New_Quantity,
                )
            }
        }
    }
}
