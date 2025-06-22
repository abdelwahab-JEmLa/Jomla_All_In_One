package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.D_AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel

class ViewModel_Sec1Frag3(
    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val d_AchatOperationComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.d_AchatOperationComposeRepositoryPJ17

    data class UiState_Sec1Frag3(
        val v: String = "",
    )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()
}

@Preview
@Composable
private fun Sec1Frag3Prv() {
    Sec1Frag3()
}

@Composable
fun Sec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_Sec1Frag3 = koinViewModel()
) {
    val achats = viewModel.d_AchatOperationComposeRepositoryPJ17.datasValue

    MainList(
        modifier = modifier,
        achats = achats
    )
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    achats: List<D_AchatOperation> = emptyList()
) {
    // Group purchases by parentBonVentObjectId
    val groupedAchats = achats.groupBy { it.parentBonVentObjectId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(groupedAchats.toList()) { (bonVentId, achatsList) ->
            BonVentGroup(
                bonVentId = bonVentId,
                achats = achatsList
            )
        }
    }
}

@Composable
fun BonVentGroup(
    bonVentId: String,
    achats: List<D_AchatOperation>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Group header
            Text(
                text = "Bon de Vente: ${bonVentId.ifEmpty { "Non spécifié" }}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${achats.size} article(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of purchases in this group
            achats.forEach { achat ->
                MainItem(
                    achat = achat,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Group summary
            val totalQuantity = achats.sumOf { it.quantityAchete }
            val totalPrice = achats.sumOf { it.provisoireMonPrix * it.quantityAchete }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total: $totalQuantity articles",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Prix total: ${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MainItem(
    achat: D_AchatOperation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achat.nomImageFichieOuApellationDuCouleur.ifEmpty { "Article sans nom" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Produit ID: ${achat.produitAcheterAncienID}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "État: ${achat.etateActuellementEst.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Qté: ${achat.quantityAchete}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${String.format("%.2f", achat.provisoireMonPrix)} DZD",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
