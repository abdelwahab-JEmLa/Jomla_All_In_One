package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur.ImageDisplayerProtoAvantJuin3
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import EntreApps.Shared.Models.M3CouleurProduitInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Help
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
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ArticleImageWithOverlay(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel = koinViewModel(),
    viewModelHeadViewModel: HeadViewModel,
    viewModelInitApp: ViewModelInitApp,
    article: M01Produit,
    colorIndex: Int,
    reloadTrigger: Int,
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    qualityImagePourcentage: Int = 100,
    onClickToOpenWindow: (M01Produit, Int) -> Unit,
    alwaysShowExpandIcon: Boolean = false,
    its_secondary_affiche: Boolean = false,
    on_pour_send_data: (String, String) -> Unit,
) {

    val mode_edite_dispo = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt?.mode_edite_dispo

    val relative_M3CouleurInfos = remember(article, colorIndex, reloadTrigger) {
        val directMatch = viewModel.getter.relatedCouleurKeyParAncienMethod(article, colorIndex)

        if (directMatch != null) {
            directMatch
        } else {
            val expectedImageName1 = "${article.id}_${colorIndex + 1}"
            val expectedImageName2 = "${article.id}_${colorIndex}"

            viewModel.getter.repo03CouleurProduitInfos.datasValue.find { couleur ->
                couleur.nomImageFichieSansEtansion == expectedImageName1 ||
                        couleur.nomImageFichieSansEtansion == expectedImageName2 ||
                        (couleur.parentBProduitOldID == article.id &&
                                couleur.indexCouleurDansAncienProto == colorIndex)
            }
        }
    }

    val imageExists = remember(article.id, colorIndex, reloadTrigger, relative_M3CouleurInfos) {
        checkImageExists(
            viewModelHeadViewModel,
            article,
            colorIndex,
            reloadTrigger,
            viewModel.getter.repo03CouleurProduitInfos
        )
    }

    @Composable
    fun ContAuDepot(relative_M3CouleurInfos: M3CouleurProduitInfos) {
        val currentCouleurInfo by remember(relative_M3CouleurInfos.keyID) {
            derivedStateOf {
                viewModel.getter.repo03CouleurProduitInfos.datasValue.find {
                    it.keyID == relative_M3CouleurInfos.keyID
                } ?: relative_M3CouleurInfos
            }
        }

        val depotCount = currentCouleurInfo.count_Don_Depot
        val hasPositiveStock = depotCount > 0
        val hasNegativeStock = depotCount < 0

        val containerColor = when {
            hasPositiveStock -> MaterialTheme.colorScheme.error
            hasNegativeStock -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
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
                        hasNegativeStock -> depotCount.toString()
                        else -> "احتمال كبير متوفر"
                    },
                    style = if (hasPositiveStock || hasNegativeStock) {
                        MaterialTheme.typography.labelLarge
                    } else {
                        MaterialTheme.typography.labelSmall
                    },
                    fontWeight = if (hasNegativeStock) FontWeight.ExtraBold else FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = when {
                        hasNegativeStock -> 17.sp
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
            modifier = Modifier.fillMaxSize()
        ) {
            // FIXED: ImageDisplayerProtoAvantJuin3 now fills parent width properly
            // The fix is in ImageDisplayerProtoAvantJuin3.kt using fillMaxWidth() + aspectRatio()
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
                onClickToOpenWindow = {
                    onClickToOpenWindow(article, colorIndex)
                },
                shouldShowExpandIcon = alwaysShowExpandIcon,
                its_secondary_affiche = its_secondary_affiche
            , on_pour_send_data = on_pour_send_data
            )

            AfficheKeyCouleurAvecVent(viewModel, article, colorIndex)

            relative_M3CouleurInfos?.let { couleurInfo ->
                Box(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    ContAuDepot(couleurInfo)
                }
            }


            if (mode_edite_dispo == true) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    DisponibilityIndicator(
                        disponibilityState = article.disponibilityEtates,
                        onToggle = {
                            aCentralFacade.repositorysMainGetter.repo1ProduitInfos.upsert(
                                article.toggleDisponibilityEtates()
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DisponibilityIndicator(
    disponibilityState: DisponibilityEtates,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (disponibilityState) {
        DisponibilityEtates.DISPO -> Pair(
            Color.Green,
            Icons.Default.Check
        )
        DisponibilityEtates.NON_DISPO -> Pair(
            Color.Red,
            Icons.Default.Close
        )
        DisponibilityEtates.PETITE_PROBABILITY -> Pair(
            Color.Blue,
            Icons.Default.Help
        )
    }

    Box(
        modifier = modifier
            .size(28.dp)
            .background(
                color.copy(alpha = 0.9f),
                CircleShape
            )
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = when (disponibilityState) {
                DisponibilityEtates.DISPO -> "Disponible"
                DisponibilityEtates.NON_DISPO -> "Non Disponible"
                DisponibilityEtates.PETITE_PROBABILITY -> "Peut-être Disponible"
            },
            modifier = Modifier.size(18.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun AfficheKeyCouleurAvecVent(
    viewModel: PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel,
    article: M01Produit,
    colorIndex: Int,
) {
    val couleur = viewModel.getter.relatedCouleurKeyParAncienMethod(article, colorIndex)
    val vent = viewModel.getter.getVentForArticleAndColorInThisApp(article, colorIndex)

    couleur?.let {
        val text = with(couleur) {
            "${keyID.takeLast(4).uppercase()} $nomImageFichieSansEtansion.$extensionDisponible" +
                    " V= ${vent?.parent_M1Produit_DebugInfos ?: "NO"} ${vent?.quantity}"
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.001f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = Color.Red.copy(alpha = 0.001f),
                        shape = RoundedCornerShape(bottomStart = 8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
