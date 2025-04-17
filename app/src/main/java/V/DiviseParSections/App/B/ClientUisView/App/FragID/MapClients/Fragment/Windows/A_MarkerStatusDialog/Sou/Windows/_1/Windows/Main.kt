package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.PeriodeItem
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.SectionDivider
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.VendeurItem
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.VendeursContent
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursUiState
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject


@Composable
fun Main(
    modifier: Modifier = Modifier,
    viewModel: ViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    VendeursContent(
        uiState = uiState,
        onVendeurSelected = viewModel::setActiveVendeur,
        onPeriodeSelected = viewModel::setActivePeriode,
        modifier = modifier,
        onUpdateceComptVendeurInsertBonsAchatAuPeriodID =
            viewModel::onUpdateceComptVendeurInsertBonsAchatAuPeriodID,
        onVendeurUpdate = viewModel::update_1_5
    )
}

@Composable
fun MainScreen(
    uiState: VendeursUiState,
    onVendeurSelected: (Long) -> Unit,
    onPeriodeSelected: (Long) -> Unit,
    onVendeurUpdate: (_1_5_Vendeur) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MainList(
                uiState,
                onVendeurSelected,
                onVendeurUpdate,
                onPeriodeSelected,
                onUpdateceComptVendeurInsertBonsAchatAuPeriodID
            )
        }
    }
}

@Composable
private fun MainList(
    uiState: VendeursUiState,
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (_1_5_Vendeur) -> Unit,
    onPeriodeSelected: (Long) -> Unit,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Liste des Vendeurs",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Active Vendeur ID: ${uiState.activeVendeurId}",
                style = MaterialTheme.typography.titleMedium
            )

            SectionDivider()
        }

        // Add vendeurs as items
        items(uiState.vendeurs) { vendeur ->
            VendeurItem(
                vendeur = vendeur,
                isActive = vendeur.vid == uiState.activeVendeurId,
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

        // Add periodes as items
        items(uiState.periodes) { periode ->
            PeriodeItem(
                periode = periode,
                isActive = periode.vid == uiState.activePeriodeId,
                onPeriodeSelected = onPeriodeSelected,
                onUpdateceComptVendeurInsertBonsAchatAuPeriodID = onUpdateceComptVendeurInsertBonsAchatAuPeriodID
            )
        }
    }
}
