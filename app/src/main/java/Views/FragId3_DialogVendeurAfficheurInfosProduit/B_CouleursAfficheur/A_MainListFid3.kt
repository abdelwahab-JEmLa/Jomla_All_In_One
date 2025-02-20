package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.B_CouleurAfficheur
import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
fun A_MainListFragId3(
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    viewModel: HeadViewModel,
    reloadTrigger: Int,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBase?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
) {
    var colorsListToDisplay by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }

    // Initialize colors list on first composition
    LaunchedEffect(Unit) {
        // Get all valid colors
        colorsListToDisplay = listOf(
            stats.idcolor1,
            stats.idcolor2,
            stats.idcolor3,
            stats.idcolor4
        ).mapNotNull { colorId ->
            if (colorId != 0L) {
                colorsArticlesTabelleModel.find { it.idColore == colorId }
            } else null
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
                // Calculate a reasonable fixed height based on your content
                // Height = (single item height + spacing) * number of items, capped at max height
                val estimatedHeight = minOf(
                    (150.dp + 2.dp) * colorsListToDisplay.size,
                    500.dp // Max height cap
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(estimatedHeight)
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    colorsListToDisplay.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            B_CouleurAfficheur(
                                modifier = Modifier,
                                currentSale = currentSale,
                                article = stats,
                                color = color,
                                index = index,
                                reloadTrigger = reloadTrigger,
                                viewModel = viewModel,
                                height = 150.dp,
                                updateColorToBeMain = { /* Empty lambda since we removed ranking logic */ },
                                viewModelInitApp = viewModelInitApp,
                                currentClient = currentClient,
                                colorsArticlesTabelleModele = colorsArticlesTabelleModele
                            )
                        }
                    }
                }
            }
        }
    }
}
