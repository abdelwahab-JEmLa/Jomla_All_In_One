package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MainItem_APP2_ID_2(
    modifier: Modifier = Modifier,
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    database: AppDatabase,
) {
    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }

    LaunchedEffect(composeKeyVID) {
        _0_HeadOfRepositorys_Repository_Model._1_2_ProduitAcheteOperation_Repository
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.articlesBasesStatsModelDao().getAll()
            }
    }

    val relative_1_2_ProduitAcheteOperation = _0_HeadOfRepositorys_Repository_Model
        ._1_2_ProduitAcheteOperation_Repository
        .modelDatasSnapList.find { it.vid == composeKeyVID }

    val relative_2_1_ProduitsDataBase = _0_HeadOfRepositorys_Repository_Model._2_1_ProduitsDataBase_Repository
        .modelDatasSnapList.find { it.vid == (relative_1_2_ProduitAcheteOperation
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "OpVId (${composeKeyVID})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "id (${relative_2_1_ProduitsDataBase?.vid ?: "N/A"})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = relative_2_1_ProduitsDataBase?.nom ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            val couleursAcheteOperationsVIDs =
                _0_HeadOfRepositorys_Repository_Model._1_1_CouleurAcheteOperation_Repository
                    .modelDatasSnapList
                    .filter { it.parentProduitAchateOperationVID == composeKeyVID }
                    .map { it.vid }

            // Implement color name lookup function for products with ID < 3000
            fun getColorNameByIndex(colorIndex: Long?, productId: Long?): String? {
                if (colorIndex == null || productId == null || productId >= 3000L) return null

                val article = articlesBasesStatsModel?.find {
                    it.javaClass.getMethod("getIdArticle").invoke(it) == productId.toInt()
                } ?: return null

                return when (colorIndex) {
                    0L -> article.javaClass.getMethod("getCouleur1").invoke(article) as? String
                    1L -> article.javaClass.getMethod("getCouleur2").invoke(article) as? String
                    2L -> article.javaClass.getMethod("getCouleur3").invoke(article) as? String
                    3L -> article.javaClass.getMethod("getCouleur4").invoke(article) as? String
                    else -> "Unknown color"
                }
            }

            // Only render colors section if there are colors to display
            if (couleursAcheteOperationsVIDs.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Colors",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(couleursAcheteOperationsVIDs) { couleurVId ->
                                // Find the color operation to get its index
                                val colorOperation = _0_HeadOfRepositorys_Repository_Model
                                    ._1_1_CouleurAcheteOperation_Repository
                                    .modelDatasSnapList
                                    .find { it.vid == couleurVId }

                                // Get color name for this specific color
                                val colorName = if (relative_2_1_ProduitsDataBase?.vid != null &&
                                    relative_2_1_ProduitsDataBase.vid < 3000L) {
                                    getColorNameByIndex(
                                        colorOperation?.couleurIndex_ParentVID,
                                        relative_2_1_ProduitsDataBase.vid
                                    )
                                } else null

                                ColorDetails_APP2_ID_2(
                                    composeKeyVID = couleurVId,
                                    _0_HeadOfRepositorys_Repository_Model = _0_HeadOfRepositorys_Repository_Model,
                                    relative_2_1_ProduitsDataBase_vid = relative_2_1_ProduitsDataBase?.vid,
                                    colorName = colorName // Pass the color name to the ColorDetails component
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
