package V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun OutlinedText_Avec_Init_Click_Button_Modulable_Proto3(
    start_count: Int,
    au_depot: Int = 0,
    standard_count: Int = 1,
    start_au_premier_click_par_add_outlined: Boolean = false,
    icon: ImageVector? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    modifier: Modifier = Modifier,
    on_Data_Update: (Int) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var quantityInput by remember(start_count) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
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

    // FIXED: When start==0, first click shows outlined text field for keyboard input
    if (isEditMode) {
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

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FIXED: Depot count card shown before the icon - red background, black text, 30% smaller
            if (au_depot > 0) {
                Card(
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
                        // Click behavior based on start_au_premier_click_par_add_outlined parameter
                        when {
                            start_count == 0 -> {
                                if (start_au_premier_click_par_add_outlined) {
                                    // Show outlined text field on first click
                                    isEditMode = true
                                } else {
                                    // Add standard_count directly
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
                    // Only show icon if not null
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
