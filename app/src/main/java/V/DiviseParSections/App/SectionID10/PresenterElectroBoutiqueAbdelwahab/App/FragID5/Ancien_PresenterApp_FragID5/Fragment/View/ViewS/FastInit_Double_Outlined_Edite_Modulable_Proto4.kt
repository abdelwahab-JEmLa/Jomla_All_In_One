package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Double-value equivalent of [FastInit_Outlined_Int_Edite_Modulable_Proto4].
 *
 * Display mode : coloured pill Card showing the formatted value.
 *   • If [start_value] == 0.0  → primary colour  (not yet set)
 *   • If [start_value]  > 0.0  → tertiary colour (already set)
 *   • If [isAvailable]  is false → muted/outline colour
 *
 * On click behaviour mirrors the Int proto:
 *   • value == 0.0 AND [start_au_premier_click_par_add_outlined] == false
 *       → immediately calls [on_Data_Update] with [standard_value]
 *   • otherwise → switches to OutlinedTextField edit mode
 *
 * Edit mode : OutlinedTextField with decimal keyboard; confirms on IME Done.
 */
@Composable
fun FastInit_Double_Outlined_Edite_Modulable_Proto4(
    start_value: Double,
    standard_value: Double = 1.0,
    start_au_premier_click_par_add_outlined: Boolean = false,
    force_edit_mode_on_start: Boolean = false,
    icon: ImageVector? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    modifier: Modifier = Modifier,
    on_Data_Update: (Double) -> Unit
) {
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(force_edit_mode_on_start) }
    var valueInput by remember(start_value) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) focusRequester.requestFocus()
    }

    val horizontalPadding = if (compact_taille) 8.dp else 12.dp
    val verticalPadding   = if (compact_taille) 4.dp  else 6.dp
    val iconSize          = if (compact_taille) 14.dp else 16.dp
    val textStyle         = if (compact_taille) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    if (isEditMode) {
        OutlinedTextField(
            value       = valueInput,
            onValueChange = { newValue ->
                // Allow digits + at most one decimal separator (, or .)
                val normalized = newValue.replace(",", ".")
                val isValid = normalized.isEmpty()
                    || normalized == "."
                    || normalized.matches(Regex("\\d*\\.?\\d*"))
                if (isValid) valueInput = newValue
            },
            modifier = modifier
                .width(96.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val parsed = valueInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                    vibrateOnUpdate_Double(context)
                    on_Data_Update(parsed)
                    isEditMode = false
                }
            ),
            singleLine  = true,
            textStyle   = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled     = isAvailable,
            placeholder = {
                Text(
                    "%.2f".format(start_value),
                    style = textStyle.copy(fontWeight = FontWeight.Normal)
                )
            }
        )
    } else {
        val containerColor = when {
            !isAvailable    -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            start_value > 0 -> MaterialTheme.colorScheme.tertiary
            else            -> MaterialTheme.colorScheme.primary
        }
        val contentColor = if (!isAvailable) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.onPrimary
        }

        Card(
            modifier = modifier.clickable(enabled = isAvailable) {
                when {
                    start_value == 0.0 && !start_au_premier_click_par_add_outlined -> {
                        vibrateOnUpdate_Double(context)
                        on_Data_Update(standard_value)
                    }
                    else -> isEditMode = true
                }
            },
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = horizontalPadding,
                    vertical   = verticalPadding
                ),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                icon?.let {
                    Icon(
                        imageVector     = it,
                        contentDescription = "Value",
                        tint            = contentColor,
                        modifier        = Modifier.size(iconSize)
                    )
                }
                Text(
                    text      = "%.2f".format(start_value),
                    style     = textStyle,
                    fontWeight = FontWeight.Bold,
                    color     = contentColor
                )
            }
        }
    }
}

@SuppressLint("ObsoleteSdkInt")
@RequiresPermission(Manifest.permission.VIBRATE)
private fun vibrateOnUpdate_Double(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(600L, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(600L)
    }
}
