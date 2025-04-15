package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursUiState
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursViewModel
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@Composable
fun A_APP4FragID1_MainScreen(
    modifier: Modifier = Modifier,
    viewModel: VendeursViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    VendeursContent(
        uiState = uiState,
        onVendeurSelected = viewModel::setActiveVendeur ,
        onPeriodeSelected = viewModel::setActivePeriode,
        modifier = modifier
    )
}

@Composable
fun VendeursContent(
    uiState: VendeursUiState,
    onVendeurSelected: (Long) -> Unit,
    onPeriodeSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
               /* Text(
                    text = "Active Vendeur ID: ${uiState.activeVendeurId}",
                    style = MaterialTheme.typography.titleMedium
                )

                SectionDivider()

                Text(
                    text = "Liste des Vendeurs",
                    style = MaterialTheme.typography.titleLarge
                )

                VendeursList(
                    vendeurs = uiState.vendeurs,
                    activeVendeurId = uiState.activeVendeurId,
                    onVendeurSelected = onVendeurSelected
                )

                SectionDivider(color = Color.Red)
                       */
                Text(
                    text = "Périodes de Vente",
                    style = MaterialTheme.typography.titleLarge
                )
                /*
                Text(
                    text = "Active Periode ID: ${uiState.activePeriodeId}",
                    style = MaterialTheme.typography.bodyLarge
                )
                                  */
                SectionDivider()

                PeriodesList(
                    periodes = uiState.periodes,
                    activePeriodeId = uiState.activePeriodeId,
                    onPeriodeSelected = onPeriodeSelected
                )
            }
        }
    }
}

@Composable
fun SectionDivider(
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    height: Int = 24,
) {
    HorizontalDivider(
        modifier = Modifier.height(height.dp),
        color = color
    )
}

@Composable
fun VendeursList(
    vendeurs: List<_1_5_Vendeur>,
    activeVendeurId: Long,
    onVendeurSelected: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(vendeurs) { vendeur ->
            VendeurItem(
                vendeur = vendeur,
                isActive = vendeur.vid == activeVendeurId,
                onVendeurSelected = onVendeurSelected
            )
        }
    }
}

@Composable
fun VendeurItem(
    vendeur: _1_5_Vendeur,
    isActive: Boolean,
    onVendeurSelected: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onVendeurSelected(vendeur.vid)
            }
            .padding(vertical = 8.dp)
    ) {
        if (isActive) {
            Text(
                text = "Selected Vendeur",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = "ID: ${vendeur.vid}",
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Nom: ${vendeur.nom}",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PeriodesList(
    periodes: List<_1_4_PeriodeVent>,
    activePeriodeId: Long,
    onPeriodeSelected: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(periodes) { periode ->
            PeriodeItem(
                periode = periode,
                isActive = periode.vid == activePeriodeId,
                onPeriodeSelected = onPeriodeSelected
            )
        }
    }
}

@Composable
fun PeriodeItem(
    periode: _1_4_PeriodeVent,
    isActive: Boolean,
    onPeriodeSelected: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPeriodeSelected(periode.vid) }
            .padding(vertical = 8.dp)
    ) {
        if (isActive) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = "ID: ${periode.vid}",
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Heure de début: ${periode.heurDebutInString}",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


