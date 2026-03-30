package V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View.DropDownItem_WhenItsAchatsFragment_1
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

// ─────────────────────────────────────────────────────────────────────────────
// Confiserie catalogue id (static, from get_ListM21CataloguesCategorie())
// ─────────────────────────────────────────────────────────────────────────────
private const val CONFISERIE_CATALOGUE_ID = 1L

@Composable
fun FabDropdownMenu_BaseDonneEdite(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    // null  → idle; 0f..1f → running
    var suppressProgress by remember { mutableStateOf<Float?>(null) }

    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // ── TODO(1) — implemented ────────────────────────────────────────
            // Button + linear progress bar.
            // On click: for every M3CouleurProduitInfos whose parent product
            // belongs to a catalogue OTHER than Confiserie, set
            // dropBox_key = "Non Dispo", stopping further DropBox downloads.
            DropDownItemWBaseDonne_SuppressNonConfiserie(
                progress = suppressProgress,
                enabled = suppressProgress == null,
                onClick = {
                    coroutineScope.launch {
                        suppressProgress = 0f
                        withContext(Dispatchers.Default) {
                            val categories =
                                repositorysMainGetter.repoM16CategorieProduit.datasValue
                            val produits =
                                repositorysMainGetter.repo1ProduitInfos.datasValue
                            val couleurs =
                                repositorysMainGetter.repo03CouleurProduitInfos.datasValue

                            // IDs of categories that belong to Confiserie
                            val confiserieCatIds = categories
                                .filter { it.catalogueParentId == CONFISERIE_CATALOGUE_ID }
                                .map { it.id }
                                .toSet()

                            // Keys of products NOT in Confiserie
                            val nonConfiserieProduitKeys = produits
                                .filter { it.idParentCategorie !in confiserieCatIds }
                                .map { it.keyID }
                                .toSet()

                            // M3 colors to suppress (skip already-suppressed ones)
                            val toSuppress = couleurs.filter { couleur ->
                                couleur.parentBProduitInfosKeyID in nonConfiserieProduitKeys &&
                                        couleur.dropBox_key != "Non Dispo"
                            }

                            val total = toSuppress.size.coerceAtLeast(1)
                            toSuppress.forEachIndexed { index, couleur ->
                                val updated = couleur.copy(dropBox_key = "Non Dispo")
                                withContext(Dispatchers.Main) {
                                    repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(updated)
                                    suppressProgress = (index + 1).toFloat() / total
                                }
                            }
                        }
                        suppressProgress = null
                        onDismissDropdown()
                    }
                }
            )
            // ────────────────────────────────────────────────────────────────

            DropDownItemWBaseDonne_2(
                nomFun = "FABs Mode Edites Produit",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItemWBaseDonne_1(
                nomFun = "Givre le neveau Classement",
                onDismissDropdown = onDismissDropdown
            )
            DropDownItem_WhenItsAchatsFragment_1(
                nomFun = "",
                onDismissDropdown = onDismissDropdown
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TODO(1) — Suppress non-Confiserie DropBox item
// Shows a linear progress bar while the operation is running.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DropDownItemWBaseDonne_SuppressNonConfiserie(
    progress: Float?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = if (progress == null) "Supprimer DropBox (hors Confiserie)"
                    else "Suppression en cours…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            },
            enabled = enabled,
            onClick = onClick
        )
        if (progress != null) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Local dropdown helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * "FABs Mode Edites Produit" — toggles the product-edit FAB mode.
 */
@Composable
fun DropDownItemWBaseDonne_2(
    nomFun: String,
    onDismissDropdown: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = nomFun,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        onClick = {
            // Navigate to / activate FAB edit-product mode
            onDismissDropdown()
        }
    )
}

/**
 * "Givre le neveau Classement" — freezes / locks the current ranking level.
 */
@Composable
fun DropDownItemWBaseDonne_1(
    nomFun: String,
    onDismissDropdown: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = nomFun,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        onClick = {
            // Freeze / commit the current classement ranking
            onDismissDropdown()
        }
    )
}
