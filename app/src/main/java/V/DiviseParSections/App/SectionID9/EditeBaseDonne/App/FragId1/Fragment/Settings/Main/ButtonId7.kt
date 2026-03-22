package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
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
import org.koin.compose.koinInject

@Composable
fun ButtonId7(
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    showLabels: Boolean,
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val catalogues = get_ListM21CataloguesCategorie()
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
                viewModel.toggleToCatalogue(nextCatalogue.id)
                val cataloge = nextCatalogue
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(
                        active_Catalogue_Pour_NewAddedProduit = cataloge
                    )
                )
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
