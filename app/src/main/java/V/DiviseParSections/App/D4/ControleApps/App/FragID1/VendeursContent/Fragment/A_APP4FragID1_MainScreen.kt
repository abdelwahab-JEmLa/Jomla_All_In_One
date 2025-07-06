package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    val defaultGeneratedCompt = Z_AppCompt(
        nom = "Abdelwahab"
    ).apply {
        nomsMutableTags = addStringAuNomsMutableTags("Abdelwahab").joinToString(",")
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        VendeursContent(
            viewModel = viewModel,
            uiState = uiState,
            onVendeurSelected = viewModel::setActiveVendeur,
            onPeriodeSelected = viewModel::setActivePeriode,
            modifier = Modifier.fillMaxSize(),
            onUpdateceComptVendeurInsertBonsAchatAuPeriodID = viewModel::onUpdateceComptVendeurInsertBonsAchatAuPeriodID,
            onVendeurUpdate = viewModel::update_1_5
        )

        FloatingActionButton(
            onClick = {
                viewModel.aCentralFacade.setter.addAuRepoM9AppComptParFacade(defaultGeneratedCompt)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Default Compte",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun VendeursContent(
    uiState: VendeursUiState,
    onVendeurSelected: (Long) -> Unit,
    onPeriodeSelected: (Long) -> Unit,
    onVendeurUpdate: (Z_AppCompt) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
    viewModel: VendeursViewModel,
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
                        text = "Active _012_ComptsVendeurs ID: ${uiState.activeVendeurId}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    SectionDivider()
                }

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

                items(uiState.periodes) { periode ->
                    PeriodeItem(
                        periode = periode,
                        isActive = periode.vid == uiState.activePeriodeId,
                        onPeriodeSelected = onPeriodeSelected,
                        onUpdateceComptVendeurInsertBonsAchatAuPeriodID = onUpdateceComptVendeurInsertBonsAchatAuPeriodID
                    )
                }

                item {
                    AddPeriodeItem(
                        onAddPeriode = {
                            viewModel.addNewPeriode()
                            viewModel.addNewPeriodeIn_repo_01_VentsHistoriquesDataBase_Repository()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddPeriodeItem(
    onAddPeriode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddPeriode() }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ajouter une période",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Ajouter une période",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
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
fun VendeurItem(
    vendeur: Z_AppCompt,
    isActive: Boolean,
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (Z_AppCompt) -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }

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
                text = "Selected _012_ComptsVendeurs",
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
                    showEditDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier le vendeur",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = {
                    val updatedVendeur = vendeur.copy(hideAppScreen = !vendeur.hideAppScreen)
                    onVendeurUpdate(updatedVendeur)
                }
            ) {
                val icon = if (vendeur.hideAppScreen) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                }

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

    if (showEditDialog) {
        VendeurEditDialog(
            vendeur = vendeur,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedVendeur ->
                onVendeurUpdate(updatedVendeur)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun PeriodeItem(
    periode: MVentPeriode,
    isActive: Boolean,
    onPeriodeSelected: (Long) -> Unit,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    val viewModel: VendeursViewModel = koinInject()
    val activeVendeur = viewModel.getActiveVendeur()

    val isInsertPeriod = activeVendeur?.ceComptVendeurInsertBonsAchatAuPeriodID == periode.vid
    val isStartAffichePeriod = activeVendeur?.ceComptVendeurStartAffichePeriod == periode.vid

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
                    onUpdateceComptVendeurInsertBonsAchatAuPeriodID(periode.vid)
                }
            ) {
                val icon = if (isInsertPeriod) {
                    Icons.Default.Check
                } else {
                    Icons.Default.Add
                }

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
