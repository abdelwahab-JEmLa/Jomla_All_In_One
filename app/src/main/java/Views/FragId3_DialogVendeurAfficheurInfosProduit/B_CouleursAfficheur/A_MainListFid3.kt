package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur

import Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem.B_CouleurAfficheur
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Model._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    var parentCompose_1_5_VendeurId by remember { mutableLongStateOf(0) }
    var parentCompose_1_4_PeriodeVentVid by remember { mutableLongStateOf(0) }
    var parentCompose_1_3_BonAchatVid by remember { mutableLongStateOf(0) }
    var parentCompose_1_2_ProduitAcheteOperationVid by remember { mutableLongStateOf(0) }

    val deviceModelNom = Build.MODEL
    val currentClientId = currentClient?.id ?: 1

    val vendeurRepo = koinInject<_1_5_Vendeur_Repository>()

    val _1_4_PeriodeVent_Repository = koinInject<_1_4_PeriodeVent_Repository>()

    val _1_3_BonAchat_Repository = koinInject<_1_3_BonAchat_Repository>()
    val currenteDateInString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val _1_2_ProduitAcheteOperation_Repository = koinInject<_1_2_ProduitAcheteOperation_Repository>()
    val produitActuelle = currentSale.idArticle

    LaunchedEffect(Unit) {
        // Get or create vendor
        val existingVendor = vendeurRepo
            .modelDatasSnapList.find { it.deviceModelNom == deviceModelNom }
        parentCompose_1_5_VendeurId = if (existingVendor != null) {
            existingVendor.vid
        } else {
            val newVid = vendeurRepo.modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            vendeurRepo.addData(_1_5_Vendeur(
                vid = newVid,
                deviceModelNom = deviceModelNom
            ))
            newVid
        }

        val existing_1_4_PeriodeVent = _1_4_PeriodeVent_Repository.modelDatasSnapList
            .find {
            it.endDateInString == ""
        }
        parentCompose_1_4_PeriodeVentVid = if (existing_1_4_PeriodeVent != null) {
            existing_1_4_PeriodeVent.vid
        } else {
            val newVid = _1_4_PeriodeVent_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            _1_4_PeriodeVent_Repository.addData(
                _1_4_PeriodeVent(
                    vid = newVid,
                    startDateInString = currenteDateInString,
                    vendeur_ParentVID=parentCompose_1_5_VendeurId
                )
            )
            newVid
        }

        val existing_1_3_BonAchat = _1_3_BonAchat_Repository.modelDatasSnapList.find {
            it.clientAcheteurID == currentClientId
                    && it.parent_1_4_PeriodeVentVid == parentCompose_1_4_PeriodeVentVid
        }
        parentCompose_1_3_BonAchatVid = if (existing_1_3_BonAchat != null) {
            existing_1_3_BonAchat.vid
        } else {
            val newVid = _1_3_BonAchat_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            _1_3_BonAchat_Repository.addData(
                _1_3_BonAchat(
                    vid = newVid,
                    clientAcheteurID = currentClientId ,
                    parent_1_4_PeriodeVentVid=parentCompose_1_4_PeriodeVentVid
                )
            )
            newVid
        }

        val existing_1_2_ProduitAcheteOperation = _1_2_ProduitAcheteOperation_Repository.modelDatasSnapList.find {
            it.produitAcheterID == produitActuelle
                    && it.parent_1_3_BonAchat == parentCompose_1_3_BonAchatVid
        }
        parentCompose_1_2_ProduitAcheteOperationVid = if (existing_1_2_ProduitAcheteOperation != null) {
            existing_1_2_ProduitAcheteOperation.vid
        } else {
            val newVid = _1_2_ProduitAcheteOperation_Repository
                .modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
            _1_2_ProduitAcheteOperation_Repository.add(
                _1_2_ProduitAcheteOperation(
                    vid = newVid,
                    produitAcheterID = produitActuelle,
                    parent_1_3_BonAchat = parentCompose_1_3_BonAchatVid
                )
            )
            newVid
        }

        // Process color list
        colorsListToDisplay = listOf(stats.idcolor1, stats.idcolor2, stats.idcolor3, stats.idcolor4)
            .filter { it != 0L }
            .mapNotNull { colorId -> colorsArticlesTabelleModel.find { it.idColore == colorId } }
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
                val estimatedHeight = minOf((400.dp + 2.dp) * colorsListToDisplay.size, 3500.dp)

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
                                modifier = Modifier,
                                currentSale = currentSale,
                                article = stats,
                                color = color,
                                index = index,
                                reloadTrigger = reloadTrigger,
                                viewModel = viewModel,
                                height = 350.dp,
                                updateColorToBeMain = { },
                                viewModelInitApp = viewModelInitApp,
                                currentClient = currentClient,
                                colorsArticlesTabelleModele = colorsArticlesTabelleModele,
                                parentCompose_1_2_ProduitAcheteOperationVid=parentCompose_1_2_ProduitAcheteOperationVid,
                            )
                        }
                    }
                }
            }
        }
    }
}
