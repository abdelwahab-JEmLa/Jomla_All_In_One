package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.Shared.Module

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

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
    var tempText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEditing) {
        if (isEditing) focusRequester.requestFocus()
    }

    fun saveUnits() {
        val newUnits = tempText.toIntOrNull() ?: currentUnits
        onUnitsUpdate(newUnits)
        isEditing = false
        tempText = ""
        keyboardController?.hide()
    }

    if (!showOnlyWhenPositive || currentUnits > 0 || isEditing) {
        Column(modifier = modifier) {
            if (isEditing) {
                OutlinedTextField(
                    value = tempText,
                    onValueChange = { newValue ->
                        tempText = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Ancien: $currentUnits unités") },
                    placeholder = { Text("Nouvelles unités") },
                    suffix = { Text("unités") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { saveUnits() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            isEditing = true
                            tempText = ""
                        },
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$currentUnits unités",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            additionalInfo?.invoke()
        }
    }
}
