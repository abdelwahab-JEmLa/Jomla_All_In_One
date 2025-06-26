package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.ImageDisplayerProtoAvantJuin3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.checkImageExists
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    /*
    val imageKeyID = buildString {
        append(id)
        append("_")
        append(colorIndex)
    }     */

    val relatedCouleurKey =
        viewModel.aCentralDatasHandlerProtoJuin9.b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find {
                it.parentBProduitOldID == id
                        && it.indexCouleurDansAncienProto == colorIndex
            }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        ImageDisplayerProtoAvantJuin3(
            viewModel = viewModelHeadViewModel,
            article = article,
            indexColor = colorIndex,
            reloadKey = reloadTrigger,
            showOverlay = !imageExists,
            imageScale = contentScale,
            imageSize = imageSize,
            finalequalityImagePourcentage = qualityImagePourcentage,
            viewModelInitApp = viewModelInitApp,
        ) {
            onClickToOpenWindow(article, colorIndex)
            viewModel.acheter(
                produit = article,
                colorIndex = colorIndex,
                quantity = 1
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (relatedCouleurKey != null) {

                val text = buildString {
                    append(relatedCouleurKey.key.takeLast(4).uppercase())
                    append(" ")
                    append(relatedCouleurKey.nomImageFichieSansEtansion)
                    append(".")
                    append(relatedCouleurKey.extensionDisponible)

                }

                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier

                        .align(Alignment.TopEnd)
                        .background(
                            color = Color.Red,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
