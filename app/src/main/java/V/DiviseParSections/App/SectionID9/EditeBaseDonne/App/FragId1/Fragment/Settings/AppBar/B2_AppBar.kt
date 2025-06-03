package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.AppBar

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.FilterDropdownMenu
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Filter.FilterState
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ModeAffichage
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppBar(
    onCreateProductAndCapture: () -> A_ProduitInfosProtoJuin3,
    onProductCreated: (A_ProduitInfosProtoJuin3) -> Unit,
    currentMode: ModeAffichage,
    onModeChanged: (ModeAffichage) -> Unit,
    filterState: FilterState,
    onFilterChanged: (FilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    val hasActiveFilters = filterState.hideNonDispo || filterState.hideDispoOnly || filterState.hidePetiteProbability

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gestion Produits",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button
            IconButton(
                onClick = { showFilterMenu = !showFilterMenu }
            ) {
                Card(
                    modifier = Modifier.size(40.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasActiveFilters)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (hasActiveFilters) Icons.Filled.FilterList else Icons.Outlined.FilterList,
                        contentDescription = "Filtres",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        tint = if (hasActiveFilters)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mode toggle button - now cycles through all three modes
            IconButton(
                onClick = {
                    val nextMode = when (currentMode) {
                        ModeAffichage.CATEGORIES_LIST -> ModeAffichage.PRODUCTS_LIST
                        ModeAffichage.PRODUCTS_LIST -> ModeAffichage.REORDER_GRID
                        ModeAffichage.REORDER_GRID -> ModeAffichage.CATEGORIES_LIST
                    }
                    onModeChanged(nextMode)
                }
            ) {
                val (icon, description) = when (currentMode) {
                    ModeAffichage.CATEGORIES_LIST -> Icons.Default.ViewModule to "Passer à la liste"
                    ModeAffichage.PRODUCTS_LIST -> Icons.Default.GridView to "Passer à la grille"
                    ModeAffichage.REORDER_GRID -> Icons.Default.List to "Passer aux catégories"
                }

                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            CameraFABProtoJuin3(
                onCreateProductAndCapture = onCreateProductAndCapture,
                onProductCreated = onProductCreated,
                webPQuality = 85
            )
        }
    }

    // Filter dropdown menu
    if (showFilterMenu) {
        FilterDropdownMenu(
            filterState = filterState,
            onFilterChanged = onFilterChanged,
            onDismiss = { showFilterMenu = false }
        )
    }
}
