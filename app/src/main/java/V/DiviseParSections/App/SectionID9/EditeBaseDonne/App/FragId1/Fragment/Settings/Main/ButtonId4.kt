package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileWriter

@Composable
fun ButtonId4(
    AppDatabase: AppDatabase = koinInject(),
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    showLabels: Boolean,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Export Categories & Articles ")
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    exportDataToCsv(context, AppDatabase)
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor = Color.Blue
        ) {
            Icon(Icons.Default.Download, "Export Data to CSV", tint = Color.White)
        }
    }
}

private suspend fun exportDataToCsv(context: Context, appDatabase: AppDatabase) {
    withContext(Dispatchers.IO) {
        try {
            // Create the specific directory path
            val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
            val exportDir = File(imagesProduitsLocalExternalStorageBasePath)

            // Create directory if it doesn't exist
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            // Export Categories
            exportCategoriesToCsv(context, appDatabase, exportDir)

            // Export Articles
            exportArticlesToCsv(context, appDatabase, exportDir)

            // Show success message
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    context,
                    "Categories and Articles exported successfully to: ${exportDir.absolutePath}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            // Log error and show error toast
            e.printStackTrace()
            android.util.Log.e("ButtonId4", "Error exporting data to CSV", e)

            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    context,
                    "Error exporting CSV: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

private suspend fun exportCategoriesToCsv(context: Context, appDatabase: AppDatabase, exportDir: File) {
    // Get all categories from database
    val categories = appDatabase.categoriesModelDao().getAll()

    // Create CSV content
    val csvContent = StringBuilder()

    // Add CSV header
    csvContent.append("id,catalogueParentId,nom,position,displayedHeader,itsHeldPourDeplacement,cSelectionePourDeplace,dernierTimeTampsSynchronisationAvecFireBase\n")

    // Add category data
    categories.forEach { category ->
        csvContent.append("${category.id},")
        csvContent.append("${category.catalogueParentId},")
        csvContent.append("\"${category.nom.replace("\"", "\"\"")}\",") // Escape quotes in CSV
        csvContent.append("${category.position},")
        csvContent.append("${category.displayedHeader},")
        csvContent.append("${category.itsHeldPourDeplacement},")
        csvContent.append("${category.cSelectionePourDeplace},")
        csvContent.append("${category.dernierTimeTampsSynchronisationAvecFireBase}\n")
    }

    // Create file with fixed name
    val fileName = "CategoriesTabelle.csv"
    val file = File(exportDir, fileName)

    // Write CSV content to file
    FileWriter(file).use { writer ->
        writer.write(csvContent.toString())
    }
}
private suspend fun exportArticlesToCsv(context: Context, appDatabase: AppDatabase, exportDir: File) {
    // Get all articles from database
    val articles = appDatabase.articlesBasesStatsModelDao().getAll()

    // Create CSV content
    val csvContent = StringBuilder()

    // Add CSV header - complete list of all fields
    csvContent.append("id,idParentCategorie,nom,nombreUniteInt,nombreProduitDonSonCarton,dernierFireBaseUpdateTimestamps,prixVent,prixAchat,clientPrixVentUnite,actualiseSonImage,actualiseSonImageTest2,disponibilityEtates,keyFireBase,nomArab,autreNomDarticle,couleur1,idcolor1,couleur2,idcolor2,couleur3,idcolor3,couleur4,idcolor4,nomCategorie2,affichageUniteState,commmentSeVent,afficheBoitSiUniter,minQuan,monBenfice,neaon2,catalogeParentID,funChangeImagsDimention,nomCategorie,neaon1,lastUpdateState,cartonState,dateCreationCategorie,prixDeVentTotaleChezClient,benficeTotaleEntreMoiEtClien,benificeTotaleEn2,monPrixAchatUniter,monPrixVentUniter,articleHaveUniteImages,itsNewArrivale,imageDimention,idForSearchArticles\n")

    // Add article data
    articles.forEach { article ->
        csvContent.append("${article.id},")
        csvContent.append("${article.idParentCategorie ?: ""},")
        csvContent.append("\"${article.nom.replace("\"", "\"\"")}\",") // Escape quotes in CSV
        csvContent.append("${article.nombreUniteInt},")
        csvContent.append("${article.nombreProduitDonSonCarton},")
        csvContent.append("${article.dernierFireBaseUpdateTimestamps},")
        csvContent.append("${article.prixVent},")
        csvContent.append("${article.prixAchat},")
        csvContent.append("${article.clientPrixVentUnite},")
        csvContent.append("${article.actualiseSonImage},")
        csvContent.append("${article.actualiseSonImageTest2},")
        csvContent.append("${article.disponibilityEtates},")
        csvContent.append("\"${article.keyFireBase.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.nomArab.replace("\"", "\"\"")}\",")
        csvContent.append("\"${(article.autreNomDarticle ?: "").replace("\"", "\"\"")}\",")

        // Additional fields that were missing
        csvContent.append("\"${(article.couleur1 ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.idcolor1},")
        csvContent.append("\"${(article.couleur2 ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.idcolor2},")
        csvContent.append("\"${(article.couleur3 ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.idcolor3},")
        csvContent.append("\"${(article.couleur4 ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.idcolor4},")
        csvContent.append("\"${(article.nomCategorie2 ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.affichageUniteState},")
        csvContent.append("\"${(article.commmentSeVent ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("\"${(article.afficheBoitSiUniter ?: "").replace("\"", "\"\"")}\",")
        csvContent.append("${article.minQuan},")
        csvContent.append("${article.monBenfice},")
        csvContent.append("\"${article.neaon2.replace("\"", "\"\"")}\",")
        csvContent.append("${article.catalogeParentID},")
        csvContent.append("${article.funChangeImagsDimention},")
        csvContent.append("\"${article.nomCategorie.replace("\"", "\"\"")}\",")
        csvContent.append("${article.neaon1},")
        csvContent.append("\"${article.lastUpdateState.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.cartonState.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.dateCreationCategorie.replace("\"", "\"\"")}\",")
        csvContent.append("${article.prixDeVentTotaleChezClient},")
        csvContent.append("${article.benficeTotaleEntreMoiEtClien},")
        csvContent.append("${article.benificeTotaleEn2},")
        csvContent.append("${article.monPrixAchatUniter},")
        csvContent.append("${article.monPrixVentUniter},")
        csvContent.append("${article.articleHaveUniteImages},")
        csvContent.append("${article.itsNewArrivale},")
        csvContent.append("\"${article.imageDimention.replace("\"", "\"\"")}\",")
        csvContent.append("${article.idForSearchArticles}\n") // Last field, no comma
    }

    // Create file with fixed name
    val fileName = "ArticlesBasesStatsTable.csv"
    val file = File(exportDir, fileName)

    // Write CSV content to file
    FileWriter(file).use { writer ->
        writer.write(csvContent.toString())
    }
}
