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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * A reusable component that displays a quantity button with two-click behavior:
 * - First click: Updates to standard_count and triggers onFirstClick
 * - Second click: Enters edit mode and shows an outlined text field
 *
 * @param start_count The initial/current count to display
 * @param standard_count The count to set on first click (default: 1)
 * @param icon The icon to display in the button
 * @param enabled Whether the component is enabled for interaction (default: true)
 * @param on_Data_Update Callback when quantity needs to be updated (returns new quantity)
 * @param modifier Optional modifier for the component
 */
@Composable
fun OutlinedText_Avec_Init_Click_Button_Modulable_Proto2(
    start_count: Int,
    standard_count: Int = 1,
    icon: ImageVector = Icons.Default.ShoppingCart,
    enabled: Boolean = true,
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

    if (isEditMode && start_count > 0) {
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
            textStyle = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            enabled = enabled
        )
    } else {
        // Display mode: Show clickable card
        val containerColor = if (!enabled) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        } else if (start_count > 0) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.primary
        }

        val contentColor = if (!enabled) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.onPrimary
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            modifier = modifier.clickable(enabled = enabled) {
                when {
                    start_count == 0 -> {
                        // First click: Set to standard_count
                        on_Data_Update(standard_count)
                    }
                    start_count >= standard_count -> {
                        // Second click: Enter edit mode
                        isEditMode = true
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Quantity",
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = start_count.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Extended version with explicit disabled state handling
 * This version makes it clearer when items are unavailable
 */
@Composable
fun OutlinedText_Avec_Init_Click_Button_WithAvailability(
    start_count: Int,
    standard_count: Int = 1,
    icon: ImageVector = Icons.Default.ShoppingCart,
    isAvailable: Boolean = true,
    modifier: Modifier = Modifier,
    on_Data_Update: (Int) -> Unit
) {
    OutlinedText_Avec_Init_Click_Button_Modulable_Proto2(
        start_count = start_count,
        standard_count = standard_count,
        icon = icon,
        enabled = isAvailable,
        modifier = modifier,
        on_Data_Update = on_Data_Update
    )
}
