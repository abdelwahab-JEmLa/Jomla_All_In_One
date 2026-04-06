package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

private const val TAG_CLEANUP = "CleanupM8M10"

@Composable
fun Fab_CleanupM8AndM10(
    on_vent_key: String,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    onDismissDropdown: () -> Unit,
) {
    val sizeM1 = repositorysMainGetter.repo1ProduitInfos.datasValue.size
    val sizeM3 = repositorysMainGetter.repo3CouleurProduit.datasValue.size
    val sizeM8 = repositorysMainGetter.repo8BonVent.datasValue.size
    val sizeM2 = repositorysMainGetter.repo2Client.datasValue.size
    val sizeM10 = repositorysMainGetter.repo10OperationVentCouleur.datasValue.size
    val sizeM11 = repositorysMainGetter.repo11AchatOperation.datasValue.size
    val sizeM13 = repositorysMainGetter.repo13TarificationInfos.datasValue.size

    // Badge counts: approximate (no file I/O in composition) — mirrors the two conditions
    // used in moveColorsWithoutImagesToNonActive (tariff check + name-presence proxy for image).
    val productIdsWithTariff = repositorysMainGetter.repo13TarificationInfos.datasValue
        .filter { !it.typeChoisi.ignore_affiche && it.prixCurrency > 0 }
        .map { it.parent_M1Produit_KeyId }.toSet()

    val colorsToMoveIds = repositorysMainGetter.repo3CouleurProduit.datasValue
        .filter { color ->
            color.nomImageFichieSansEtansion.isBlank() ||
                    color.nomImageFichieSansEtansion == "Non Dispo" ||
                    color.parentBProduitInfosKeyID !in productIdsWithTariff
        }
        .map { it.keyID }.toSet()
    val sizeNoImage = colorsToMoveIds.size
    val sizeM3AfterImageMove = sizeM3 - sizeNoImage

    val activeColorProductIds = repositorysMainGetter.repo3CouleurProduit.datasValue
        .filter { it.keyID !in colorsToMoveIds }
        .map { it.parentBProduitInfosKeyID }.toSet()
    val colorsByProduit =
        repositorysMainGetter.repo3CouleurProduit.datasValue.groupBy { it.parentBProduitInfosKeyID }
    val productsWithNoActiveColor =
        repositorysMainGetter.repo1ProduitInfos.datasValue.count { product ->
            val colors = colorsByProduit[product.keyID] ?: emptyList()
            colors.isNotEmpty() && product.keyID !in activeColorProductIds
        }
    val sizeM1AfterImageMove = sizeM1 - productsWithNoActiveColor

    val inactiveProductIds = repositorysMainGetter.repo1ProduitInfos.datasValue
        .filter { product ->
            val colors = colorsByProduit[product.keyID] ?: emptyList()
            colors.isNotEmpty() && product.keyID !in activeColorProductIds
        }
        .map { it.keyID }.toSet()
    val sizeM13MovedWithImages = repositorysMainGetter.repo13TarificationInfos.datasValue
        .count { it.parent_M1Produit_KeyId in inactiveProductIds }
    val sizeM13AfterImageMove = sizeM13 - sizeM13MovedWithImages

    val duplicateTariffCount = repositorysMainGetter.repo13TarificationInfos.datasValue
        .groupBy { Pair(it.typeChoisi, it.parent_M1Produit_KeyId) }
        .values.filter { it.size > 1 }
        .sumOf { it.size - 1 }
    val sizeM13AfterDeduplicate = sizeM13 - duplicateTariffCount

    val sizeInvalidM2 = repositorysMainGetter.repo2Client.datasValue.count { client ->
        invalidM2ClientPredicate(client.numTelephone, client.latitude, client.longitude)
    }

    var showSubMenu by remember { mutableStateOf(false) }
    var confirmM8M10 by remember { mutableStateOf(false) }
    var isRunningM8M10 by remember { mutableStateOf(false) }
    var isRunningM13 by remember { mutableStateOf(false) }
    var isRunningM2 by remember { mutableStateOf(false) }
    var progressNoImage by remember { mutableStateOf<Float?>(null) }
    // TODO(1): summary text emitted by moveColorsWithoutImagesToNonActive after it finishes
    var summaryText by remember { mutableStateOf("") }

    Box {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text = "Cleanup  M1: $sizeM1  | M3: $sizeM3  | M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11  |  M13: $sizeM13",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { showSubMenu = true }
        )

        DropdownMenu(
            expanded = showSubMenu,
            onDismissRequest = {
                showSubMenu = false
                confirmM8M10 = false
                summaryText = ""
            }
        ) {

            // ── Header: snapshot of all repo sizes + last cleanup summary ────
            DropdownMenuItem(
                enabled = false,
                text = {
                    Text(
                        text = buildString {
                            appendLine("📦 État actuel des données")
                            append("M1: $sizeM1  M2: $sizeM2  M3: $sizeM3")
                            appendLine()
                            append("M8: $sizeM8  M10: $sizeM10  M11: $sizeM11  M13: $sizeM13")
                            if (summaryText.isNotBlank()) {
                                appendLine()
                                appendLine()
                                append(summaryText)
                            }
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (summaryText.isNotBlank())
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                },
                onClick = {}
            )

            HorizontalDivider()

            // ── M8 / M10 / M11 cleanup (2-click guard) ──────────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = if (isRunningM8M10) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = when {
                            isRunningM8M10 -> "Running…"
                            confirmM8M10   -> "Sure? Tap again to confirm  ⚠️"
                            else           -> "Cleanup  M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11  "
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isRunningM8M10 -> MaterialTheme.colorScheme.outline
                            confirmM8M10   -> MaterialTheme.colorScheme.error
                            else           -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                enabled = !isRunningM8M10,
                onClick = {
                    if (isRunningM8M10) return@DropdownMenuItem
                    if (!confirmM8M10) { confirmM8M10 = true; return@DropdownMenuItem }

                    isRunningM8M10 = true
                    confirmM8M10 = false

                    Log.d(TAG_CLEANUP, "── onClick START ──────────────────────────────")
                    Log.d(TAG_CLEANUP, "on_vent_key='$on_vent_key'")
                    Log.d(TAG_CLEANUP, "M8 size=${repositorysMainGetter.repo8BonVent.datasValue.size}  " +
                            "M10 size=${repositorysMainGetter.repo10OperationVentCouleur.datasValue.size}  " +
                            "M11 size=${repositorysMainGetter.repo11AchatOperation.datasValue.size}")

                    var pendingOps by mutableIntStateOf(2)
                    Log.d(TAG_CLEANUP, "pendingOps initialised → $pendingOps")

                    val onOneDone = {
                        val remaining = --pendingOps
                        Log.d(TAG_CLEANUP, "onOneDone fired — pendingOps now=$remaining")
                        if (remaining == 0) {
                            Log.d(TAG_CLEANUP, "ALL ops done → dismissing dropdown")
                            isRunningM8M10 = false
                            showSubMenu = false
                            onDismissDropdown()
                        }
                    }

                    Log.d(TAG_CLEANUP, "launching cleanupOldBonVents_Np …")
                    cleanupOldBonVents_Np(
                        repo8BonVent = repositorysMainGetter.repo8BonVent,
                        bonVents = repositorysMainGetter.repo8BonVent.datasValue,
                        on_vent_key = on_vent_key,
                        onDone = onOneDone
                    )

                    Log.d(TAG_CLEANUP, "launching cleanupInvalidOperations_Np …")
                    cleanupInvalidOperations_Np(
                        repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur,
                        on_vent_key = on_vent_key,
                        onDone = onOneDone
                    )

                    Log.d(TAG_CLEANUP, "calling M11AchatOperation.remove_ref() (sync)")
                    M11AchatOperation.Companion.remove_ref()
                    Log.d(TAG_CLEANUP, "── onClick END (coroutines still running) ──────")
                }
            )

            // ── M2 invalid clients: no phone + sitting on default pin ────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = null,
                        tint = if (isRunningM2) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = if (isRunningM2) "Running…"
                        else "Move invalid M2 clients  |  $sizeInvalidM2 → 0  (reste ${sizeM2 - sizeInvalidM2} / $sizeM2)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRunningM2) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                enabled = !isRunningM2 && sizeInvalidM2 > 0,
                onClick = {
                    if (isRunningM2) return@DropdownMenuItem
                    isRunningM2 = true
                    moveM2InvalidClients(
                        repositorysMainGetter = repositorysMainGetter,
                        onProgress = { fraction ->
                            if (fraction >= 1f) {
                                isRunningM2 = false
                                showSubMenu = false
                                onDismissDropdown()
                            }
                        }
                    )
                }
            )

            // ── M3 colors without images → move to non-active ────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = null,
                        tint = when {
                            progressNoImage != null -> MaterialTheme.colorScheme.outline
                            sizeNoImage == 0        -> MaterialTheme.colorScheme.outline
                            else                    -> MaterialTheme.colorScheme.secondary
                        }
                    )
                },
                text = {
                    when (val p = progressNoImage) {
                        null -> Text(
                            text = buildString {
                                append("Move colors without images  |  ")
                                append("M3: $sizeM3 → $sizeM3AfterImageMove")
                                append("  M1: $sizeM1 → $sizeM1AfterImageMove")
                                append("  M13: $sizeM13 → $sizeM13AfterImageMove")
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (sizeNoImage == 0) MaterialTheme.colorScheme.outline
                            else MaterialTheme.colorScheme.onSurface
                        )
                        else -> LinearProgressIndicator(
                            progress = { p },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
                enabled = progressNoImage == null && sizeNoImage > 0,
                onClick = {
                    if (progressNoImage != null) return@DropdownMenuItem
                    progressNoImage = 0f
                    summaryText = ""
                    moveColorsWithoutImagesToNonActive(
                        repositorysMainGetter = repositorysMainGetter,
                        onProgress = { fraction ->
                            progressNoImage = fraction
                            if (fraction >= 1f) {
                                progressNoImage = null
                                // stay open so user can read the summary; they can dismiss manually
                            }
                        },
                        // TODO(1): receive and store summary so header can display it
                        onSummary = { text ->
                            summaryText = text
                        }
                    )
                }
            )

            HorizontalDivider()

            // ── M13 duplicate tariffs → move to non-active ───────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = if (isRunningM13) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.tertiary
                    )
                },
                text = {
                    Text(
                        text = if (isRunningM13) "Running…"
                        else buildString {
                            append("Cleanup M13 duplicates  |  ")
                            append("$sizeM13 → $sizeM13AfterDeduplicate")
                            append("  (−$duplicateTariffCount doublons)")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRunningM13) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                enabled = !isRunningM13 && duplicateTariffCount > 0,
                onClick = {
                    if (isRunningM13) return@DropdownMenuItem
                    isRunningM13 = true
                    cleanupDuplicateTariffs(
                        repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
                        tariffs = repositorysMainGetter.repo13TarificationInfos.datasValue,
                        onDone = {
                            isRunningM13 = false
                            showSubMenu = false
                            onDismissDropdown()
                        }
                    )
                }
            )
        }
    }
}
