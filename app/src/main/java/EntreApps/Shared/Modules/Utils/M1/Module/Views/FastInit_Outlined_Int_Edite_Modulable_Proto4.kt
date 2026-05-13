package EntreApps.Shared.Modules.Utils.M1.Module.Views

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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@SuppressLint("ObsoleteSdkInt")
@RequiresPermission(Manifest.permission.VIBRATE)
private fun vibrateOnUpdate(context: Context) {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FastInit_Outlined_Int_Edite_Modulable_Proto4(
    start_count: Int,
    au_depot: Int = 0,
    standard_count: Int = 1,
    start_au_premier_click_par_add_outlined: Boolean = false,
    icon: ImageVector? = null,
    isAvailable: Boolean = true,
    compact_taille: Boolean = false,
    show_depot_card_on_top_in_flow_row: Boolean = false,
    is_admin: Boolean = true,
    add_spacing_between_depot_and_sale: Boolean = false,
    on_admin_depot_update: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    on_Data_Update: (Int) -> Unit,
    startCouleur: Color = Color(0xFF3F51B5),
) {
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }
    var quantityInput by remember(start_count) { mutableStateOf("") }
    var isEditDepotMode by remember { mutableStateOf(false) }
    var depotInput by remember(au_depot) { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val depotFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) {
        if (isEditMode) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isEditDepotMode) {
        if (isEditDepotMode) {
            depotFocusRequester.requestFocus()
        }
    }

    val horizontalPadding = if (compact_taille) 8.dp else 12.dp
    val verticalPadding = if (compact_taille) 4.dp else 6.dp
    val iconSize = if (compact_taille) 14.dp else 16.dp
    val textStyle = if (compact_taille) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelLarge
    }

    val spacingBetweenCards = if (add_spacing_between_depot_and_sale) 8.dp else 4.dp

    if (isEditDepotMode) {
        OutlinedTextField(
            value = depotInput,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    depotInput = newValue
                }
            },
            modifier = modifier
                .width(80.dp)
                .focusRequester(depotFocusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val newDepotCount = depotInput.toIntOrNull() ?: 0
                    vibrateOnUpdate(context)
                    on_admin_depot_update(newDepotCount)
                    isEditDepotMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            label = { Text("Dépôt") }
        )
    } else if (isEditMode) {
        OutlinedTextField(     //<--
        //TODO(1): fait que si l entre 0 de delete le vent operaiotn
            value = quantityInput,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    quantityInput = newValue
                }
            },
            modifier = modifier
                .width(80.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val newQuantity = quantityInput.toIntOrNull() ?: 0
                    vibrateOnUpdate(context)
                    on_Data_Update(newQuantity)
                    isEditMode = false
                }
            ),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled = isAvailable,
            placeholder = if (au_depot > 0) {
                { Text("Dépôt: $au_depot", style = textStyle.copy(fontWeight = FontWeight.Normal)) }
            } else null
        )
    } else {
        val containerColor = if (!isAvailable) {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        } else if (start_count > 0) {
            MaterialTheme.colorScheme.tertiary
        } else {
            startCouleur
        }

        val contentColor = if (!isAvailable) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.onPrimary
        }

        if (show_depot_card_on_top_in_flow_row) {
            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(spacingBetweenCards),
                maxItemsInEachRow = 2
            ) {
                if (au_depot > 0) {
                    Card(
                        modifier = Modifier
                            .clickable(enabled = is_admin) {
                                isEditDepotMode = true
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = horizontalPadding * 0.7f,
                                vertical = verticalPadding * 0.7f
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warehouse,
                                contentDescription = "Dépôt",
                                tint = Color.Black,
                                modifier = Modifier.size((iconSize.value * 0.7f).dp)
                            )
                            Text(
                                text = au_depot.toString(),
                                style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .clickable(enabled = isAvailable) {
                            when {
                                start_count == 0 -> {
                                    if (start_au_premier_click_par_add_outlined) {
                                        isEditMode = true
                                    } else {
                                        vibrateOnUpdate(context)
                                        on_Data_Update(standard_count)
                                    }
                                }

                                else -> {
                                    // Covers both start_count < standard_count and start_count >= standard_count
                                    isEditMode = true
                                }
                            }
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
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Quantity",
                                tint = contentColor,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                        Text(
                            text = start_count.toString(),
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (au_depot > 0) {
                    Card(
                        modifier = Modifier
                            .clickable(enabled = is_admin) {
                                isEditDepotMode = true
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = horizontalPadding * 0.7f,
                                vertical = verticalPadding * 0.7f
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = au_depot.toString(),
                                style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f),
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .clickable(enabled = isAvailable) {
                            when {
                                start_count == 0 -> {
                                    if (start_au_premier_click_par_add_outlined) {
                                        isEditMode = true
                                    } else {
                                        vibrateOnUpdate(context)
                                        on_Data_Update(standard_count)
                                    }
                                }

                                else -> {
                                    // Covers both start_count < standard_count and start_count >= standard_count
                                    isEditMode = true
                                }
                            }
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
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "Quantity",
                                tint = contentColor,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                        Text(
                            text = start_count.toString(),
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
