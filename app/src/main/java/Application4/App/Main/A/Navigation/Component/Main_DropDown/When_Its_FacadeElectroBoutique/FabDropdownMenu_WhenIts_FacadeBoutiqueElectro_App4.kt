package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique

import Application4.App.Fragment.ID1.Fragment.ViewModel.Model.Prioriter
import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    viewModelNewProtoPatterns: ViewModel_NewProtoPatterns = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
        ) {
            // ── Upload button ──────────────────────────────────────────────
            UploadButton(isUploading, coroutineScope, onDismissDropdown)

            // ── Prioriter filter toggles ───────────────────────────────────
            PrioriterToggleItem(viewModelNewProtoPatterns)
        }
    }
}

// ---------------------------------------------------------------------------
// Upload button (unchanged logic)
// ---------------------------------------------------------------------------
@Composable
private fun UploadButton(
    isUploading: Boolean,
    coroutineScope: CoroutineScope,
    onDismissDropdown: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        },
        text = {
            Text(
                text = if (isUploading) "Envoi en cours..." else "Envoyer produits filtrés → Firestore",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        enabled = !isUploading,
        onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                onDismissDropdown()
            }
        }
    )
}

// ---------------------------------------------------------------------------
// Three-chip toggle row for affiche_produits_Ou_On_TagPrioriter
// ---------------------------------------------------------------------------
@Composable
private fun PrioriterToggleItem(viewModelNewProtoPatterns: ViewModel_NewProtoPatterns) {
    val activeFilter = viewModelNewProtoPatterns.active_Datas.affiche_produits_Ou_On_TagPrioriter

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Priorité affichage produits",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Prioriter.entries.forEach { prioriter ->
                        val isSelected = activeFilter?.contains(prioriter) == true
                        val label = when (prioriter) {
                            Prioriter.Dernier_VentAchat_Est_Moin_Mois  -> "< Mois"
                            Prioriter.Dernier_VentAchat_Est_Moin_Semain -> "< Sem"
                            Prioriter.PlusDe80P_Ne_Le_Voit_Pas          -> "80%"
                            else -> {""}
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                // Toggle: add if absent, remove if present
                                val current = activeFilter?.toMutableSet() ?: mutableSetOf()
                                if (isSelected) current.remove(prioriter) else current.add(prioriter)
                                viewModelNewProtoPatterns.active_Datas
                                    .affiche_produits_Ou_On_TagPrioriter =
                                    current.ifEmpty { null }
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        },
        onClick = { /* row is non-clickable; chips handle their own clicks */ }
    )
}
