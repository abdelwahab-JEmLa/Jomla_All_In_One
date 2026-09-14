package EntreApps.Shared.Modules.Utils.M1.Module.Views

import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
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

@Composable
private fun MediaPickerBar(
    onPickImage: (() -> Unit)?,
    onPickVideo: (() -> Unit)?,
    textStyle: TextStyle
) {

    if (onPickImage != null) {

        Card(
            modifier = Modifier.clickable { onPickImage() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "🖼",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = textStyle,
                fontWeight = FontWeight.Bold
            )
        }
    }
    if (onPickVideo != null) {
        Card(
            modifier = Modifier.clickable { onPickVideo() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "🎥",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = textStyle,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Affiche_Vent_Et_Couleur_Relatives(
    active_Central_Values: ActiveCentralValues,
    start_count: Int,
    affichable_mem_si_zero_depot: Boolean = true,
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
    affiche_ProduitDataBaseEdites: Boolean = false,
    c_unite_couleur_de_couleurKey: String = "",
    on_set_c_unite_key: (String) -> Unit = {},
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,
    mode_selection_parent_couleur_key: String = "",
    is_this_color_selected_as_parent_for_link: Boolean = false,
    on_pour_mode_selection_parent_couleur: () -> Unit = {},
    onPickImage: (() -> Unit)? = null,
    onPickVideo: (() -> Unit)? = null,
    its_couleur_ac_imgVid_presentative_de_tout_les_couleur: Boolean = false,
    on_toggle_presentative: (() -> Unit)? = null,
    affiche_que_c_don_le_panie: Boolean = false,
    on_toggle_affiche_panie: (() -> Unit)? = null,
    relative_couleur: M3CouleurProduitInfos? = null,
    on_update_m3couleur: ((M3CouleurProduitInfos) -> Unit)? = null,
) {
    val context = LocalContext.current

    var isEditMode by remember { mutableStateOf(false) }
    val flow_row = !active_Central_Values.compact_button_au_edite_base_donne_options
    var quantityInput by remember(start_count) { mutableStateOf("") }
    var isEditDepotMode by remember { mutableStateOf(false) }
    var depotInput by remember(au_depot) { mutableStateOf("") }
    var isEditingNomCouleur by remember { mutableStateOf(false) }
    var nomCouleurInput by remember(relative_couleur?.nomCouleurStrSiSonImageDispo) {
        mutableStateOf(relative_couleur?.nomCouleurStrSiSonImageDispo ?: "")
    }
    val mode_c_unite_actif = c_unite_couleur_de_couleurKey.isNotEmpty() || is_this_color_selected_as_parent_for_link
    val focusRequester = remember { FocusRequester() }
    val depotFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditMode) { if (isEditMode) focusRequester.requestFocus() }
    LaunchedEffect(isEditDepotMode) { if (isEditDepotMode) depotFocusRequester.requestFocus() }

    if (isEditingNomCouleur && relative_couleur != null && on_update_m3couleur != null) {
        AlertDialog(
            onDismissRequest = { isEditingNomCouleur = false },
            title = { Text("Nom de la couleur") },
            text = {
                OutlinedTextField(
                    value = nomCouleurInput,
                    onValueChange = { nomCouleurInput = it },
                    singleLine = true,
                    label = { Text("Nom") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        on_update_m3couleur(relative_couleur.copy(nomCouleurStrSiSonImageDispo = nomCouleurInput))
                        isEditingNomCouleur = false
                    })
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    on_update_m3couleur(relative_couleur.copy(nomCouleurStrSiSonImageDispo = nomCouleurInput))
                    isEditingNomCouleur = false
                }) { Text("Valider") }
            },
            dismissButton = {
                TextButton(onClick = { isEditingNomCouleur = false }) { Text("Annuler") }
            }
        )
    }

    val horizontalPadding = if (compact_taille) 8.dp else 12.dp
    val verticalPadding = if (compact_taille) 4.dp else 6.dp
    val iconSize = if (compact_taille) 14.dp else 16.dp
    val textStyle = if (compact_taille) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge
    val spacingBetweenCards = if (add_spacing_between_depot_and_sale) 8.dp else 4.dp

    if (isEditDepotMode) {
        OutlinedTextField(
            value = depotInput,
            onValueChange = { newValue -> if (newValue.isEmpty() || newValue.all { it.isDigit() }) depotInput = newValue },
            modifier = modifier.width(80.dp).focusRequester(depotFocusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                vibrateOnUpdate(context)
                on_admin_depot_update(depotInput.toIntOrNull() ?: 0)
                isEditDepotMode = false
            }),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            label = { Text("Dépôt") }
        )
    } else if (isEditMode) {
        OutlinedTextField(
            value = quantityInput,
            onValueChange = { newValue -> if (newValue.isEmpty() || newValue.all { it.isDigit() }) quantityInput = newValue },
            modifier = modifier.width(80.dp).focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                vibrateOnUpdate(context)
                on_Data_Update(quantityInput.toIntOrNull() ?: 0)
                isEditMode = false
            }),
            singleLine = true,
            textStyle = textStyle.copy(fontWeight = FontWeight.Bold),
            enabled = isAvailable,
            placeholder = if (au_depot > 0 || affichable_mem_si_zero_depot) {
                { Text("Dépôt: $au_depot", style = textStyle.copy(fontWeight = FontWeight.Normal)) }
            } else null
        )
    } else {
        val containerColor = if (!isAvailable) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        else if (start_count > 0) MaterialTheme.colorScheme.tertiary
        else startCouleur
        val contentColor = if (!isAvailable) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        else MaterialTheme.colorScheme.onPrimary

        if (show_depot_card_on_top_in_flow_row) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                val rowItems = buildList<@Composable () -> Unit> {
                    if (affiche_ProduitDataBaseEdites && on_update_m3couleur != null && relative_couleur != null && is_admin) {
                        val its_delicate_a_regle_apres = relative_couleur.its_delicate_a_regle_apres
                        add {
                            Card(
                                modifier = Modifier.clickable {
                                    on_update_m3couleur(relative_couleur.copy(its_delicate_a_regle_apres = !its_delicate_a_regle_apres))
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (its_delicate_a_regle_apres) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "👁",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = if (its_delicate_a_regle_apres) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (affiche_ProduitDataBaseEdites && on_update_m3couleur != null && relative_couleur != null && is_admin) {
                        add {
                            Card(
                                modifier = Modifier.clickable {
                                    nomCouleurInput = relative_couleur.nomCouleurStrSiSonImageDispo
                                    isEditingNomCouleur = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    text = "✏️",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if ((!its_couleur_ac_imgVid_presentative_de_tout_les_couleur && c_unite_couleur_de_couleurKey.isEmpty()) || start_count > 0) {
                        add {
                            Card(
                                modifier = Modifier.clickable(enabled = isAvailable) {
                                    when {
                                        start_count == 0 -> {
                                            if (start_au_premier_click_par_add_outlined) isEditMode = true
                                            else { vibrateOnUpdate(context); on_Data_Update(standard_count) }
                                        }
                                        else -> isEditMode = true
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = containerColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    icon?.let { Icon(imageVector = it, contentDescription = "Quantity", tint = contentColor, modifier = Modifier.size(iconSize)) }
                                    Text(text = start_count.toString(), style = textStyle, fontWeight = FontWeight.Bold, color = contentColor)
                                }
                            }
                        }
                    }

                    // Le compteur de dépôt reste affichable même si le "start" (start_count)
                    // ou le lien (c_unite_couleur_de_couleurKey) est actif pour cette couleur.
                    if (au_depot > 0 || affichable_mem_si_zero_depot) {
                        add {
                            Card(
                                modifier = Modifier.clickable(enabled = is_admin) { isEditDepotMode = true },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Red)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = horizontalPadding * 0.7f, vertical = verticalPadding * 0.7f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Warehouse, contentDescription = "Dépôt", tint = Color.Black, modifier = Modifier.size((iconSize.value * 0.7f).dp))
                                    Text(text = au_depot.toString(), style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f), fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                    if ((affiche_buttons_lien_unite_couleur_au_couleut_parent || mode_selection_parent_couleur_key.isNotEmpty()) && is_admin) {
                        if (on_toggle_affiche_panie != null) {
                            add {
                                Card(
                                    modifier = Modifier.clickable { on_toggle_affiche_panie() },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (affiche_que_c_don_le_panie) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Text(
                                        text = if (affiche_que_c_don_le_panie) "🧺 P" else "🧺",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = textStyle,
                                        fontWeight = FontWeight.Bold,
                                        color = if (affiche_que_c_don_le_panie) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    val showStar = affiche_buttons_lien_unite_couleur_au_couleut_parent || its_couleur_ac_imgVid_presentative_de_tout_les_couleur
                    if (on_toggle_presentative != null && is_admin && showStar) {
                        add {
                            Card(
                                modifier = Modifier.clickable {
                                    on_toggle_presentative()
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (its_couleur_ac_imgVid_presentative_de_tout_les_couleur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "⭐",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = if (its_couleur_ac_imgVid_presentative_de_tout_les_couleur) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }


                    if ((affiche_buttons_lien_unite_couleur_au_couleut_parent || mode_selection_parent_couleur_key.isNotEmpty()) && is_admin) {
                        add {
                            MediaPickerBar(onPickImage = onPickImage, onPickVideo = onPickVideo, textStyle = textStyle)
                        }
                    }
                    if (((affiche_buttons_lien_unite_couleur_au_couleut_parent || mode_selection_parent_couleur_key.isNotEmpty()) || mode_c_unite_actif) && is_admin) {
                        add {
                            Card(
                                modifier = Modifier.clickable {
                                    if (c_unite_couleur_de_couleurKey.isNotEmpty()) on_set_c_unite_key("") else on_pour_mode_selection_parent_couleur()
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (mode_c_unite_actif) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = if (mode_c_unite_actif) "⛓ cé" else "⛓",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mode_c_unite_actif) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    val showValidationButton = mode_c_unite_actif || (mode_selection_parent_couleur_key.isNotEmpty() && !is_this_color_selected_as_parent_for_link)
                    if (showValidationButton && is_admin) {
                        add {
                            Card(
                                modifier = Modifier.clickable {
                                    if (mode_selection_parent_couleur_key.isNotEmpty() && !is_this_color_selected_as_parent_for_link) {
                                        on_set_c_unite_key(mode_selection_parent_couleur_key)
                                    } else {
                                        on_set_c_unite_key(c_unite_couleur_de_couleurKey)
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    text = "✓",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }
                    }

                    if (!its_couleur_ac_imgVid_presentative_de_tout_les_couleur && c_unite_couleur_de_couleurKey.isNotEmpty()) {
                        add {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    text = "🔗 Lié",
                                    modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
                                    style = textStyle,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (flow_row) {
                    FlowRow(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards, Alignment.End)
                    ) {
                        rowItems.forEach { renderItem -> renderItem() }
                    }
                } else {
                    LazyRow(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards, Alignment.End)
                    ) {
                        rowItems.forEach { renderItem -> item { renderItem() } }
                    }
                }
            }
        } else {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenCards),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!its_couleur_ac_imgVid_presentative_de_tout_les_couleur && c_unite_couleur_de_couleurKey.isEmpty()) {
                    if (au_depot > 0 || affichable_mem_si_zero_depot) {
                        Card(
                            modifier = Modifier.clickable(enabled = is_admin) { isEditDepotMode = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Red)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = horizontalPadding * 0.7f, vertical = verticalPadding * 0.7f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = au_depot.toString(), style = textStyle.copy(fontSize = textStyle.fontSize * 0.7f), fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.clickable(enabled = isAvailable) {
                            when {
                                start_count == 0 -> {
                                    if (start_au_premier_click_par_add_outlined) isEditMode = true
                                    else { vibrateOnUpdate(context); on_Data_Update(standard_count) }
                                }
                                else -> isEditMode = true
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            icon?.let { Icon(imageVector = it, contentDescription = "Quantity", tint = contentColor, modifier = Modifier.size(iconSize)) }
                            Text(text = start_count.toString(), style = textStyle, fontWeight = FontWeight.Bold, color = contentColor)
                        }
                    }
                } else if (c_unite_couleur_de_couleurKey.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "🔗 Lié",
                            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding),
                            style = textStyle,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
