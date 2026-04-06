package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.E_ArticleLayout

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.ArticleImageWithOverlay
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex.InfosArticleBottom
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import V.DiviseParSections.App.Shared.ViewModel.HeadViewModel
import V.DiviseParSections.App.Shared.ViewModel.UiState
import org.koin.compose.koinInject

@Composable
fun SmallDisplayerMultiColor(
    article: M01Produit,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    onClickToOpenWindos: (M01Produit, Int) -> Unit,
    uiState: UiState,
    modifier: Modifier = Modifier.Companion,
    imageSize: DpSize,
    lockHost: Boolean,
    viewModelInitApp: ViewModelInitApp,
    expandedColorIndex: Int? = null,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),

    
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos = koinInject()
) {
    val allColorsForProduct = remember(article.keyID, repo03CouleurProduitInfos.datasValue.size, reloadTrigger) {
        repo03CouleurProduitInfos.datasValue
            .filter { it.parentBProduitInfosKeyID == article.keyID }
            .filter { it.nomImageFichieSansEtansion != "Non Dispo" }
            .filter { it.count_Don_Depot > 0 }
            .sortedBy { it.indexCouleurDansAncienProto }
    }

    // Get the indices that have actual color data
    val availableColorIndices = remember(allColorsForProduct) {
        allColorsForProduct.map { it.indexCouleurDansAncienProto }.distinct()
    }

    // DEBUG: Log to see what's happening
    remember(availableColorIndices) {
        android.util.Log.d("SmallDisplayerMultiColor",
            "Article ${article.id}: availableColorIndices = $availableColorIndices")
        availableColorIndices
    }

    // Use expandedColorIndex if provided, otherwise use first available
    val primaryIndex = expandedColorIndex ?: availableColorIndices.firstOrNull() ?: 0

    // Get secondary indices (all colors except the primary)
    val secondaryIndices = availableColorIndices.filter { it != primaryIndex }

    // DEBUG: Log secondary indices
    remember(secondaryIndices) {
        android.util.Log.d("SmallDisplayerMultiColor",
            "Article ${article.id}: primaryIndex = $primaryIndex, secondaryIndices = $secondaryIndices")
        secondaryIndices
    }

    Column(
        modifier = modifier.padding(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Primary image (expanded color or first available)
        ArticleImageWithOverlay(
            article = article,
            viewModelHeadViewModel = viewModel,
            colorIndex = primaryIndex,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindow = onClickToOpenWindos,
            imageSize = imageSize,
            qualityImagePourcentage = 48,
            viewModelInitApp = viewModelInitApp,
            alwaysShowExpandIcon = true
        , 
        )

        // Secondary images (all other colors)
        secondaryIndices.forEach { colorIndex ->
            ArticleImageWithOverlay(
                article = article,
                viewModelHeadViewModel = viewModel,
                colorIndex = colorIndex,
                reloadTrigger = reloadTrigger,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(70.dp),
                contentScale = ContentScale.Companion.Crop,
                onClickToOpenWindow = onClickToOpenWindos,
                imageSize = imageSize,
                viewModelInitApp = viewModelInitApp,
                alwaysShowExpandIcon = true,
                its_secondary_affiche = true
            , 
            )
        }

        InfosArticleBottom(
            article = article,
            modifier = Modifier.Companion.padding(horizontal = 3.dp),
            uiState = uiState,
            lockHost
        )
    }
}
