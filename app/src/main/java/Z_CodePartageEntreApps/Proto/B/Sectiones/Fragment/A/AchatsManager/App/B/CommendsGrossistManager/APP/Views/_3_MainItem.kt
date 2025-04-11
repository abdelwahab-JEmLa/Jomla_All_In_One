package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainItem_APP2_ID_2(
    modifier: Modifier = Modifier,
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
) {
    val relativeOperationAchatProduit = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList.find { it.vid == composeKeyVID }

    val relativeProduit = _0_HeadOfRepositorys_Repository_Model._2_1_ProduitsDataBase_Repository
        .modelDatasSnapList.find { it.vid == (relativeOperationAchatProduit
            ?.produitAcheterID ?: 0) }

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
            Card (
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Adding a Row to properly lay out the Text components with weight
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "idOperationProduit (${composeKeyVID})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "id (${relativeProduit?.vid ?: "N/A"})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "nom (${relativeProduit?.nom ?: "N/A"})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            // Group products by product ID
            val couleursAcheteOperationsVIDs =
                _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
                    .modelDatasSnapList
                    .filter { it.parentProduitAchateOperationVID == composeKeyVID }
                    .map { it.vid }

            // Only render colors section if there are colors to display
            if (couleursAcheteOperationsVIDs.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    couleursAcheteOperationsVIDs.forEach { couleurVId ->
                        ColorDetails_APP2_ID_2(
                            composeKeyVID = couleurVId,
                        )
                    }
                }
            }
        }
    }
}
