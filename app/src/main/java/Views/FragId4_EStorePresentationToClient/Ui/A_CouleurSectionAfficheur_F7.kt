package Views.FragId4_EStorePresentationToClient.Ui

import Z_CodePartageEntreApps.Model.E_AppsOptionsStates.ApplicationEstInstalleDonTelephone.Companion.metricsWidthPixels
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import Z_CodePartageEntreApps.Model.Z.Archive.ProductDisplayController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun A_CouleurSectionAfficheur_F7(
    displayController: ProductDisplayController,
    articlesBasesStatsTable: ArticlesBasesStatsTable,
    modifier: Modifier = Modifier,
    relodeTigger: Int,
    colorsArticlesList: List<ColorsArticlesTabelle>,
    viewModelInitApp: ViewModelInitApp,
) {
    var colorsListToDisplaye by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }
    val colorArrangements = remember(displayController.newArregmentColorsJsonStruct) {
        displayController.getColorArrangement()
    }
    val TAG = "ColorsCards7Debug"
    val itsTablette = metricsWidthPixels > 400
    val sizeScreen = if(metricsWidthPixels > 400) 300.dp else 170.dp

    LaunchedEffect(displayController.newArregmentColorsJsonStruct) {
        colorsListToDisplaye = try {
            val arrangement = displayController.getColorArrangement()
            if (arrangement.isEmpty()) {
                listOf(
                    articlesBasesStatsTable.idcolor1,
                    articlesBasesStatsTable.idcolor2,
                    articlesBasesStatsTable.idcolor3,
                    articlesBasesStatsTable.idcolor4
                ).mapNotNull { colorId ->
                    if (colorId != 0L) {
                        colorsArticlesList.find { it.idColore == colorId }
                    } else null
                }
            } else {
                arrangement.mapNotNull { arrangedColor ->
                    colorsArticlesList.find { it.idColore == arrangedColor.idColore }
                }
            }
        } catch (e: Exception) {
            listOf(
                articlesBasesStatsTable.idcolor1,
                articlesBasesStatsTable.idcolor2,
                articlesBasesStatsTable.idcolor3,
                articlesBasesStatsTable.idcolor4
            ).mapNotNull { colorId ->
                if (colorId != 0L) {
                    colorsArticlesList.find { it.idColore == colorId }
                } else null
            }
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(displayController.clientWindowsLazyRowSupColorsScroll) {
        if (!displayController.isHostPhone && colorsListToDisplaye.size > 1) {
            scope.launch {
                try {
                    val targetIndex = displayController.clientWindowsLazyRowSupColorsScroll.coerceIn(
                        0,
                        (colorsListToDisplaye.size - 2).coerceAtLeast(0)
                    )
                    listState.animateScrollToItem(index = targetIndex)
                    delay(300)
                } catch (e: Exception) {
                    // Silent catch - no logging needed
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (itsTablette) (colorsListToDisplaye.size * 700).dp else (colorsListToDisplaye.size*350).dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(colorsListToDisplaye) { color ->
                        Log.d(TAG, "Rendering color: ${color.idColore}")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (itsTablette) (700).dp else (350).dp)
                        ) {
                            B_CouleurAfficheur_F7(
                                modifier = Modifier.fillMaxSize(),
                                article = articlesBasesStatsTable,
                                color = color,
                                colorIndex = colorsListToDisplaye.indexOf(color),
                                viewModelInitApp = viewModelInitApp
                            )
                        }
                    }
                }
            }
        }
    }
}
