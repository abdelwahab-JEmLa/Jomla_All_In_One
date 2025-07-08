package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.SectionDivider
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.View_M9
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.V9.Add.View.AddItemM14
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.View10.M14.View.View_M14VentPeriod
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
fun ViewList(
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
            View_M9(
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
            // Show a message when no periods exist
            item {
                Text(
                    text = "Aucune période de vente trouvée",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Always show the add item component
        item {
            AddItemM14(
                viewModel = viewModel
            )
        }
    }
}
