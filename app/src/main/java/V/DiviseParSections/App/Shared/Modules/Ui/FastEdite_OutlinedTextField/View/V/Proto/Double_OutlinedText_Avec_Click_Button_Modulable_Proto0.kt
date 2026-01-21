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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adjustable icon component that can be customized in size and color
 *
 * @param icon The icon vector to display
 * @param size The size of the icon (default: 16.dp)
 * @param tint The color tint of the icon
 * @param modifier Optional modifier
 */
@Composable
fun Icon_Outlined(
    icon: ImageVector,
    size: Dp = 16.dp,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size)
    )
}

/**
 * A reusable component that displays a quantity button with two-click behavior:
 * - First click: Updates to standard_count and triggers on_Data_Update
 * - Second click: Enters edit mode and shows an outlined text field
 *
 * @param start_count The initial/current count to display
 * @param standard_count The count to set on first click (default: 1.0)
 * @param iconComposable Optional composable for custom icon (uses Icon_Outlined internally)
 * @param isAvailable Whether the component is enabled for interaction (default: true)
 * @param compact_taille Whether to use compact sizing (reduces padding and text size)
 * @param on_Data_Update Callback when quantity needs to be updated (returns new quantity as Double)
 * @param modifier Optional modifier for the component
 */
@Composable
fun Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
    start_count: Double,
    standard_count: Double = 1.0,
    iconComposable: @Composable (() -> Unit)? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    modifier: Modifier = Modifier,
    on_Data_Update: (Double) -> Unit
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
    val textStyle = if (compact_taille) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    if (isEditMode && start_count > 0) {
        // Edit mode: Show outlined text field
        OutlinedTextField(
            value = quantityInput,
            onValueChange = { newValue ->
                // Allow decimal numbers
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    quantityInput = newValue
                }
            },
            modifier = modifier
                .width(80.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val newQuantity = quantityInput.toDoubleOrNull() ?: 0.0
                    on_Data_Update(newQuantity)
                    isEditMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled = isAvailable
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

        Card(
            modifier = modifier
                .clickable(enabled = isAvailable) {
                    when {
                        start_count == 0.0 -> {
                            on_Data_Update(standard_count)
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
                // Display custom icon composable if provided
                iconComposable?.invoke()

                Text(
                    text = String.format("%.2f", start_count),
                    style = textStyle,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}
