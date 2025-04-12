package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ColorDetails_APP2_ID_2(
    composeKeyVID: Long,
    _0_HeadOfRepositorys_Repository_Model: _0_0_HeadOfRepositorys_Model,
    relative_2_1_ProduitsDataBase_vid: Long?,
) {
    val database = koinInject<AppDatabase>()

    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }

    LaunchedEffect(composeKeyVID) {
        _0_HeadOfRepositorys_Repository_Model._1_2_ProduitAcheteOperation_Repository
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.articlesBasesStatsModelDao().getAll()
            }
    }

    val relative_1_1_CouleurAcheteOperation = _0_HeadOfRepositorys_Repository_Model
        ._1_1_CouleurAcheteOperation_Repository
        .modelDatasSnapList
        .find { it.vid == composeKeyVID }

    fun getColorNameByIndex(colorIndex: Long?, productId: Long?): String? {
        if (colorIndex == null || productId == null) return null

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

    // Get color name for this specific color
    val colorName = if (relative_2_1_ProduitsDataBase_vid != null) {
        getColorNameByIndex(
            relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID,
            relative_2_1_ProduitsDataBase_vid
        )
    } else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Added a white background with 30% opacity around the IconButton
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .zIndex(1f)
            )

            IconButton(
                onClick = {
                    // Get the current color item
                    relative_1_1_CouleurAcheteOperation?.let { colorItem ->
                        // Create updated version with the new state
                        val updatedColorItem = colorItem.copy(
                            etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
                        )
                        // Update the item in repository
                        _0_HeadOfRepositorys_Repository_Model
                            ._1_1_CouleurAcheteOperation_Repository
                            .updateUnSeulData(updatedColorItem)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp)
                    .size(24.dp)
                    .zIndex(1f)  // This ensures the button appears above other content
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete color",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // Position the image in the center of the box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                A_GlideDisplayImageByKeyId_Proto_4_11(
                    produitVID = relative_2_1_ProduitsDataBase_vid,
                    couleurVID = relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID?.plus(1),
                    size = 100.dp,
                    onImageNeExistePas = {
                        Text(
                            text = colorName ?: "Color name not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .graphicsLayer(rotationZ = 45f)  // Rotate 45 degrees
                        )
                    }
                )

                relative_1_1_CouleurAcheteOperation?.totaleQuantity?.let { quantity ->
                    if (quantity > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                .zIndex(2f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
