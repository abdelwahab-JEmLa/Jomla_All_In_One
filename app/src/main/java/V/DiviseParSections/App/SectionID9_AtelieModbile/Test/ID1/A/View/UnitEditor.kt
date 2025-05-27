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
fun UnitEditor(
    currentUnits: Int,
    label: String,
    onUnitsUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showOnlyWhenPositive: Boolean = false,
    additionalInfo: (@Composable () -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempUnitsText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Focus and show keyboard when editing starts
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    fun saveUnits() {
        val newUnits = tempUnitsText.toIntOrNull() ?: currentUnits
        // Ensure units are not negative
        val validUnits = if (newUnits < 0) 0 else newUnits
        onUnitsUpdate(validUnits)
        isEditing = false
        tempUnitsText = "" // Reset to empty after saving
        keyboardController?.hide()
    }

    if (!showOnlyWhenPositive || currentUnits > 0 || isEditing) {
        if (isEditing) {
            OutlinedTextField(
                value = tempUnitsText,
                onValueChange = { 
                    // Only allow numeric input
                    if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                        tempUnitsText = it
                    }
                },
                label = { Text("Ancien: $currentUnits") },
                placeholder = { Text("Nombre d'unités") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { saveUnits() }
                ),
                singleLine = true,
                modifier = modifier.focusRequester(focusRequester)
            )
        } else {
            Column(modifier = modifier) {
                Text(
                    text = "$label: $currentUnits",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    modifier = Modifier.clickable {
                        isEditing = true
                        tempUnitsText = "" // Start with empty string
                    }
                )
                // Additional info if provided
                additionalInfo?.invoke()
            }
        }
    }
}
