package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.D_AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview @Composable private fun Sec1Frag3Prv() { Sec1Frag3() }

@Composable
fun Sec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val achats = viewModel.d_AchatOperationComposeRepositoryPJ17.filteredDatasValue

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
    // Group achats by parentProduitBsonObjectId
    val groupedAchats = achats.groupBy { it.parentProduitBsonObjectId }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(groupedAchats.entries.toList()) { (productId, achatGroup) ->     //<--
        //TODO(1): pk aucun achat ne s affiche InitDebug         Initial data size: 5
            //                  Item: test_achat_001, parentBonVentObjectId: bon_001
            //                  Item: test_achat_100, parentBonVentObjectId: bon_001
            //                  Item: test_achat_002, parentBonVentObjectId: bon_001
            //                  Item: test_achat_003, parentBonVentObjectId: bon_002
            //                  Item: test_achat_004, parentBonVentObjectId: bon_002
            ProductGroup(
                productId = productId,
                achats = achatGroup
            )
        }
    }
}

@Composable
fun ProductGroup(
    productId: String,
    achats: List<D_AchatOperation>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Product ID: $productId",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${achats.size} item(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal scrollable list of purchase operations
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(achats) { achat ->
                    MainItem(
                        achat = achat,
                        modifier = Modifier.width(200.dp)
                    )
                }
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
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (achat.etateActuellementEst) {
                D_AchatOperation.EtateActuellementEst.CONFIRME -> MaterialTheme.colorScheme.primaryContainer
                D_AchatOperation.EtateActuellementEst.Affiche -> MaterialTheme.colorScheme.surfaceVariant
                D_AchatOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK -> MaterialTheme.colorScheme.errorContainer
                D_AchatOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = achat.nomImageFichieOuApellationDuCouleur,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Qty: ${achat.quantityAchete}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${achat.provisoireMonPrix}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Status indicator
            Surface(
                color = when (achat.etateActuellementEst) {
                    D_AchatOperation.EtateActuellementEst.CONFIRME -> MaterialTheme.colorScheme.primary
                    D_AchatOperation.EtateActuellementEst.Affiche -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.error
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = when (achat.etateActuellementEst) {
                        D_AchatOperation.EtateActuellementEst.CONFIRME -> "Confirmed"
                        D_AchatOperation.EtateActuellementEst.Affiche -> "Display"
                        D_AchatOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK -> "Removed"
                        D_AchatOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE -> "Deleted"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (achat.etateActuellementEst) {
                        D_AchatOperation.EtateActuellementEst.CONFIRME -> MaterialTheme.colorScheme.onPrimary
                        D_AchatOperation.EtateActuellementEst.Affiche -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onError
                    }
                )
            }
        }
    }
}
