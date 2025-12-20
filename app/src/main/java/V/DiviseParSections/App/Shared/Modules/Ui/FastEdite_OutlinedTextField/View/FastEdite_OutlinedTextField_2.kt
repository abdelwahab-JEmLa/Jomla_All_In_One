package V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
@Composable
fun FastEdite_OutlinedTextField_V2(
    label: String,
    value: Double,
    onSave: (Double) -> Unit,
    suffix: String = "DA"
) {
    var isEditing by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    fun startEditing() {
        isEditing = true
        inputValue = value.toString()
    }

    fun saveValue() {
        val newValue = inputValue.toDoubleOrNull() ?: value
        onSave(newValue)
        isEditing = false
        keyboardController?.hide()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isEditing) "$label (Ancien: ${String.format("%.2f", value)} $suffix)" else label,
            style = MaterialTheme.typography.bodyMedium
        )

        if (isEditing) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier
                    .width(80.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { saveValue() }),
                singleLine = true
            )
        } else {
            Text(
                text = String.format("%.2f %s", value, suffix),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { startEditing() }
            )
        }
    }
}
