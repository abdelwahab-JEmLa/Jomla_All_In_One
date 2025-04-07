package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.B_CouleurAfficheur
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.os.Build
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
import org.koin.compose.koinInject

@Composable
fun A_MainListFragId3(
    viewModel: HeadViewModel,
    currentSale: SoldArticlesTabelle,
    stats: ArticlesBasesStatsTable,
    colorsArticlesTabelleModel: List<ColorsArticlesTabelle>,
    reloadTrigger: Int,
    viewModelInitApp: ViewModelInitApp,
    currentClient: B_ClientsDataBase?,
    colorsArticlesTabelleModele: List<ColorsArticlesTabelle>,
) {
    var colorsListToDisplay by remember { mutableStateOf(emptyList<ColorsArticlesTabelle>()) }

    // LaunchedEffect to handle all suspend functions
    LaunchedEffect(key1 = Unit) {
        // Process color list
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
                val estimatedHeight = minOf(
                    (400.dp + 2.dp) * colorsListToDisplay.size,
                    3500.dp
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
                                .height(350.dp)
                                .clip(MaterialTheme.shapes.medium)
                        ) {
                            B_CouleurAfficheur(
                                composMainKeyModel = _1_1_CouleurAcheteOperation(
                                    vendeur_ParentVID = koinInject<_1_5_Vendeur_Repository>()
                                        .getIdParNomModel(Build.MODEL),
                                    periodeVentDateInString_ParentVID = koinInject<_1_4_PeriodeVent_Repository>()
                                        .getByMainVAl(),
                                    produitId_ParentVID = currentSale.idArticle,
                                    couleurId_ParentVID = color.idColore
                                ),
                                modifier = Modifier,
                                currentSale = currentSale,
                                article = stats,
                                color = color,
                                index = index,
                                reloadTrigger = reloadTrigger,
                                viewModel = viewModel,
                                height = 350.dp,
                                updateColorToBeMain = { /* Empty lambda since we removed ranking logic */ },
                                viewModelInitApp = viewModelInitApp,
                                currentClient = currentClient,
                                colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                            )
                        }
                    }
                }
            }
        }
    }
}
