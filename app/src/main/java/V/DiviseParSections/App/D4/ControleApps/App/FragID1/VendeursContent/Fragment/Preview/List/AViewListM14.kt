package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_DefaultAddItem_M14VentPeriode
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.SectionDivider
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
 fun ViewList_M14VentPeriod(
    viewModel: ViewModel_M14VentPeriod,
    relative_M9AppCompt: Z_AppCompt? ,
) {
    val m14VentPeriodList = viewModel.aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            SectionDivider()
        }

        // Show periods if they exist
        if (m14VentPeriodList.isNotEmpty()) {
            items(m14VentPeriodList) { periode ->
                View_M14VentPeriod(
                    viewModel = viewModel,
                    relative_M14VentPeriode = periode,
                    relative_M9AppCompt = relative_M9AppCompt
                )
            }
        } else {
            item {
                Text(
                    text = "Aucune période de vente trouvée",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        item {
            View_DefaultAddItem_M14VentPeriode(
                viewModel = viewModel
            )
        }
    }
}
