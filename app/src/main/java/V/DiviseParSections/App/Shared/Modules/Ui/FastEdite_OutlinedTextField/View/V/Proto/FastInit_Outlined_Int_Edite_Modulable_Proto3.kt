package V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FastInit_Outlined_Int_Edite_Modulable_Proto3(
    start_count: Int,
    au_depot: Int = 0,
    standard_count: Int = 1,
    start_au_premier_click_par_add_outlined: Boolean = false,
    icon: ImageVector? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    show_depot_card_on_top_in_flow_row: Boolean = false,
    is_admin: Boolean = false,
    // FIXED: Added parameter to control spacing between depot and sale button
    add_spacing_between_depot_and_sale: Boolean = false,
    on_admin_depot_update: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    on_Data_Update: (Int) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var quantityInput by remember(start_count) { mutableStateOf("") }
    var isEditDepotMode by remember { mutableStateOf(false) }
    var depotInput by remember(au_depot) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val depotFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditDepotMode) {
        if (isEditDepotMode) {
            depotFocusRequester.requestFocus()
        }
    }

    // Adjust sizes based on compact mode
    val horizontalPadding = if (compact_taille) 8.dp else 12.dp
    val verticalPadding = if (compact_taille) 4.dp else 6.dp
    val iconSize = if (compact_taille) 14.dp else 16.dp
    val textStyle = if (compact_taille) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    // FIXED: Calculate spacing based on parameter to prevent accidental clicks
    val spacingBetweenCards = if (add_spacing_between_depot_and_sale) 8.dp else 4.dp

    // FIXED: Depot edit mode - admin can edit depot count directly
    if (isEditDepotMode) {
        OutlinedTextField(
            value = depotInput,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    depotInput = newValue
                }
            },
            modifier = modifier
                .width(80.dp)
                .focusRequester(depotFocusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val newDepotCount = depotInput.toIntOrNull() ?: 0
                    on_admin_depot_update(newDepotCount)
                    isEditDepotMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            label = { Text("Dépôt") }
        )
    } else if (isEditMode) {
        // Edit mode: Show outlined text field
        OutlinedTextField(
            value = quantityInput,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    quantityInput = newValue
                }
            },
            modifier = modifier
                .width(80.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val newQuantity = quantityInput.toIntOrNull() ?: 0
                    on_Data_Update(newQuantity)
                    isEditMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled = isAvailable,
            // FIXED: Display depot count as hint when available
            placeholder = if (au_depot > 0) {
                { Text("Dépôt: $au_depot", style = textStyle.copy(fontWeight = FontWeight.Normal)) }
            } else null
        )
    } else {
        // Display mode: Show clickable card
        val containerColor = if (!isAvailable) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        } else if (start_count > 0) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.primary
        }

        val contentColor = if (!isAvailable) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.onPrimary
        }

        if (show_depot_card_on_top_in_flow_row) {
            // FIXED: FlowRow layout with increased spacing to prevent accidental clicks
            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(spacingBetweenCards),
                maxItemsInEachRow = 2
            ) {
                // Depot count card shown first (will be on top in flow)
                if (au_depot > 0) {
                    Card(
                        modifier = Modifier
                            .clickable(enabled = is_admin) {
                                isEditDepotMode = true
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = horizontalPadding * 0.7f,
                                vertical = verticalPadding * 0.7f
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // FIXED: Added warehouse icon to depot card when in FlowRow
                            Icon(
                                imageVector = Icons.Default.Warehouse,
                                contentDescription = "Dépôt",
                                tint = Color.Black,
                                modifier = Modifier.size((iconSize.value * 0.7f).dp)
                            )
                            Text(
                                text = au_depot.toString(),
                                style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Main quantity card
                Card(
                    modifier = Modifier
                        .clickable(enabled = isAvailable) {
                            when {
                                start_count == 0 -> {
                                    if (start_au_premier_click_par_add_outlined) {
                                        isEditMode = true
                                    } else {
                                        on_Data_Update(standard_count)
                                    }
                                }
                                start_count >= standard_count -> {
                                    isEditMode = true
                                }
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = verticalPadding
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Quantity",
                                tint = contentColor,
                                modifier = Modifier.size(iconSize)
                            )
                        }

                        Text(
                            text = start_count.toString(),
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        } else {
            // FIXED: Original Row layout with increased spacing to prevent accidental clicks
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FIXED: Depot count card shown before the icon - red background, black text, 30% smaller
                if (au_depot > 0) {
                    Card(
                        modifier = Modifier
                            .clickable(enabled = is_admin) {
                                isEditDepotMode = true
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = horizontalPadding * 0.7f,
                                vertical = verticalPadding * 0.7f
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = au_depot.toString(),
                                style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Main quantity card
                Card(
                    modifier = Modifier
                        .clickable(enabled = isAvailable) {
                            when {
                                start_count == 0 -> {
                                    if (start_au_premier_click_par_add_outlined) {
                                        isEditMode = true
                                    } else {
                                        on_Data_Update(standard_count)
                                    }
                                }
                                start_count >= standard_count -> {
                                    isEditMode = true
                                }
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = verticalPadding
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Quantity",
                                tint = contentColor,
                                modifier = Modifier.size(iconSize)
                            )
                        }

                        Text(
                            text = start_count.toString(),
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
