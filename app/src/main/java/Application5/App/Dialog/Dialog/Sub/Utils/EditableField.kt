package Application5.App.Dialog.Dialog.Sub.Utils

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun EditableField_SeparatedAppsCodingPattern(
    label: String,
    value: String,
    isEditing: Boolean,
    inputValue: String,
    onInputChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onSave: () -> Unit,
    focusRequester: FocusRequester,
    keyboardType: KeyboardType = KeyboardType.Companion.Text,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    width: Dp = 150.dp
) {
    Row(
        modifier = Modifier.Companion.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        if (isEditing) {
            OutlinedTextField(
                value = inputValue,
                onValueChange = onInputChange,
                modifier = Modifier.Companion.width(width).focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Companion.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSave() }),
                singleLine = true
            )
        } else {
            Text(
                text = value.ifBlank { "---" },
                style = textStyle,
                modifier = Modifier.Companion.clickable { onEditClick() }
            )
        }
    }
}
