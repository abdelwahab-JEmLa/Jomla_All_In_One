package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.Test.List

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.SectionDivider
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_DefaultAddItem_M14VentPeriode
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod
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
 fun ViewListM14(
    viewModel: ViewModel_M14VentPeriod
) {
    val M14VentPeriodList = viewModel.aCentralFacade.getRepositorys.repo14VentPeriode.datasValue

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            SectionDivider(color = Color.Red)

            Text(
                text = "Périodes de Vente",
                style = MaterialTheme.typography.titleLarge
            )

            // Add debug info to see what's happening
            Text(
                text = "Count: ${M14VentPeriodList.size}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            SectionDivider()
        }

        // Show periods if they exist
        if (M14VentPeriodList.isNotEmpty()) {
            items(M14VentPeriodList) { periode ->
                View_M14VentPeriod(
                    viewModel = viewModel,
                    m14VentPeriode = periode,
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
