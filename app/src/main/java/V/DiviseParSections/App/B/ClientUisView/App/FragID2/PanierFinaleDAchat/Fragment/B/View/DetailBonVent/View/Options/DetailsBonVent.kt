package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.CartSummarySection
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.ClientDetailsSection
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Details.UI.B.UI.GBonVentInfosHeader
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.ErrorCard
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.PeriodDetailsSection
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
fun DetailsBonVentPrev() {
    DetailsBonVent()
}

val petitePaddine = 4.dp //rename

data class ActionButtonData(
    val key: String,
    val content: @Composable () -> Unit
)

@Composable
fun DetailsBonVent(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printHandler = remember { PrintReceiptHandler() }

    val zAppComptRepositoryComposable =
        viewModel.uiStateCentralRepositorys.repo9AppCompt
    val comptAppActuelle = zAppComptRepositoryComposable.currentAppCompt

    val fVentCouleurOperationRepository =
        viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur

    val ouvertPeriodKeyId = comptAppActuelle?.current_OnVent_M14VentPeriode_KeyID ?: ""


    // Create list of action buttons for LazyColumn
    val actionButtons = remember(uiState, isMinimized) {
        listOf(
            ActionButtonData("panie_mode") {
                PanieModeButton(
                    uiState = uiState,
                    showLabel = !isMinimized,
                    onTogglePanieMode = { viewModel.togglePanieMode() }
                )
            },
            ActionButtonData("filter") {
                FilterButton(
                    uiState = uiState,
                    showLabel = !isMinimized,
                    onToggleFilter = { viewModel.toggelePanierFilterNonTrouve() }
                )
            },
            ActionButtonData("print") {
                PrintButton(
                    showLabel = !isMinimized,
                    onPrint = {
                        val fClientRepository =
                            viewModel.uiStateCentralRepositorys.repo2Client
                        printHandler.printVentReceipt(
                            context = context,
                            fVentCouleurOperationRepository = fVentCouleurOperationRepository,
                            bProduitInfosRepository = viewModel.uiStateCentralRepositorys.repoM1ProduitInfos,
                            b1CouleurOuGoutProduitDataBaseRepository = viewModel.uiStateCentralRepositorys.repo3CouleurProduitInfos,
                            client = viewModel.aCentral.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos,
                            scope = scope
                        )
                    }
                )
            },
            ActionButtonData("confirmation") {
                ConfirmationButton(
                    viewModel = viewModel,
                    showLabel = !isMinimized,
                )
            },
            ActionButtonData("minimize") {
                MinimizeButton(
                    isMinimized = isMinimized,
                    showLabel = !isMinimized,
                    onToggleMinimized = { viewModel.toggleMinimizedState() }
                )
            }
        )
    }

    if (comptAppActuelle != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = if (isMinimized) 180.dp else 400.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(petitePaddine),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(petitePaddine),
                    // FIXED: Compact spacing when minimized
                    verticalArrangement = Arrangement.spacedBy(if (isMinimized) 6.dp else 12.dp)
                ) {
                    if (!isMinimized) {
                        GBonVentInfosHeader(viewModel)
                    }
                    if (!isMinimized) {
                        PeriodDetailsSection(
                            viewModel = viewModel,
                            ouvertPeriodKeyId = ouvertPeriodKeyId,
                        )
                    }

                    ClientDetailsSection(
                        modifier = Modifier,
                        viewModel = viewModel,
                    )

                    // FIXED: Only show divider when not minimized to save space
                    if (!isMinimized) {
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }

                    CartSummarySection(
                        viewModel,
                    )
                }
            }

            // FIXED: Using LazyColumn for better scrolling behavior
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(petitePaddine)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = actionButtons,
                    key = { it.key }
                ) { buttonData ->
                    buttonData.content()
                }
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}

@Composable
fun PanieModeButton(
    uiState: ZViewModel_Sec1Frag3.UiState_Sec1Frag3,
    showLabel: Boolean,
    onTogglePanieMode: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onTogglePanieMode,
            containerColor = when (uiState.panieMode) {
                ZViewModel_Sec1Frag3.PanieMode.Delivery -> Color(0xFF4CAF50) // Green for Delivery
                ZViewModel_Sec1Frag3.PanieMode.Vent -> Color(0xFF2196F3) // Blue for Vent
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = when (uiState.panieMode) {
                    ZViewModel_Sec1Frag3.PanieMode.Delivery -> Icons.Default.LocalShipping
                    ZViewModel_Sec1Frag3.PanieMode.Vent -> Icons.Default.Storefront
                },
                contentDescription = "Basculer mode: ${uiState.panieMode.name}",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }

        if (showLabel) {
            Text(
                text = uiState.panieMode.name,
                modifier = Modifier
                    .background(
                        color = when (uiState.panieMode) {
                            ZViewModel_Sec1Frag3.PanieMode.Delivery -> Color(0xFF4CAF50)
                            ZViewModel_Sec1Frag3.PanieMode.Vent -> Color(0xFF2196F3)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun FilterButton(
    uiState: ZViewModel_Sec1Frag3.UiState_Sec1Frag3,
    showLabel: Boolean,
    onToggleFilter: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onToggleFilter,
            containerColor = if (uiState.filterNonTrouve) {
                Color(0xFFFF5722) // Orange when filter is active
            } else {
                MaterialTheme.colorScheme.tertiary
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = if (uiState.filterNonTrouve)
                    "Désactiver filtre Non trouvé"
                else
                    "Activer filtre Non trouvé",
                modifier = Modifier.size(20.dp),
                tint = if (uiState.filterNonTrouve) Color.White else MaterialTheme.colorScheme.onTertiary
            )
        }

        if (showLabel) {
            Text(
                text = if (uiState.filterNonTrouve) "Filtre actif" else "Filtre inactif",
                modifier = Modifier
                    .background(
                        color = if (uiState.filterNonTrouve) {
                            Color(0xFFFF5722)
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (uiState.filterNonTrouve) Color.White else MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PrintButton(
    showLabel: Boolean,
    onPrint: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onPrint,
            containerColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "Imprimer le reçu",
                modifier = Modifier.size(20.dp)
            )
        }

        if (showLabel) {
            Text(
                text = "Imprimer",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MinimizeButton(
    isMinimized: Boolean,
    showLabel: Boolean,
    onToggleMinimized: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = onToggleMinimized,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = if (isMinimized) "Afficher détails" else "Masquer détails",
                modifier = Modifier.size(20.dp)
            )
        }

        if (showLabel) {
            Text(
                text = if (isMinimized) "Afficher" else "Masquer",
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
