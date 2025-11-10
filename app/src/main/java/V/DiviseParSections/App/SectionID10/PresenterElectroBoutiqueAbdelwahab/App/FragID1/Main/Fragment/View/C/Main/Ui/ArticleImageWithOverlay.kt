package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.ImageDisplayerProtoAvantJuin3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
        val hasStock = relative_M3CouleurInfos.count_Don_Depot > 0

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (hasStock) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                },
                contentColor = if (hasStock) {
                    MaterialTheme.colorScheme.onError
                } else {
                    MaterialTheme.colorScheme.onTertiary
                }
            ),
            shape = RoundedCornerShape(if (hasStock) 6.dp else 4.dp)
        ) {
            Text(
                text = if (hasStock) {
                    relative_M3CouleurInfos.count_Don_Depot.toString()
                } else {
                    "احتمال كبير متوفر"
                },
                style = if (hasStock) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.labelSmall
                },
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = if (hasStock) 10.dp else 4.dp,
                    vertical = if (hasStock) 6.dp else 2.dp
                ),
                textAlign = TextAlign.Center,
                fontSize = if (hasStock) 16.sp else 9.sp
            )
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
