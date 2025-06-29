package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Details.UI.B.UI.GBonVentInfosHeader
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        viewModel.uiStateCentralRepositorys.zAppComptRepositoryComposable
    val comptAppActuelle = zAppComptRepositoryComposable.currentAppCompt

    val fVentCouleurOperationRepository =
        viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository

    val ouvertPeriodKeyId = comptAppActuelle?.onVentHVentPeriodKeyId ?: ""

    val cartSummary by remember {
        derivedStateOf {
            val vents = fVentCouleurOperationRepository.onVentFilteredDatas
            CartSummary(
                totalItems = vents.sumOf { it.quantityAchete },
                totalProducts = vents.groupBy { it.parentBProduitInfosKeyId }.size,
                totalValue = vents.sumOf { it.quantityAchete * it.provisoireMonPrix },
                itemsCount = vents.size
            )
        }
    }

    if (comptAppActuelle != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        viewModel = viewModel,
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    CartSummarySection(cartSummary)
                }
            }

            // Action buttons row at top end
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(petitePaddine),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Print button
                FloatingActionButton(
                    onClick = {
                        val fClientRepository = viewModel.uiStateCentralRepositorys.fClientRepository

                        printHandler.printVentReceipt(
                            context = context,
                            fVentCouleurOperationRepository = fVentCouleurOperationRepository,
                            bProduitInfosRepository = viewModel.uiStateCentralRepositorys.bProduitInfosRepository,
                            b1CouleurOuGoutProduitDataBaseRepository = viewModel.uiStateCentralRepositorys.b1CouleurOuGoutProduitDataBaseRepository,
                            client = fClientRepository.onVentClient,
                            scope = scope
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Imprimer le reçu",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Toggle visibility button
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleMinimizedState()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isMinimized) "Afficher détails" else "Masquer détails",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}
