package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PriceEditor(
    currentPrice: Double,
    label: String,
    onPriceUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier,
    showOnlyWhenPositive: Boolean = true,
    additionalInfo: (@Composable () -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempPrixText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Focus and show keyboard when editing starts
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    // Helper function to parse double with both comma and dot as decimal separators
    fun parseDoubleLocalized(text: String): Double? {
        return try {
            // Replace comma with dot for parsing
            val normalizedText = text.replace(',', '.')
            normalizedText.toDoubleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun savePrix() {
        val newPrix = parseDoubleLocalized(tempPrixText) ?: currentPrice
        onPriceUpdate(newPrix)
        isEditing = false
        tempPrixText = "" // Reset to empty after saving
        keyboardController?.hide()
    }

    if (!showOnlyWhenPositive || currentPrice > 0 || isEditing) {
        if (isEditing) {
            OutlinedTextField(
                value = tempPrixText,
                onValueChange = { newValue ->
                    // Allow digits, one dot, and one comma (but not both)
                    val filteredValue = newValue.filter { char ->
                        char.isDigit() || char == '.' || char == ','
                    }

                    // Ensure only one decimal separator
                    val dotCount = filteredValue.count { it == '.' }
                    val commaCount = filteredValue.count { it == ',' }

                    if (dotCount <= 1 && commaCount <= 1 && (dotCount + commaCount) <= 1) {
                        tempPrixText = filteredValue
                    }
                },
                label = { Text("Ancien: $currentPrice DA") },
                placeholder = { Text("Nouveau prix") },
                suffix = { Text("DA") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { savePrix() }
                ),
                singleLine = true,
                modifier = modifier.focusRequester(focusRequester)
            )
        } else {
            Column(modifier = modifier) {
                Text(
                    text = "$label: $currentPrice DA",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    modifier = Modifier.clickable {
                        isEditing = true
                        tempPrixText = "" // Start with empty string
                    }
                )
                // Additional info (like benefit calculation)
                additionalInfo?.invoke()
            }
        }
    }
}
