package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.C.P.View.Modules

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
fun PriceEditor_T1(
    currentPrice: Double,
    label: String,
    onPriceUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier,
    additionalInfo: (@Composable () -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shouldHideQuickInfoCards: Boolean = false,
    onNextField: (() -> Unit)? = null // New parameter for navigation to next field
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isEditing) {
        if (isEditing) focusRequester.requestFocus()
    }

    fun parsePrice(text: String): Double? {
        return try {
            text.replace(',', '.').toDoubleOrNull()
        } catch (e: Exception) { null }
    }

    fun savePrice() {
        val newPrice = parsePrice(tempText) ?: currentPrice
        onPriceUpdate(newPrice)
        isEditing = false
        tempText = ""

        if (shouldHideQuickInfoCards && onNextField != null) {
            // Navigate to next field instead of hiding keyboard
            onNextField()
        } else {
            keyboardController?.hide()
        }
    }

    // Fixed condition: Always show the PriceEditorFragID2 (removed currentPrice > 0 check)
    if (currentPrice >= 0 || isEditing) {
        Column(modifier = modifier) {
            if (isEditing) {
                OutlinedTextField(
                    value = tempText,
                    onValueChange = { newValue ->
                        val filtered = newValue.filter { char ->
                            char.isDigit() || char == '.' || char == ','
                        }
                        val dotCount = filtered.count { it == '.' }
                        val commaCount = filtered.count { it == ',' }
                        if (dotCount <= 1 && commaCount <= 1 && (dotCount + commaCount) <= 1) {
                            tempText = filtered
                        }
                    },
                    label = { Text("Ancien: $currentPrice DA") },
                    placeholder = { Text("Nouveau prix") },
                    suffix = { Text("DA") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        savePrice()
                        if (shouldHideQuickInfoCards && onNextField != null) {
                            onNextField()
                        }
                    }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                if (shouldHideQuickInfoCards) {
                    OutlinedTextField(
                        value = tempText,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { char ->
                                char.isDigit() || char == '.' || char == ','
                            }
                            val dotCount = filtered.count { it == '.' }
                            val commaCount = filtered.count { it == ',' }
                            if (dotCount <= 1 && commaCount <= 1 && (dotCount + commaCount) <= 1) {
                                tempText = filtered
                            }
                        },
                        label = { Text("$label: $currentPrice DA") },
                        placeholder = { Text("Nouveau prix") },
                        suffix = { Text("DA") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            savePrice()
                            if (shouldHideQuickInfoCards && onNextField != null) {
                                onNextField()
                            }
                        }),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    // Normal surface display mode
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
                                text = "$currentPrice DA",
                                style = MaterialTheme.typography.bodyLarge,
                                color = textColor,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
            additionalInfo?.invoke()
        }
    }
}
