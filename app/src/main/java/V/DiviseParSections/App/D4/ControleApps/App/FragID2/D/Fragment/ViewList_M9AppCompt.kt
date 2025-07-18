package V.DiviseParSections.App.D4.ControleApps.App.FragID2.D.Fragment

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.SectionDivider
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
fun ViewList_M9AppCompt(
    viewModel: ViewModel_M9AppCompt
) {
    val list_M9AppCompt = viewModel.aCentralFacade.repoMainGetter.repo9AppCompt.datasValue

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            SectionDivider(color = Color.Red)

            Text(
                text = "_M9AppCompt",
                style = MaterialTheme.typography.titleLarge
            )

            // Add debug info to see what's happening
            Text(
                text = "Count: ${list_M9AppCompt.size}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            SectionDivider()
        }

        // Show periods if they exist
        if (list_M9AppCompt.isNotEmpty()) {
            items(list_M9AppCompt) {
                View_M9AppCompt(
                    viewModel = viewModel,
                    relative_M9AppCompt = it,
                )
            }
        } else {
            item {
                Text(
                    text = "Aucune data trouvée",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

    }
}
