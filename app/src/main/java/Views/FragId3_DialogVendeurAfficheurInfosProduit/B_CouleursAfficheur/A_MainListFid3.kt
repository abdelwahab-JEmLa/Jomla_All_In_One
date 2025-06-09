package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.B_CouleurAfficheur
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository.B_ClientsDataBaseProtoD
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Modules.Glide.CalculeCouleurHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun A_MainListFragId3(
    viewModel: HeadViewModel,
    calculeCouleurHandler: CalculeCouleurHandler = koinInject(),
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBaseProtoD?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
    clickedCouleurIndex: Int,
) {
    var colorsListToDisplay by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }

    // Use LazyListState for list control and scrolling
    val listState = rememberLazyListState()

    // Get screen height to calculate appropriate LazyColumn height
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Calculate adaptive height based on content and screen size
    val listHeight = remember {
        minOf(maxOf(screenHeight * 0.8f, 400.dp), 600.dp)
    }

    LaunchedEffect(Unit) {
        // Get product image info and convert to ColorsArticlesTabelle
        val productImageInfos = calculeCouleurHandler.getProduitInfoImageParIndex(stats)

        // Create a list of exactly 4 ColorsArticlesTabelle objects
        colorsListToDisplay = (1..4).mapNotNull { couleurId ->
            val imageInfo = productImageInfos.find { it.couleurId == couleurId }

            if (imageInfo != null && (imageInfo.exists || imageInfo.colorName.isNotBlank())) {
                ColorsArticlesTabelle(
                    nameColore = imageInfo.colorName,
                )
            } else null
        }
    }

    LaunchedEffect(clickedCouleurIndex, colorsListToDisplay) {
        // Wait a short delay to ensure colors list is populated and layout is ready
        delay(100)
        // Only attempt to scroll if we have a valid index and it's within bounds
        if (clickedCouleurIndex >= 0 && clickedCouleurIndex < colorsListToDisplay.size) {
            listState.animateScrollToItem(clickedCouleurIndex)
            // Log for debug purposes
            android.util.Log.d(
                "ColorScroll",
                "Scrolling to index: $clickedCouleurIndex"
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp)

    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Using LazyColumn with adaptive height based on content and screen size
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                        .height(listHeight),  // Using calculated adaptive height
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Log.d("ColoreList", colorsListToDisplay.toString())

                    itemsIndexed(colorsListToDisplay) { index, color ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            B_CouleurAfficheur(
                                modifier = Modifier,
                                currentSale = currentSale,
                                article = stats,
                                color = color,
                                index = index,
                                viewModel = viewModel,
                                height = 350.dp,
                                updateColorToBeMain = { colorId ->
                                    // Implementation for color selection
                                    viewModel.updateColorSelection(colorId, 1)
                                },
                                viewModelInitApp = viewModelInitApp,
                                currentClient = currentClient,
                                colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                                parentCompose_1_2_ProduitAcheteOperationVid = parentCompose_1_2_ProduitAcheteOperationVid,
                                clickedCouleurIndex = clickedCouleurIndex,
                            )
                        }
                    }
                }
            }
        }
    }
}
