package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursUiState
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.ViewModel.VendeursViewModel
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        onVendeurSelected = viewModel::setActiveVendeur,
        onPeriodeSelected = viewModel::setActivePeriode,
        modifier = modifier,
        onUpdateceComptVendeurInsertBonsAchatAuPeriodID =
            viewModel::onUpdateceComptVendeurInsertBonsAchatAuPeriodID,
        onVendeurUpdate = viewModel::update_1_5
    )
}

@Composable
fun VendeursContent(
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                VendeursList(
                    uiState = uiState,
                    vendeurs = uiState.vendeurs,
                    activeVendeurId = uiState.activeVendeurId,
                    onVendeurSelected = onVendeurSelected,
                    onVendeurUpdate = onVendeurUpdate
                )

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

                PeriodesList(
                    periodes = uiState.periodes,
                    activePeriodeId = uiState.activePeriodeId,
                    onPeriodeSelected = onPeriodeSelected,
                    onUpdateceComptVendeurInsertBonsAchatAuPeriodID = onUpdateceComptVendeurInsertBonsAchatAuPeriodID
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
    onVendeurUpdate: (_1_5_Vendeur) -> Unit,
    uiState: VendeursUiState,
) {
    Text(
        text = "Liste des Vendeurs",
        style = MaterialTheme.typography.titleLarge
    )
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(
                text = "Active Vendeur ID: ${uiState.activeVendeurId}",
                style = MaterialTheme.typography.titleMedium
            )

            SectionDivider()
        }
        items(vendeurs) { vendeur ->
            VendeurItem(
                vendeur = vendeur,
                isActive = vendeur.vid == activeVendeurId,
                onVendeurSelected = onVendeurSelected,
                onVendeurUpdate = onVendeurUpdate
            )
        }
    }
}

@Composable
fun VendeurItem(
    vendeur: _1_5_Vendeur,
    isActive: Boolean,
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (_1_5_Vendeur) -> Unit,
) {
    // Determine background color based on whether this is the active vendeur
    val backgroundColor = when {
        isActive -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onVendeurSelected(vendeur.vid)
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (isActive) {
            Text(
                text = "Selected Vendeur",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ID: ${vendeur.vid}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    // Toggle hideAppScreen property and update the vendeur
                    val updatedVendeur = vendeur.copy(hideAppScreen = !vendeur.hideAppScreen)
                    onVendeurUpdate(updatedVendeur)
                }
            ) {
                // Change icon based on hideAppScreen status
                val icon = if (vendeur.hideAppScreen) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                }

                // Change icon color
                val tint = if (vendeur.hideAppScreen) {
                    Color.Gray
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Icon(
                    imageVector = icon,
                    contentDescription = if (vendeur.hideAppScreen) "Show App Screen" else "Hide App Screen",
                    tint = tint
                )
            }
        }

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
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(periodes) { periode ->
            PeriodeItem(
                periode = periode,
                isActive = periode.vid == activePeriodeId,
                onPeriodeSelected = onPeriodeSelected,
                onUpdateceComptVendeurInsertBonsAchatAuPeriodID
            )
        }
    }
}

@Composable
fun PeriodeItem(
    periode: _1_4_PeriodeVent,
    isActive: Boolean,
    onPeriodeSelected: (Long) -> Unit,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    val viewModel: VendeursViewModel = koinInject()

    val activeVendeur = viewModel.getActiveVendeur()

    // Check if this period is the one set for ceComptVendeurInsertBonsAchatAuPeriodID
    val isInsertPeriod = activeVendeur?.ceComptVendeurInsertBonsAchatAuPeriodID == periode.vid

    // Check if this period is the one set for ceComptVendeurStartAffichePeriod
    val isStartAffichePeriod = activeVendeur?.ceComptVendeurStartAffichePeriod == periode.vid

    // Determine background color based on whether this is the start display period
    val backgroundColor = when {
        isStartAffichePeriod -> MaterialTheme.colorScheme.primaryContainer
        isActive -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPeriodeSelected(periode.vid) }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (isActive) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ID: ${periode.vid}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    // Call function to update comptVendeurInsertBonsAchatAuPeriodID
                    onUpdateceComptVendeurInsertBonsAchatAuPeriodID(periode.vid)
                }
            ) {
                // Change icon based on whether this is the insert period
                val icon = if (isInsertPeriod) {
                    Icons.Default.Check
                } else {
                    Icons.Default.Add
                }

                // Change icon color based on status
                val tint = if (isInsertPeriod) {
                    Color.Green
                } else {
                    MaterialTheme.colorScheme.primary
                }

                Icon(
                    imageVector = icon,
                    contentDescription = if (isInsertPeriod) "Current Insert Period" else "Set as Insert Period",
                    tint = tint
                )
            }
        }

        Text(
            text = "Heure de début: ${periode.heurDebutInString}",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
