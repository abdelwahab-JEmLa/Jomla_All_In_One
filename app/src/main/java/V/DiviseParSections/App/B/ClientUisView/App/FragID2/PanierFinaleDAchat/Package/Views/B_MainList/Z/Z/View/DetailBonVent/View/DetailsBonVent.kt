package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
fun DetailsBonVentPrev() {
    DetailsBonVent()
}

@Composable
fun DetailsBonVent(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinInject()
) {
    var isMinimized by remember { mutableStateOf(false) }

    val zAppComptRepositoryComposable =
        viewModel.uiStateCentralRepositorys.zAppComptRepositoryComposable
    val comptAppActuelle = zAppComptRepositoryComposable.ouvertData

    val fVentCouleurOperationRepository =
        viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository

    val ouvertPeriodKeyId = comptAppActuelle?.onVentHPeriodVentKeyId ?: ""

    val cartSummary by remember {
        derivedStateOf {
            val vents = fVentCouleurOperationRepository.onVentFilteredDatas
            CartSummary(
                totalItems = vents.sumOf { it.quantityAchete },
                totalProducts = vents.groupBy { it.parentProduitId }.size,
                totalValue = vents.sumOf { it.quantityAchete * it.provisoireMonPrix },
                itemsCount = vents.size
            )
        }
    }

    if (comptAppActuelle != null) {
        // Use Box instead of Scaffold to avoid layout conflicts
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight() // This ensures it only takes the space it needs
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isMinimized) {
                        TransactionVentInfosHeader(viewModel)
                    }
                    if (!isMinimized) {
                        PeriodDetailsSection(
                            viewModel = viewModel,
                            ouvertPeriodKeyId = ouvertPeriodKeyId,
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Client details - Always visible
                    ClientDetailsSection(
                        viewModel = viewModel,
                    )
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    CartSummarySection(cartSummary)
                }
            }

            // FloatingActionButton positioned at bottom-end
            FloatingActionButton(
                onClick = { isMinimized = !isMinimized },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isMinimized) "Afficher détails" else "Masquer détails"
                )
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}

@Composable
fun ClientDetailsSection(
    viewModel: ZViewModel_Sec1Frag3,
) {
    val fClientRepository = viewModel.uiStateCentralRepositorys.fClientRepository
    val onVentClient = fClientRepository.onVentClient

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Client",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Client On Vent",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (onVentClient != null) {
                with(onVentClient) {
                    Text(
                        text = "Nom: $nom", // FIXED: This should now display correctly
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "keyID: $keyID",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Téléphone: $numTelephone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "Non Définie Pour Le Moment",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
