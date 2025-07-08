package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Ui.SectionDivider
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.Item2.View_M9AppCompt.View.View_M9AppCompt
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun A_MainListView(
    viewModel: ViewModel_AdminAppPanelControleur,
) {
    val listM9AppCompt = viewModel.aCentralFacade.get.repo9AppCompt.datasValue

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
    }
}

