package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.ImageDisplayerProtoAvantJuin3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArticleImageWithOverlay(
    modifier: Modifier = Modifier,
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel = koinViewModel(),
    viewModelHeadViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    reloadTrigger: Int,
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    qualityImagePourcentage: Int = 100,
    onClickToOpenWindow: (ArticlesBasesStatsTable, Int) -> Unit
) {
    val id = article.id
    val imageExists = remember(id, colorIndex, reloadTrigger) {
        checkImageExists(viewModelHeadViewModel, article, colorIndex, reloadTrigger)
    }

    // FIXED: Get the related color info with fallback search by image filename pattern
    val relative_M3CouleurInfos = remember(article, colorIndex) {
        // First try the standard method
        val directMatch = viewModel.getter.relatedCouleurKeyParAncienMethod(article, colorIndex)

        if (directMatch != null) {
            directMatch
        } else {
            // If null, search by matching nomImageFichieSansEtansion pattern
            // Pattern: {productId}_{colorIndex+1} (e.g., "3959_1", "3959_2")
            val expectedImageName = "${article.id}_${colorIndex + 1}"

            viewModel.getter.repo03CouleurProduitInfos.datasValue.find { couleur ->
                couleur.nomImageFichieSansEtansion == expectedImageName ||
                        // Also check if the parent product matches
                        (couleur.parentBProduitOldID == article.id &&
                                couleur.indexCouleurDansAncienProto == colorIndex)
            }
        }
    }

    @Composable
    fun ContAuDepot(relative_M3CouleurInfos: M3CouleurProduitInfos) {
        // FIXED: Use remember with derivedStateOf to trigger recomposition when data changes
        val currentCouleurInfo by remember(relative_M3CouleurInfos.keyID) {
            derivedStateOf {
                // Always get the latest data from repository
                viewModel.getter.repo03CouleurProduitInfos.datasValue.find {
                    it.keyID == relative_M3CouleurInfos.keyID
                } ?: relative_M3CouleurInfos
            }
        }

        // This will trigger recomposition when count_Don_Depot changes
        val depotCount = currentCouleurInfo.count_Don_Depot
        val hasPositiveStock = depotCount > 0
        val hasNegativeStock = depotCount < 0
        val isZero = depotCount == 0

        // Determine colors and styles based on stock status
        val containerColor = when {
            hasPositiveStock -> MaterialTheme.colorScheme.error // Positive stock - red
            hasNegativeStock -> MaterialTheme.colorScheme.errorContainer // Negative stock - darker red
            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f) // Zero - tertiary
        }

        val contentColor = when {
            hasPositiveStock -> MaterialTheme.colorScheme.onError
            hasNegativeStock -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onTertiary
        }


        Card(
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            shape = RoundedCornerShape(if (hasPositiveStock || hasNegativeStock) 6.dp else 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (hasPositiveStock || hasNegativeStock) 10.dp else 4.dp,
                    vertical = if (hasPositiveStock || hasNegativeStock) 6.dp else 2.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show icon for negative stock
                if (hasNegativeStock) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = "Déficit",
                        modifier = Modifier.padding(end = 4.dp),
                        tint = contentColor
                    )
                }

                Text(
                    text = when {
                        hasPositiveStock -> depotCount.toString()
                        hasNegativeStock -> depotCount.toString() // Shows "-4", "-10", etc.
                        else -> "0"
                    },
                    style = if (hasPositiveStock || hasNegativeStock) {
                        MaterialTheme.typography.labelLarge
                    } else {
                        MaterialTheme.typography.labelSmall
                    },
                    fontWeight = if (hasNegativeStock) FontWeight.ExtraBold else FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = when {
                        hasNegativeStock -> 17.sp // Slightly larger for emphasis
                        hasPositiveStock -> 16.sp
                        else -> 9.sp
                    }
                )
            }
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ImageDisplayerProtoAvantJuin3(
                relative_M1Produit = article,
                viewModel = viewModelHeadViewModel,
                indexColor = colorIndex,
                reloadKey = reloadTrigger,
                showOverlay = !imageExists,
                imageScale = contentScale,
                imageSize = imageSize,
                finalequalityImagePourcentage = qualityImagePourcentage,
                viewModelInitApp = viewModelInitApp,
            )

            AfficheKeyCouleurAvecVent(viewModel, article, colorIndex)

            // Display depot count overlay - now always visible
            relative_M3CouleurInfos?.let { couleurInfo ->
                Box(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    ContAuDepot(couleurInfo)
                }
            }
        }
    }
}

@Composable
private fun AfficheKeyCouleurAvecVent(
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
) {
    val couleur = viewModel.getter.relatedCouleurKeyParAncienMethod(article, colorIndex)
    val vent = viewModel.getter.getVentForArticleAndColorInThisApp(article, colorIndex)

    couleur
        ?.let {
            val text = with(couleur) {
                "${keyID.takeLast(4).uppercase()} $nomImageFichieSansEtansion.$extensionDisponible" +
                        " V= ${vent?.parent_M1Produit_DebugInfos ?: "NO"} ${vent?.quantity}"
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = text,
                    color = Color.White.copy(alpha = 0.001f), // Very low alpha for text
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(
                            color = Color.Red.copy(alpha = 0.001f), // Very low alpha for background
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
}
