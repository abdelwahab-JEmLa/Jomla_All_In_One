package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.ViewModel.VendeursUiState
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.ViewModel.ViewModel_AdminAppPanelControleur
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Ui.VendeurEditDialog
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.ViewList
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun View_AdminAppPanelControleur(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AdminAppPanelControleur = koinInject(),
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
            onVendeurUpdate = viewModel::update_1_5,
            modifier = Modifier.fillMaxSize(),
            onUpdateceComptVendeurInsertBonsAchatAuPeriodID = viewModel::onUpdateceComptVendeurInsertBonsAchatAuPeriodID
        )

        FloatingActionButton(
            onClick = {
                viewModel.aCentralFacade.set.addAuRepoM9AppComptParFacade(defaultGeneratedCompt)
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
    viewModel: ViewModel_AdminAppPanelControleur,
    uiState: VendeursUiState,
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (Z_AppCompt) -> Unit,
    modifier: Modifier = Modifier,
    onUpdateceComptVendeurInsertBonsAchatAuPeriodID: (Long) -> Unit,
) {
    val repo9AppCompt = viewModel.aCentralFacade.get.repo9AppCompt
    val compts = repo9AppCompt.datasValue

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ViewList(
                viewModel,
                compts,
                onVendeurSelected,
                onVendeurUpdate,
                uiState,
                onUpdateceComptVendeurInsertBonsAchatAuPeriodID
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
    onVendeurSelected: (Long) -> Unit,
    onVendeurUpdate: (Z_AppCompt) -> Unit,
    viewModel: ViewModel_AdminAppPanelControleur,
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val isActive = (viewModel.aCentralFacade.focusedActiveValuesFacade.get.currentM9AppCompt?.keyID
        ?: "") == vendeur.keyID

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
                text = "ComptsVendeurs",
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

