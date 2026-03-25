package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.Do
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Each selectable option is described by the [Do] entry **and** the [Do.From]
 * sub-value it implies, because [Do.DeleteInsertAll] is a single enum entry
 * whose [Do.from] is fixed to [Do.From.Ref_All_Datas] at declaration time —
 * it cannot be re-instantiated with a different [Do.From].
 *
 * We therefore carry the intended [Do.From?] alongside the [Do] entry and
 * match against it when highlighting the active chip.
 *
 * Note: the ViewModel init block already branches on [Do.from]:
 *   Do.From.Ref_All_Datas, null → Initializer_App4 (full)
 *   Do.From.Active_Key          → initializeAllRepositories_ByFilter()
 * so writing [Do.DeleteInsertAll] (from = Ref_All_Datas) vs a future entry
 * with Active_Key is what drives behaviour.  Until the enum gains a second
 * DeleteInsertAll variant both "DeleteInsertAll" chips write the same entry;
 * the distinction is kept here for UI clarity and future-proofing.
 */
private data class NextStartOption(
    val label: String,
    val doEntry: Do,
    val matchFrom: Do.From?,          // used only for isSelected check
)

private val OPTIONS = listOf(
    NextStartOption(
        label     = "Rien (aucune action)",
        doEntry   = Do.StandartInit,
        matchFrom = null,
    ),
    NextStartOption(
        label     = "Supprimer + Réinsérer (toutes les refs)",
        doEntry   = Do.DeleteInsertAll,
        matchFrom = Do.From.Ref_All_Datas,
    ),
    NextStartOption(
        label     = "Supprimer + Réinsérer (clé active)",
        doEntry   = Do.DeleteInsertAll,
        matchFrom = Do.From.Active_Key,
    ),
)

@Composable
fun NextStart_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit,
) {
    val activeCompt      = viewModelNewProtoPatterns.active_Datas.active_M9Compt
    val currentNextStart = activeCompt?.next_start ?: Do.StandartInit

    var isExpanded by remember { mutableStateOf(false) }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (currentNextStart != Do.StandartInit)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {

                // ── Header ──────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text     = "Action au prochain démarrage",
                        style    = MaterialTheme.typography.labelMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    Icon(
                        imageVector        = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (isExpanded) {
                    // ── Option chips ─────────────────────────────────────────
                    OPTIONS.forEach { option ->
                        // A chip is selected when both the Do entry AND the From match.
                        val isSelected = currentNextStart == option.doEntry &&
                                currentNextStart.from == option.matchFrom

                        FilterChip(
                            selected = isSelected,
                            onClick  = {
                                activeCompt?.let { compt ->
                                    viewModelNewProtoPatterns.update_active_Compt(
                                        compt.copy(next_start = option.doEntry)
                                    )
                                }
                                onDismissDropdown()
                            },
                            label = {
                                Text(
                                    text  = option.label,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor     = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                } else {
                    // ── Collapsed summary ────────────────────────────────────
                    val activeOption = OPTIONS.firstOrNull {
                        currentNextStart == it.doEntry && currentNextStart.from == it.matchFrom
                    }
                    if (activeOption != null && activeOption.doEntry != Do.StandartInit) {
                        Text(
                            text     = activeOption.label,
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        },
        onClick = {},
    )
}
