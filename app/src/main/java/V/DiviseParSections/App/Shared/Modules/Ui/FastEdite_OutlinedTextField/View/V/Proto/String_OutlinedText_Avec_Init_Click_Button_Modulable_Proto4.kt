package V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto

import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.AbsenceJustificationShortcuts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * Smart text field for absence justification with auto-completion:
 * - "م" -> auto-completes to "مبرر"
 * - "ا" -> auto-completes to "مجاز من المدرسة"
 * - Any other text is kept as is
 */
@Composable
fun String_OutlinedText_Avec_Init_Click_Button_Modulable_Proto4_ForStrings(
    start_text: String,
    placeholder: String = "تبرير الغياب",
    icon: ImageVector? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    modifier: Modifier = Modifier,
    on_DonneClick_Data_Update: (String) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var textInput by remember(start_text) { mutableStateOf(start_text) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
        }
    }

    // Adjust sizes based on compact mode
    val horizontalPadding = if (compact_taille) 8.dp else 12.dp
    val verticalPadding = if (compact_taille) 4.dp else 6.dp
    val iconSize = if (compact_taille) 14.dp else 16.dp
    val textStyle = if (compact_taille) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    if (isEditMode) {
        // Edit mode: Show outlined text field
        OutlinedTextField(
            value = textInput,
            onValueChange = { newValue ->
                textInput = newValue
                // Auto-complete when single character shortcuts are entered
                if (AbsenceJustificationShortcuts.isShortcut(newValue)) {
                    val completed = AbsenceJustificationShortcuts.processInput(newValue)
                    textInput = completed
                    // Auto-save and exit edit mode
                    on_DonneClick_Data_Update(completed)
                    isEditMode = false
                }
            },
            modifier = modifier
                .width(200.dp)
                .focusRequester(focusRequester),
            placeholder = { Text(placeholder) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    on_DonneClick_Data_Update(textInput)
                    isEditMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled = isAvailable,
            supportingText = {
                Text(
                    text = AbsenceJustificationShortcuts.getHelperText(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )
    } else {
        // Display mode: Show clickable card
        val isJustified = start_text.isNotBlank()

        val containerColor = if (!isAvailable) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        } else if (isJustified) {
            MaterialTheme.colorScheme.tertiary
        } else {
            MaterialTheme.colorScheme.errorContainer
        }

        val contentColor = if (!isAvailable) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        } else if (isJustified) {
            MaterialTheme.colorScheme.onTertiary
        } else {
            MaterialTheme.colorScheme.error
        }

        Card(
            modifier = modifier
                .clickable(enabled = isAvailable) {
                    isEditMode = true
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
                // Only show icon if not null
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = "Tabrire",
                        tint = contentColor,
                        modifier = Modifier.size(iconSize)
                    )
                }

                Text(
                    text = if (start_text.isNotEmpty()) {
                        start_text
                    } else {
                        "⚠️ غير مبرر - اضغط للتبرير"
                    },
                    style = textStyle,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}
