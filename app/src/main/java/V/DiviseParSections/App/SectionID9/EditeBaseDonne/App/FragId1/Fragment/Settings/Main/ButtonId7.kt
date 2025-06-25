package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.B4CatalogueCategoriesRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ButtonId7(
    showLabels: Boolean,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val catalogues = B4CatalogueCategoriesRepository()
    val currentCatalogueIndex = catalogues.indexOfFirst { it.id == uiState.activeCatalogue.id }
    val nextCatalogueIndex = if (currentCatalogueIndex >= catalogues.size - 1) 0 else currentCatalogueIndex + 1
    val nextCatalogue = catalogues[nextCatalogueIndex]

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showLabels) {
            Text(
                text = "Current: ${uiState.activeCatalogue.nom}",
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = Color.Blue,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        FloatingActionButton(
            onClick = {
                // Toggle to the next catalogue in the list (cycles through all catalogues)
                viewModel.toggleToCatalogue(nextCatalogue.id)
            },
            modifier = Modifier.size(48.dp),
            containerColor = Color.Blue
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Switch to next catalogue: ${nextCatalogue.nom}",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
