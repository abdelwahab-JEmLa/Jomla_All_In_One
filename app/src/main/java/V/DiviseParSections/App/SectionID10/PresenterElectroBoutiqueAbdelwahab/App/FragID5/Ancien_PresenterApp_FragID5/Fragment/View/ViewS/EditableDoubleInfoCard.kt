package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * Mirrors [EditableInfoCard] but for [Double] values.
 * In display mode → tappable InfoCard.
 * In edit mode    → inline BasicTextField (empty start, last value as placeholder,
 *                   keyboard auto-focused) + confirm button.
 */
@Composable
fun EditableDoubleInfoCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    startValue: Double,
    onUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    // FIX TODO(1): start empty so the user types a fresh value
    var textValue by remember(isEditing) { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    // FIX TODO(1): request focus (= open keyboard) as soon as edit mode appears
    LaunchedEffect(isEditing) {
        if (isEditing) focusRequester.requestFocus()
    }

    fun confirm() {
        val parsed = textValue.replace(",", ".").toDoubleOrNull()
        if (parsed != null) onUpdate(parsed)
        isEditing = false
    }

    if (isEditing) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = itemPadding + 2.dp, vertical = itemPadding),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()

                // FIX TODO(1): decorationBox shows last value as placeholder when empty
                BasicTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { confirm() }),
                    textStyle = TextStyle(
                        fontSize = valueTextSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .width(56.dp)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (textValue.isEmpty()) {
                                Text(
                                    text = "%.2f".format(startValue),
                                    style = TextStyle(
                                        fontSize = valueTextSize,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                IconButton(
                    onClick = { confirm() },
                    modifier = Modifier.size(itemPadding + 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmer",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(itemPadding + 10.dp)
                    )
                }
            }
        }
    } else {
        Card(
            modifier = modifier.clickable { isEditing = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = itemPadding + 2.dp, vertical = itemPadding),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        fontSize = labelTextSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium,
                        lineHeight = labelTextSize
                    )
                    Text(
                        text = value,
                        fontSize = valueTextSize,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        lineHeight = valueTextSize
                    )
                }
            }
        }
    }
}
