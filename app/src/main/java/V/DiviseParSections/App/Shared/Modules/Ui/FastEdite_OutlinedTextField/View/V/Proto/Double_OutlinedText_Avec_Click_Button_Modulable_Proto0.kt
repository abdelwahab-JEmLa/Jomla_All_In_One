package V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data class for icon configuration
 * FIXED: Renamed parameter from imageVector to icon to match function signature
 */
data class Icon_Outlined(
    val icon: ImageVector,  // FIXED: Changed from imageVector to icon
    val size: Dp = 16.dp,
    val color: Color = Color.Unspecified
)

/**
 * Adjustable icon component that can be customized in size and color
 *
 * @param iconConfig Configuration object for the icon
 * @param modifier Optional modifier
 */
@Composable
fun Icon_Outlined_Display(
    iconConfig: Icon_Outlined,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = iconConfig.icon,
        contentDescription = null,
        tint = if (iconConfig.color != Color.Unspecified) {
            iconConfig.color
        } else {
            MaterialTheme.colorScheme.onPrimary
        },
        modifier = modifier.size(iconConfig.size)
    )
}

/**
 * A reusable component that displays a quantity button with two-click behavior:
 * - First click: Updates to standard_count and triggers on_Data_Update
 * - Second click: Enters edit mode and shows an outlined text field
 *
 * @param value The initial/current value to display
 * @param standard_count The count to set on first click (default: 1.0)
 * @param Icon_Outlined_p0 Optional icon configuration object
 * @param isAvailable Whether the component is enabled for interaction (default: true)
 * @param compact_taille Whether to use compact sizing (reduces padding and text size)
 * @param textSize Optional custom text size (overrides the default compact/normal size)
 * @param showDecimals Whether to show decimal places (default: true)
 * @param decimalPlaces Number of decimal places to show when showDecimals is true (default: 2)
 * @param onValueChanged Callback when value needs to be updated (returns new value as Double)
 * @param modifier Optional modifier for the component
 */
@Composable
fun Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
    value: Double,
    standard_count: Double = 1.0,
    Icon_Outlined_p0: Icon_Outlined? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    textSize: TextUnit? = null,
    showDecimals: Boolean = true,
    decimalPlaces: Int = 2,
    modifier: Modifier = Modifier,
    onValueChanged: (Double) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var quantityInput by remember(value) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
        }
    }

    // Adjust sizes based on compact mode
    val horizontalPadding = if (compact_taille) 2.dp else 12.dp
    val verticalPadding = if (compact_taille) 2.dp else 6.dp

    // Use custom textSize if provided, otherwise fall back to defaults
    val effectiveTextSize = textSize ?: if (compact_taille) 7.sp else 12.sp

    val textStyle = MaterialTheme.typography.labelMedium.copy(fontSize = effectiveTextSize)

    // FIXED: Format the display text based on showDecimals parameter
    val displayText = if (showDecimals) {
        String.format("%.${decimalPlaces}f", value)
    } else {
        // If the number is a whole number, show without decimals
        if (value % 1.0 == 0.0) {
            value.toInt().toString()
        } else {
            // If it has decimals, show with specified decimal places
            String.format("%.${decimalPlaces}f", value)
        }
    }

    if (isEditMode && value > 0) {
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
                    onValueChanged(newQuantity)
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
        } else if (value > 0) {
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
            // FIXED: Now the Card uses the passed modifier which can include fillMaxWidth()
            modifier = modifier
                .clickable(enabled = isAvailable) {
                    when {
                        value == 0.0 -> {
                            onValueChanged(standard_count)
                        }
                        value >= standard_count -> {
                            isEditMode = true
                        }
                    }
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Row(
                // FIXED: Row now fills the width of its parent Card
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = horizontalPadding,
                        vertical = verticalPadding
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // FIXED: Display icon using the config object if provided
                Icon_Outlined_p0?.let { iconConfig ->
                    Icon_Outlined_Display(
                        iconConfig = iconConfig,
                        modifier = Modifier
                    )
                }

                Text(
                    text = displayText,
                    style = textStyle,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}
