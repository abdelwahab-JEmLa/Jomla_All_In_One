package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Ui.SectionDivider
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.Item2.View_M9AppCompt.View.View_M9AppCompt
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.Item3.View_DefaultAddItem_M14VentPeriode.View.View_DefaultAddItem_M14VentPeriode
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.item1.View_M14VentPeriod.View.View_M14VentPeriod
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
fun A_MainListView(
    viewModel: ViewModel_AdminAppPanelControleur,
) {
    val listM9AppCompt = viewModel.aCentralFacade.get.repo9AppCompt.datasValue
    val M14VentPeriodList = viewModel.aCentralFacade.get.repo14VentPeriode.datasValue

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Liste des AppCompt",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Active Compt ID: ${
                    viewModel.aCentralFacade
                        .focusedActiveValuesFacade.get.currentM9AppCompt?.nom
                }",
                style = MaterialTheme.typography.titleMedium
            )

            SectionDivider()
        }

        items(listM9AppCompt) { compt ->
            View_M9AppCompt(
                viewModel = viewModel,
                compt = compt,
            )
        }

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

        // FIXED: Move View_DefaultAddItem_M14VentPeriode outside the if/else block and ensure it's always displayed
        item {
            // Add some spacing before the add button
            SectionDivider()

            View_DefaultAddItem_M14VentPeriode(
                viewModel = viewModel
            )
        }
    }
}
