package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Couleurs(
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    Produit: _1_2_ProduitAcheteOperation,
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
) {
    val database = koinInject<AppDatabase>()

    var articlesBasesStatsModel by remember { mutableStateOf<List<Any>?>(null) }

    LaunchedEffect(Produit.vid) {
        models._1_2_ProduitAcheteOperation_Repository
            .repositoryScope
            .launch {
                articlesBasesStatsModel = database.articlesBasesStatsModelDao().getAll()
            }
    }

    fun getColorNameByIndex(colorIndex: Long?, productId: Long?): String? {
        if (colorIndex == null || productId == null) return null

        val article = articlesBasesStatsModel?.find {
            try {
                it.javaClass.getMethod("getIdArticle").invoke(it) == productId.toInt()
            } catch (e: Exception) {
                false
            }
        } ?: return null

        return try {
            when (colorIndex) {
                0L -> article.javaClass.getMethod("getCouleur1").invoke(article) as? String
                1L -> article.javaClass.getMethod("getCouleur2").invoke(article) as? String
                2L -> article.javaClass.getMethod("getCouleur3").invoke(article) as? String
                3L -> article.javaClass.getMethod("getCouleur4").invoke(article) as? String
                else -> "Unknown color"
            }
        } catch (e: Exception) {
            "Color name not available"
        }
    }

    LazyRow {
        items(
            colorsForProduct
                .filter { it.totaleQuantity > 0 }
                .distinctBy { it.couleurIndex_ParentVID }
        ) { Couleur ->
            VerticalDivider(
                thickness = 9.dp,
                color = Color.Red
            )

            val totaleQuantity = colorsForProduct
                .filter { it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID }
                .sumOf { it.totaleQuantity }

            // Get color name for this specific color
            val colorName = getColorNameByIndex(
                Couleur.couleurIndex_ParentVID,
                Produit.produitAcheterID
            )

            Card(
                modifier = Modifier.background(Color.Red)
            ) {
                Column {
                    Box {
                        A_GlideDisplayImageByKeyId_Proto_4_11(
                            Produit.produitAcheterID,
                            Couleur.couleurIndex_ParentVID + 1,
                            360.dp,
                            onImageNeExistePas = {
                                Text(
                                    text = colorName ?: "Color name not available",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp)
                                        .graphicsLayer(rotationZ = 45f)  // Rotate 45 degrees
                                )
                            }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.70f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                        ) {
                            Text(
                                text = "Qua>$totaleQuantity",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }

                    // Display buyers for this color
                    Acheteurs(buyerIds, models, colorsForProduct, Couleur)
                }
            }
        }
    }
}
