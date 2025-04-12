package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
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
            .padding(start = 8.dp, top = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "OpID (${composeKeyVID})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Couleur ID: ${relative_1_1_CouleurAcheteOperation?.couleurIndex_ParentVID ?: "N/A"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
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
        }
    }
}
