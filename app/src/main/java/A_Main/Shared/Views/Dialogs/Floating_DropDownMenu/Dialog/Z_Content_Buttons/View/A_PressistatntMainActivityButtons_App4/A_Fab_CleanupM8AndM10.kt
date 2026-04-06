package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.A_PressistatntMainActivityButtons_App4

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.DropDownItemWBaseDonne_OrganiserLocaleParCatalogue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.compose.koinInject

@Composable
fun Fab_CleanupM8AndM10(
    on_vent_key: String,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    onDismissDropdown: () -> Unit,
) {
    val sizeM1  = repositorysMainGetter.repo1ProduitInfos.datasValue.size
    val sizeM3  = repositorysMainGetter.repo3CouleurProduit.datasValue.size
    val sizeM8  = repositorysMainGetter.repo8BonVent.datasValue.size
    val sizeM2  = repositorysMainGetter.repo2Client.datasValue.size
    val sizeM10 = repositorysMainGetter.repo10OperationVentCouleur.datasValue.size
    val sizeM11 = repositorysMainGetter.repo11AchatOperation.datasValue.size
    val sizeM13 = repositorysMainGetter.repo13TarificationInfos.datasValue.size

    val sizeNoImage = repositorysMainGetter.repo3CouleurProduit.datasValue.count { color ->
        color.nomImageFichieSansEtansion.isBlank() || color.nomImageFichieSansEtansion == "Non Dispo"
    }

    // Badge count reuses the same predicate as the move function — no duplication.
    val sizeInvalidM2 = repositorysMainGetter.repo2Client.datasValue.count { client ->
        invalidM2ClientPredicate(client.numTelephone, client.latitude, client.longitude)
    }

    var showSubMenu    by remember { mutableStateOf(false) }
    var confirmM8M10   by remember { mutableStateOf(false) }
    var isRunningM8M10 by remember { mutableStateOf(false) }
    var isRunningM13   by remember { mutableStateOf(false) }
    var isRunningM2    by remember { mutableStateOf(false) }

    // ── No-image move: granular progress (null = idle, 0..1 = running) ───────
    var progressNoImage by remember { mutableStateOf<Float?>(null) }

    Box {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector        = Icons.Default.DeleteSweep,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Text(
                    text  = "Cleanup  M1: $sizeM1  | M3: $sizeM3  | M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11  |  M13: $sizeM13",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { showSubMenu = true }
        )

        DropdownMenu(
            expanded         = showSubMenu,
            onDismissRequest = { showSubMenu = false; confirmM8M10 = false }
        ) {
            // ── M8 / M10 / M11 cleanup (2-click guard) ──────────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint               = if (isRunningM8M10) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = when {
                            isRunningM8M10 -> "Running…"
                            confirmM8M10   -> "Sure? Tap again to confirm  ⚠️"
                            else           -> "Cleanup  M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11  |  M2: $sizeM2"
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
                    confirmM8M10   = false

                    cleanupOldBonVents_Np(
                        repo8BonVent = repositorysMainGetter.repo8BonVent,
                        bonVents     = repositorysMainGetter.repo8BonVent.datasValue,
                        on_vent_key  = on_vent_key
                    )
                    cleanupInvalidOperations_Np(
                        repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur,
                        on_vent_key                = on_vent_key
                    )
                    M11AchatOperation.Companion.remove_ref()

                    showSubMenu = false
                    onDismissDropdown()
                }
            )

            // ── M2 invalid clients: no phone + sitting on default pin ────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Default.PersonOff,
                        contentDescription = null,
                        tint               = if (isRunningM2) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.error
                    )
                },
                text = {
                    Text(
                        text = if (isRunningM2) "Running…"
                        else "Move invalid M2 clients  |  $sizeInvalidM2 / $sizeM2",
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

            // ── M13 duplicate tariffs ────────────────────────────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector        = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint               = if (isRunningM13) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.tertiary
                    )
                },
                text = {
                    Text(
                        text  = if (isRunningM13) "Running…"
                        else "Cleanup M13 duplicates  |  $sizeM13 tariffs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRunningM13) MaterialTheme.colorScheme.outline
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                enabled = !isRunningM13,
                onClick = {
                    if (isRunningM13) return@DropdownMenuItem
                    isRunningM13 = true
                    cleanupDuplicateTariffs(
                        repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
                        tariffs                 = repositorysMainGetter.repo13TarificationInfos.datasValue
                    )
                    showSubMenu = false
                    onDismissDropdown()
                }
            )

            DropDownItemWBaseDonne_OrganiserLocaleParCatalogue(
                progress = progressNoImage,
                enabled  = sizeNoImage > 0 && progressNoImage == null,
                onClick  = {
                    if (progressNoImage != null) return@DropDownItemWBaseDonne_OrganiserLocaleParCatalogue

                    progressNoImage = 0f

                    moveColorsWithoutImagesToNonActive(
                        repositorysMainGetter = repositorysMainGetter,
                        onProgress = { fraction ->
                            progressNoImage = fraction
                            if (fraction >= 1f) {
                                showSubMenu = false
                                onDismissDropdown()
                            }
                        }
                    )
                }
            )
        }
    }
}
