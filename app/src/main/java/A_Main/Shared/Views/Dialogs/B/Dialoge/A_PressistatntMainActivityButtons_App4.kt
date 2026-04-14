package A_Main.Shared.Views.Dialogs.B.Dialoge

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode.Companion.sum_vent_et_benifice
import EntreApps.Shared.Models.M8BonVent.Companion.sum_benifice
import EntreApps.Shared.Models.M8BonVent.Companion.sum_totale_vents
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private const val TAG = "PressistatntMainActivity"

enum class ProductDisplayMode {
    AllProducts,
    Echantillons,
    Panie,
}

// ─────────────────────────────────────────────────────────────────────────────
// Search FAB
// ─────────────────────────────────────────────────────────────────────────────

/**
 * A floating search button that expands into a text-field when toggled.
 * [searchText] / [onSearchTextChange] are owned by the caller (ViewModel state).
 * Only rendered when [currentMode] is [ProductDisplayMode.Echantillons] or
 * [ProductDisplayMode.Panie] — hidden in AllProducts mode.
 */
@Composable
fun But_4_FloatingSearchFAB(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    currentMode: ProductDisplayMode,
    modifier: Modifier = Modifier,
) {
    if (currentMode == ProductDisplayMode.AllProducts) return

    var showField by remember { mutableStateOf(false) }

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

// ─────────────────────────────────────────────────────────────────────────────
// Main floating button cluster
// ─────────────────────────────────────────────────────────────────────────────

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

    val listm10operationventcouleurFilteredbyActivem8bonventState =
        activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state

    val tariffs = uiState.list_M13TarificationInfos
    val sumBenefice by remember {
        derivedStateOf {
            listm10operationventcouleurFilteredbyActivem8bonventState?.let { ops ->
                val bonVent = activeDatas.activeOnVent_M8BonVent

                val result = bonVent?.sum_benifice(ops, tariffs)
                // Log moved here where bonVent / ops / tarifs / result are all in scope
                Log.d(TAG, buildString {
                    appendLine("=== sum_benifice DEBUG ===")
                    appendLine("bonVent       : ${bonVent?.keyID} (null=${bonVent == null})")
                    appendLine("ops.size      : ${ops.size}")
                    appendLine("tarifs.size   : ${tariffs.size}")
                    ops.forEach { op ->
                        val matchingTarif =
                            tariffs.find { it.keyID == op.parentM13TarificationKeyID }
                        appendLine(
                            "  op ${op.keyID.takeLast(6)} | qty=${op.quantity}" +
                                    " | prixVent=${op.prix_de_Vent_entre_directement_NewProto}" +
                                    " | type=${op.typeTarificationEnumT2}" +
                                    " | tarifFound=${matchingTarif != null}" +
                                    " | tarifPrix=${matchingTarif?.prixCurrency}"
                        )
                    }
                    appendLine("sum_benifice  → $result")
                })
                result
            }
        }
    }

    // ── Total ventes ─────────────────────────────────────────────────────────
    // Same fix: plain remember { derivedStateOf { } } so derivedStateOf tracks its own
    // state reads without a new-List key breaking the observer each frame.
    val sumTotaleVents by remember {
        derivedStateOf {
            listm10operationventcouleurFilteredbyActivem8bonventState?.let { ops ->
                activeDatas.activeOnVent_M8BonVent?.sum_totale_vents(
                    ops,
                    uiState.list_M13TarificationInfos
                )
            }
        }
    }
    val periodSums by remember(
        activeDatas.active_M9Compt?.current_OnVent_M14VentPeriode_KeyID,
        activeDatas.list_M8BonVent,
        activeDatas.list_M10OperationVentCouleur,
        uiState.list_Datas?.m13TarificationInfos,
    ) {
        derivedStateOf {
            val periodKey = activeDatas.active_M9Compt?.current_OnVent_M14VentPeriode_KeyID
                ?.takeIf { it.isNotBlank() && it != "null" } ?: return@derivedStateOf null
            val period = uiState.list_Datas?.m14VentPeriode
                ?.find { it.keyID == periodKey } ?: return@derivedStateOf null
            val allBons = activeDatas.list_M8BonVent ?: emptyList()
            val allOperations = activeDatas.list_M10OperationVentCouleur ?: emptyList()
            val allTariffs = uiState.list_Datas?.m13TarificationInfos ?: emptyList()

            val sums = period.sum_vent_et_benifice(
                bonsList = allBons,
                ventsList = allOperations,
                tariffsList = allTariffs,
            )
            Log.d(TAG, buildString {
                appendLine("=== sum_vent_et_benifice (période) ===")
                appendLine("période         : ${period.keyID.takeLast(4)}")
                appendLine("bons commande   : ${sums.on_command_bons}")
                appendLine("totale ventes   : ${sums.totale_vents}")
                appendLine("bénéfices       : ${sums.totale_benifices}")
                appendLine("nb crédit bons  : ${sums.credits_bons}")
                appendLine("crédit sum      : ${sums.credit_sum}")
                appendLine("cash            : ${sums.totale_cash}")
            })
            sums
        }
    }
     val second_vent = listm10operationventcouleurFilteredbyActivem8bonventState?.last()

    val prixAchat = tariffs
        .filter {
            it.parent_M1Produit_KeyId == second_vent?.parent_M1Produit_KeyId &&
                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
        }
        .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

    val tariffs_produit = tariffs
        .filter {
            it.parent_M1Produit_KeyId == second_vent?.parent_M1Produit_KeyId
        }

    Box(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = listm10operationventcouleurFilteredbyActivem8bonventState,
                    key = SemanticsPropertyKey("listm10operationventcouleurFilteredbyActivem8bonventState")
                )
                set(
                    value = prixAchat,
                    key = SemanticsPropertyKey("prixAchat")
                )
                set(value = tariffs_produit, key = SemanticsPropertyKey("tariffs_produit"))
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
            Box {
                periodSums?.let { sums ->
                    Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                        if (sums.totale_vents > 0.0) {
                            FloatingActionButton(
                                onClick = { /* lecture seule */ },
                                modifier = Modifier
                                    .widthIn(min = 56.dp)
                                    .height(40.dp),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = "pér: %.0f DA  (${sums.on_command_bons} bons)".format(sums.totale_vents),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                        if (sums.totale_benifices > 0.0) {
                            FloatingActionButton(
                                onClick = { /* lecture seule */ },
                                modifier = Modifier
                                    .widthIn(min = 56.dp)
                                    .height(40.dp),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = "bén. pér: %.0f DA".format(sums.totale_benifices),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                        if (sums.totale_cash > 0.0) {
                            FloatingActionButton(
                                onClick = { /* lecture seule */ },
                                modifier = Modifier
                                    .widthIn(min = 56.dp)
                                    .height(40.dp),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = "cash: %.0f DA".format(sums.totale_cash),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }
                }
            }

            Row {
                sumBenefice?.let { benef ->
                    if (benef > 0.0) {
                        FloatingActionButton(
                            onClick = { /* lecture seule */ },
                            modifier = Modifier
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

                sumTotaleVents?.let { total ->
                    if (total > 0.0) {
                        FloatingActionButton(
                            onClick = { /* lecture seule */ },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "tot: %.0f DA".format(total),
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
                        onClick = { applyMode(ProductDisplayMode.Panie); showDropdown = false }
                    )
                }

                But_4_FloatingSearchFAB(
                    searchText = viewModelNewProtoPatterns.active_Datas.filter_echatilaten,
                    onSearchTextChange = {
                        viewModelNewProtoPatterns.active_Datas.filter_echatilaten = it
                    },
                    currentMode = currentMode,
                )

                // ── Mode label ───────────────────────────────────────────────────
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
