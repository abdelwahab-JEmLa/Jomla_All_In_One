package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.UiState_APP2_ID_2
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainItem_APP2_ID_2(
    modifier: Modifier = Modifier,
    uiState: UiState_APP2_ID_2,
    idproduit: Long,
    opetaionsAcceptedListVID: List<Long>,
) {
    Card(
        modifier = modifier
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Product info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "idproduit ($idproduit)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
            // Group products by product ID
            val groupedCouleurs = uiState._1_1_CouleurAcheteOperationList
                .filter { couleur ->
                    opetaionsAcceptedListVID.any { it == couleur.parent_1_2_ProduitAcheteOperationID }
                }
                .groupBy { it.couleurId }

            // Only render colors section if there are colors to display
            if (groupedCouleurs.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    groupedCouleurs.forEach{(couleurtId, couleurstList) ->
                        ColorDetails_APP2_ID_2(
                            couleurtId=couleurtId,
                            totaleQuantityDesCouleurs=couleurstList.sumOf { it.totaleQuantity },
                            clientsAchteurDeCeProduit = uiState._1_3_BonAchatList
                                .filter { bonAchat ->
                                    opetaionsAcceptedListVID.any { vidOp ->
                                        uiState._1_1_CouleurAcheteOperationList
                                            .filter { couleur -> couleur.couleurId == couleurtId }
                                            .any { couleur -> couleur.parent_1_2_ProduitAcheteOperationID == vidOp }
                                    } &&
                                            uiState._1_2_ProduitAcheteOperationList
                                                .filter { it.vid in opetaionsAcceptedListVID }
                                                .any { it.parent_1_3_BonAchat == bonAchat.vid }
                                }
                                .map { it.clientAchteurID }
                                .distinct(),
                        )
                    }
                }
            }
        }
    }
}
