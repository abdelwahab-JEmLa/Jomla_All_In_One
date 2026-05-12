package A_Main.Shared.Views.Dialogs.B.Dialoge

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode.Companion.sum_vent_et_benifice
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.benifice
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.sum_totale_vents
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class ProductDisplayMode {
    AllProducts,
    Echantillons,
    Panie,
}

@Composable
fun But_4_FloatingSearchFAB(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    currentMode: ProductDisplayMode,
    modifier: Modifier = Modifier,
) {
    if (currentMode == ProductDisplayMode.AllProducts) return

    var showField by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = {
                showField = !showField
                if (!showField) onSearchTextChange("")
            },
            containerColor = MaterialTheme.colorScheme.tertiary,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher un produit",
                tint = Color.White
            )
        }

        if (showField) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                placeholder = {
                    Text(
                        "Rechercher...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchTextChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
            )
        }
    }
}

@Composable
fun PressistatntMainActivityButtons_App4(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns
) {

    val currentMode by remember {
        derivedStateOf {
            val datas = viewModelNewProtoPatterns.active_Datas
            when {
                datas.its_Panie_Mode -> ProductDisplayMode.Panie
                datas.isEchatillantsMode -> ProductDisplayMode.Echantillons
                else -> ProductDisplayMode.AllProducts
            }
        }
    }

    fun applyMode(mode: ProductDisplayMode) {
        val isPanie = mode == ProductDisplayMode.Panie
        viewModelNewProtoPatterns.active_Datas.its_Panie_Mode = isPanie

        viewModelNewProtoPatterns.active_Datas.isEchatillantsMode =
            mode == ProductDisplayMode.Echantillons
        viewModelNewProtoPatterns.active_Datas.filter_echatilaten = ""
        // Sync the segmented toggle so it matches the newly selected mode.
        viewModelNewProtoPatterns.active_Datas.filterAffichageMode_Proto = when (mode) {
            ProductDisplayMode.Echantillons -> Filter_Affichage_Mode_Proto.Echants_Seulement
            else -> Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement
        }
        viewModelNewProtoPatterns.active_Datas.active_M9Compt?.let { compt ->
            viewModelNewProtoPatterns.update_active_Compt(
                compt.copy(its_Panie_Mode_Au_Lence_Boutique = isPanie)
            )
        }
    }

    var showDropdown by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val fabColor = when (currentMode) {
        ProductDisplayMode.AllProducts -> MaterialTheme.colorScheme.surfaceVariant
        ProductDisplayMode.Echantillons -> MaterialTheme.colorScheme.primary
        ProductDisplayMode.Panie -> MaterialTheme.colorScheme.tertiary
    }

    val fabIcon: ImageVector = when (currentMode) {
        ProductDisplayMode.AllProducts -> Icons.Default.FilterList
        ProductDisplayMode.Echantillons -> Icons.Default.Check
        ProductDisplayMode.Panie -> Icons.Default.ShoppingCart
    }

    val fabTint = when (currentMode) {
        ProductDisplayMode.AllProducts -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> Color.White
    }

    val labelText = when (currentMode) {
        ProductDisplayMode.AllProducts -> "Tous les produits"
        ProductDisplayMode.Echantillons -> "Échantillons"
        ProductDisplayMode.Panie -> "Panier"
    }

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val activeDatas = viewModelNewProtoPatterns.active_Datas
    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
        activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
    val tariffs = uiState.list_M13TarificationInfos

    val current_OnVent_M14VentPeriode_KeyID =
        activeDatas.active_M9Compt?.current_OnVent_M14VentPeriode_KeyID

    val activeOnVent_M8BonVent_benefice by remember(
        current_OnVent_M14VentPeriode_KeyID,
        activeDatas.list_M8BonVent,
        activeDatas.list_M10OperationVentCouleur,
        uiState.list_Datas?.m13TarificationInfos,
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.let { ops ->
                activeDatas.activeOnVent_M8BonVent?.benifice(ops, tariffs)
            }
        }
    }

    val activeOnVent_M8BonVent_sum_totale_vents by remember {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.let { ops ->
                activeDatas.activeOnVent_M8BonVent?.sum_totale_vents(ops, tariffs)
            }
        }
    }

    val m14VentPeriode_sums by remember(
        current_OnVent_M14VentPeriode_KeyID,
        activeDatas.list_M8BonVent,
        activeDatas.list_M10OperationVentCouleur,
        uiState.list_Datas?.m13TarificationInfos,
    ) {              // prixAchat null/zero guard applied in M8BonVent.sum_totale_et_benifice
        derivedStateOf {
            val periodKey = current_OnVent_M14VentPeriode_KeyID
                ?.takeIf { it.isNotBlank() && it != "null" } ?: return@derivedStateOf null
            val period = uiState.list_Datas?.m14VentPeriode
                ?.find { it.keyID == periodKey } ?: return@derivedStateOf null
            val allBons = activeDatas.list_M8BonVent ?: emptyList()
            val allOperations = activeDatas.list_M10OperationVentCouleur ?: emptyList()
            val allTariffs = uiState.list_Datas?.m13TarificationInfos ?: emptyList()

            period.sum_vent_et_benifice(
                bonsList = allBons,
                ventsList = allOperations,
                tariffsList = allTariffs,
            )
        }
    }

    Box(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = current_OnVent_M14VentPeriode_KeyID,
                    key = SemanticsPropertyKey("current_OnVent_M14VentPeriode_KeyID")
                )
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
    ) {

        Column {
            Text("Period")
            Row {
                m14VentPeriode_sums?.let { periodSums ->
                    if (periodSums.totale_vents > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "pér: %.0f DA  (${periodSums.on_command_bons} bons)".format(
                                    periodSums.totale_vents
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                    if (periodSums.totale_benifices > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "bén. pér: %.0f DA".format(periodSums.totale_benifices),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                    if (periodSums.totale_cash > 0.0) {
                        FloatingActionButton(
                            onClick = { },

                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "cash: %.0f DA".format(periodSums.totale_cash),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()
            Text("Bon Vent")
            Row {
                activeOnVent_M8BonVent_benefice?.let { benef ->
                    if (benef > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {

                                }
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "bén: %.0f DA".format(benef),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }

                activeOnVent_M8BonVent_sum_totale_vents?.let { it ->
                    if (it > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "tot: %.0f DA".format(it),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box {
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(56.dp),
                            onClick = { showDropdown = true },
                            containerColor = fabColor,
                        ) {
                            Icon(imageVector = fabIcon, contentDescription = null, tint = fabTint)
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            ModeMenuItem(
                                label = "Tous les produits",
                                icon = Icons.Default.FilterList,
                                isSelected = currentMode == ProductDisplayMode.AllProducts,
                                onClick = {
                                    applyMode(ProductDisplayMode.AllProducts); showDropdown = false
                                }
                            )
                            ModeMenuItem(
                                label = "Échantillons",
                                icon = Icons.Default.Check,
                                isSelected = currentMode == ProductDisplayMode.Echantillons,
                                onClick = {
                                    applyMode(ProductDisplayMode.Echantillons); showDropdown = false
                                }
                            )
                            ModeMenuItem(
                                label = "Panier",
                                icon = Icons.Default.ShoppingCart,
                                isSelected = currentMode == ProductDisplayMode.Panie,
                                onClick = {
                                    applyMode(ProductDisplayMode.Panie)
                                    activeDatas.filterAffichageMode_Proto = Filter_Affichage_Mode_Proto.Panie

                                    ; showDropdown = false }
                            )
                        }
                    }

                    // Segmented toggle: Tablette / Échants / Les deux — under the FAB
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val tabletteMode = activeDatas.filterAffichageMode_Proto
                        listOf(
                            Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement to "Tablette",
                            Filter_Affichage_Mode_Proto.Echants_Seulement           to "Échants",
                            Filter_Affichage_Mode_Proto.Tablette_Et_Echants         to "Les 2",
                        ).forEach { (mode, label) ->
                            val isSelected = tabletteMode == mode
                            FloatingActionButton(
                                modifier = Modifier
                                    .widthIn(min = 48.dp)
                                    .height(36.dp),
                                onClick = {
                                    activeDatas.filterAffichageMode_Proto = mode

                                    viewModelNewProtoPatterns.sendData(
                                        Wifi_Messages_Types_NewProto.Change_Filtered_Produits_Du_TabletteDisplayer.prefix,
                                        mode.name
                                    )
                                },
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor   = if (isSelected) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    text     = label,
                                    style    = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }

                But_4_FloatingSearchFAB(
                    searchText = viewModelNewProtoPatterns.active_Datas.filter_echatilaten,
                    onSearchTextChange = {
                        viewModelNewProtoPatterns.active_Datas.filter_echatilaten = it
                    },
                    currentMode = currentMode,
                )

                Text(
                    text = labelText,
                    modifier = Modifier
                        .background(color = fabColor, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = fabTint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}

@Composable
private fun ModeMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (isSelected) Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = onClick
    )
}
