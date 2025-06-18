package Views.FragId4_EStorePresentationToClient.Ui

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorArrangement
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun B_CouleurAfficheur_F7(
    modifier: Modifier,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    colorIndex: Int,
    relodeTigger: Int,
    colorArrangement: ColorArrangement? = null, // Add this parameter
    sizeScreen: Dp,
    viewModelInitApp: ViewModelInitApp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val articleproduitsMainDataBase =
        viewModelInitApp._modelAppsFather.produitsMainDataBase.find { it.id == article.id }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            A_GlideDisplayImageByKeyId_Proto_4_11(
                produitVID = article.id.toLong(),
                couleurVID = colorIndex.toLong() + 1,
                size = 600.dp,
                qualityImage = 100,
                onImageNeExistePas = {
                    Text(
                        text = color?.nameColore!!,
                        fontSize = 55.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp)
                            .graphicsLayer(rotationZ = 45f)
                    )
                }
            )


            // Add QuantityBadge if there's a quantity to display
            colorArrangement?.let { arrangement ->
                if (arrangement.colorSoldQuantity > 0) {
                    QuantityBadge(
                        quantity = arrangement.colorSoldQuantity,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }

            // Gradient Overlay and Content (remains the same)
            color?.let { colorData ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    if (colorIndex > 0) {
                        // Centered Row for index > 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Color Name
                            Text(
                                text = colorData.nameColore,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            // Icon Container
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .size(60.dp)
                                    .alpha(iconAlpha)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when {
                                        colorData.iconColore in listOf("©", "💯", "") -> {
                                            GlideImage(
                                                model = R.drawable.logo,
                                                contentDescription = "Brand Logo",
                                                modifier = Modifier.size(60.dp)
                                            )
                                        }

                                        else -> {
                                            Text(
                                                text = colorData.iconColore,
                                                fontSize = 35.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Original layout for index 0
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()

                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = colorData.nameColore,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    modifier = Modifier
                                        .size(60.dp)
                                        .alpha(iconAlpha)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when {
                                            colorData.iconColore in listOf("©", "💯", "") -> {
                                                GlideImage(
                                                    model = R.drawable.logo,
                                                    contentDescription = "Brand Logo",
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }

                                            else -> {
                                                Text(
                                                    text = colorData.iconColore,
                                                    fontSize = 35.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun QuantityBadge(
    quantity: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    ) {
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}
