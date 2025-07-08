package Views.FragId4_EStorePresentationToClient.Ui

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.View.ImageDisplayerGlid_ProtoAvrile11
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.clientjetpack.R
import org.koin.compose.koinInject

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun B_CouleurAfficheur_F7(
    modifier: Modifier,
    article: ArticlesBasesStatsTable,
    color: ColorsArticlesTabelle?,
    colorIndex: Int,
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

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageDisplayerGlid_ProtoAvrile11(
                produitVID = article.id,
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

            AfficheKeyCouleurAvecVentDebugParAncienMethode(article,colorIndex)

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
fun AfficheKeyCouleurAvecVentDebugParAncienMethode(
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    getter: Get = koinInject(),
) {
    val couleur = getter.relatedCouleurKeyParAncienMethod(article, colorIndex)
    val vent = getter.getVentForArticleAndColorInThisApp(article, colorIndex)

    couleur
        ?.let {
            val text = with(couleur) {
                "${key.takeLast(4).uppercase()} $nomImageFichieSansEtansion.$extensionDisponible" +
                        " V= ${vent?.parentBProduitNomDebug ?: "NO"} ${vent?.quantityAchete}"
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.Companion
                        .background(
                            color = Color.Red,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
}
