package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.ViewModel.VendeursUiState
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.ViewModel.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.SectionDivider
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.VendeurItem
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.V9.Add.View.AddItemM14
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.View10.M14.View.View_M14VentPeriod
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
fun ViewList(
    viewModel: ViewModel_AdminAppPanelControleur,
    compts: List<Z_AppCompt>,
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (Z_AppCompt) -> Unit,
    uiState: VendeursUiState,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit
) {
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

        items(compts) { vendeur ->
            VendeurItem(
                viewModel = viewModel,
                vendeur = vendeur,
                onVendeurSelected = onVendeurSelected,
                onVendeurUpdate = onVendeurUpdate
            )
        }

        item {
            SectionDivider(color = Color.Red)

            Text(
                text = "Périodes de Vente",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Active Periode ID: ${uiState.activePeriodeId}",
                style = MaterialTheme.typography.bodyLarge
            )

            SectionDivider()
        }

        val M14VentPeriodList = viewModel.aCentralFacade.get.repo14VentPeriode.datasValue
        items(M14VentPeriodList) { periode ->
            View_M14VentPeriod(
                m14VentPeriode = periode,
                viewModel = viewModel,
            )
        }

        item {
            AddItemM14(
                viewModel =viewModel
            )
        }
    }
}
