package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.But.View

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
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
    val sizeM8 = repositorysMainGetter.repo8BonVent.datasValue.size
    val sizeM10 = repositorysMainGetter.repo10OperationVentCouleur.datasValue.size
    val sizeM11 = repositorysMainGetter.repo11AchatOperation.datasValue.size
    val sizeM13 = repositorysMainGetter.repo13TarificationInfos.datasValue.size

    // Controls visibility of the nested sub-dropdown
    var showSubMenu by remember { mutableStateOf(false) }

    // Button 1 state
    var confirmM8M10 by remember { mutableStateOf(false) }   // true = waiting for second click
    var isRunningM8M10 by remember { mutableStateOf(false) }

    // Button 2 state
    var isRunningM13 by remember { mutableStateOf(false) }

    Box {
        // ── Main entry in the parent dropdown ──────────────────────────────
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
                    text = "Cleanup  M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11  |  M13: $sizeM13",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { showSubMenu = true }
        )

        // ── Sub-dropdown revealed on first click ───────────────────────────
        DropdownMenu(
            expanded = showSubMenu,
            onDismissRequest = {
                showSubMenu = false
                confirmM8M10 = false   // reset guard if dismissed
            }
        ) {

            // ── Button 1 : M8 / M10 / M11 cleanup with 2-click safety ─────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = when {
                            isRunningM8M10 -> MaterialTheme.colorScheme.outline
                            confirmM8M10 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                },
                text = {
                    Text(
                        text = when {
                            isRunningM8M10 -> "Running…"
                            confirmM8M10 -> "Sure? Tap again to confirm  ⚠️"
                            else -> "Cleanup  M8: $sizeM8  |  M10: $sizeM10  |  M11: $sizeM11"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isRunningM8M10 -> MaterialTheme.colorScheme.outline
                            confirmM8M10 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                },
                enabled = !isRunningM8M10,
                onClick = {
                    if (isRunningM8M10) return@DropdownMenuItem

                    if (!confirmM8M10) {
                        // First click → ask for confirmation
                        confirmM8M10 = true
                        return@DropdownMenuItem
                    }

                    // Second click → run cleanup
                    isRunningM8M10 = true
                    confirmM8M10 = false

                    cleanupOldBonVents_Np(
                        repo8BonVent = repositorysMainGetter.repo8BonVent,
                        bonVents = repositorysMainGetter.repo8BonVent.datasValue,
                        on_vent_key = on_vent_key
                    )
                    cleanupInvalidOperations_Np(
                        repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur,
                        on_vent_key = on_vent_key
                    )
                    M11AchatOperation.Companion.remove_ref()

                    showSubMenu = false
                    onDismissDropdown()
                }
            )

            // ── Button 2 : M13 duplicate-tariff cleanup ───────────────────
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = if (isRunningM13)
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.tertiary
                    )
                },
                text = {
                    Text(
                        text = if (isRunningM13)
                            "Running…"
                        else
                            "Cleanup M13 duplicates  |  $sizeM13 tariffs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isRunningM13)
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                enabled = !isRunningM13,
                onClick = {
                    if (isRunningM13) return@DropdownMenuItem

                    isRunningM13 = true

                    cleanupDuplicateTariffs(
                        repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos,
                        tariffs = repositorysMainGetter.repo13TarificationInfos.datasValue
                    )

                    showSubMenu = false
                    onDismissDropdown()
                }
            )
        }
    }
}
