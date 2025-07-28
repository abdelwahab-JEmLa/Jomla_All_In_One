package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    var clickCount by remember { mutableStateOf(0) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels)
            Text(
                when (clickCount) {
                    0 -> "From Locale To Csv"
                    else -> "T Sure"
                }
            )
        FloatingActionButton(
            onClick = {
                when (clickCount) {
                    0 -> clickCount++
                    1 -> coroutineScope.launch {
                        exportAllDataToCsv(context, AppDatabase)
                        clickCount=0
                    }
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor = Color.Blue
        ) {
            Icon(Icons.Default.Upload, "Export All Data to CSV", tint = Color.White)
        }
    }
}

private suspend fun exportAllDataToCsv(context: Context, appDatabase: AppDatabase) {
    withContext(Dispatchers.IO) {
        try {
            // Create the specific directory path
            val imagesProduitsLocalExternalStorageBasePath =
                "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
            val exportDir = File(imagesProduitsLocalExternalStorageBasePath)

            // Create directory if it doesn't exist
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            // Export Categories
            exportCategoriesToCsv(context, appDatabase, exportDir)

            // Export Articles
            exportArticlesToCsv(context, appDatabase, exportDir)

            // Export Clients
            exportClientsToCsv(context, appDatabase, exportDir)

            // Show success message
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    context,
                    "Categories, Articles, and Clients exported successfully to: ${exportDir.absolutePath}",
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

private suspend fun exportCategoriesToCsv(
    context: Context,
    appDatabase: AppDatabase,
    exportDir: File
) {
    // Get all categories from database
    val categories = appDatabase.Dao16CategorieProduit().getAll()

    // Create CSV content
    val csvContent = StringBuilder()

    // Add CSV header - aligned with CategoriesTabelle schema
    csvContent.append("id,bsonObjectId,dernierTimeTampsSynchronisationAvecFireBase,catalogueParentId,parentCatalogueIdObject,nom,position,displayedHeader,itsHeldPourDeplacement,cSelectionePourDeplace\n")

    // Add category data
    categories.forEach { category ->
        csvContent.append("${category.id},")
        csvContent.append("\"${category.bsonObjectId?.replace("\"", "\"\"") ?: ""}\",")
        csvContent.append("${category.dernierTimeTampsSynchronisationAvecFireBase},")
        csvContent.append("${category.catalogueParentId},")
        csvContent.append(
            "\"${
                category.parentCatalogueIdObject.replace(
                    "\"",
                    "\"\""
                ) ?: ""
            }\","
        )
        csvContent.append("\"${category.nom.replace("\"", "\"\"")}\",")
        csvContent.append("${category.position},")
        csvContent.append("${category.displayedHeader},")
        csvContent.append("${category.itsHeldPourDeplacement},")
        csvContent.append("${category.cSelectionePourDeplace}\n")
    }

    // Create file with fixed name
    val fileName = "CategoriesTabelle.csv"
    val file = File(exportDir, fileName)

    // Write CSV content to file
    withContext(Dispatchers.IO) {
        FileWriter(file).use { writer ->
            writer.write(csvContent.toString())
        }
    }
}

private suspend fun exportArticlesToCsv(
    context: Context,
    appDatabase: AppDatabase,
    exportDir: File
) {
    // Get all articles from database
    val articles = appDatabase.ArticlesBasesStatsModelDao().getAll()

    // Create CSV content
    val csvContent = StringBuilder()

    // Add CSV header - aligned with complete ArticlesBasesStatsTable schema
    csvContent.append(
        "id,keyID,bsonObjectId,dernierTimeTampsSynchronisationAvecFireBase,dernierFireBaseUpdateTimestamps,processPositioningInFactory,idParentCategorie,positionDonSonCesFrereCategorieProduits,nom,nomMutable,etateActuelleOnFusionAvecBaseDonne,nombreUniteInt,nombreProduitDonSonCarton,heldPrioriteDemandAuGrossist,prixDefiniParGerant,prixVent,cachePrixVent,prixAchat,prixAchatDernierTimeTempUpdate,clientPrixVentUnite,actualiseSonImage,actualiseSonImageTest2,afficheCesDetailPourComptBsonId,disponibilityEtates,keyFireBase,nomArab,autreNomDarticle,couleur1,idcolor1,couleur2,idcolor2,couleur3,idcolor3,couleur4,idcolor4,nomCategorie2,affichageUniteState,commmentSeVent,afficheBoitSiUniter,minQuan,monBenfice,neaon2,catalogeParentID,funChangeImagsDimention,nomCategorie,neaon1,lastUpdateState,cartonState,dateCreationCategorie,prixDeVentTotaleChezClient,benficeTotaleEntreMoiEtClien,benificeTotaleEn2,monPrixAchatUniter,monPrixVentUniter,articleHaveUniteImages,itsNewArrivale,imageDimention,idForSearchArticles,setIN_Vent_Its_Quantity_Represent,quantite_Boit_Par_Carton\n"
    )

    // Add article data
    articles.forEach { article ->
        csvContent.append("${article.id},")
        csvContent.append("\"${article.keyID.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.bsonObjectId.replace("\"", "\"\"")}\",")
        csvContent.append("${article.dernierTimeTampsSynchronisationAvecFireBase},")
        csvContent.append("${article.dernierFireBaseUpdateTimestamps},")
        csvContent.append("\"${article.processPositioningInFactory.name}\",")
        csvContent.append("${article.idParentCategorie ?: ""},")
        csvContent.append("${article.positionDonSonCesFrereCategorieProduits},")
        csvContent.append("\"${article.nom.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.nomMutable.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.etateActuelleOnFusionAvecBaseDonne.name}\",")
        csvContent.append("${article.nombreUniteInt},")
        csvContent.append("${article.nombreProduitDonSonCarton},")
        csvContent.append("${article.heldPrioriteDemandAuGrossist},")
        csvContent.append("${article.prixDefiniParGerant ?: 0.0},")
        csvContent.append("${article.prixVent},")
        csvContent.append("${article.cachePrixVent},")
        csvContent.append("${article.prixAchat},")
        csvContent.append("${article.prixAchatDernierTimeTempUpdate},")
        csvContent.append("${article.clientPrixVentUnite},")
        csvContent.append("${article.actualiseSonImage},")
        csvContent.append("${article.actualiseSonImageTest2},")
        csvContent.append(
            "\"${
                article.afficheCesDetailPourComptBsonId.replace(
                    "\"",
                    "\"\""
                )
            }\","
        )
        csvContent.append("\"${article.disponibilityEtates.name}\",")
        csvContent.append("\"${article.keyFireBase.replace("\"", "\"\"")}\",")
        csvContent.append("\"${article.nomArab.replace("\"", "\"\"")}\",")
        csvContent.append("\"${(article.autreNomDarticle ?: "").replace("\"", "\"\"")}\",")
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
        csvContent.append("${article.idForSearchArticles},")
        csvContent.append("\"${article.setIN_Vent_Its_Quantity_Represent.name}\",")
        csvContent.append("${article.quantite_Boit_Par_Carton}\n")
    }

    // Create file with fixed name
    val fileName = "ArticlesBasesStatsTable.csv"
    val file = File(exportDir, fileName)

    // Write CSV content to file
    withContext(Dispatchers.IO) {
        FileWriter(file).use { writer ->
            writer.write(csvContent.toString())
        }
    }
}

private suspend fun exportClientsToCsv(
    context: Context,
    appDatabase: AppDatabase,
    exportDir: File
) {
    // Get all clients from database
    val clients = appDatabase.B_ClientInfosProtoJuin3Dao().getAll()

    // Create CSV content
    val csvContent = StringBuilder()

    // Add CSV header for M2Client - keeping existing schema as it appears to be correct
    csvContent.append("keyID,nom,cretionTimestamps,numTelephone,couleur,bonDuClientsSu,currentCreditBalance,positionDonClientsList,cUnClientTemporaire,auFilterFAB,typeDeSonMagasine,clientTypeMode,caMarqueGpsEstOuvert,latitude,longitude,title,snippet,actuelleEtat,tagCeBonEstOuvertPourComptsIds,keyFireBase,dernierTimeTampsSynchronisationAvecFireBase\n")

    // Add client data
    clients.forEach { client ->
        csvContent.append("${client.id},")
        csvContent.append("\"${client.nom.replace("\"", "\"\"")}\",")
        csvContent.append("${client.cretionTimestamps},")
        csvContent.append("\"${client.numTelephone.replace("\"", "\"\"")}\",")
        csvContent.append("\"${client.couleur.replace("\"", "\"\"")}\",")
        csvContent.append("\"${client.bonDuClientsSu.replace("\"", "\"\"")}\",")
        csvContent.append("${client.currentCreditBalance},")
        csvContent.append("${client.positionDonClientsList},")
        csvContent.append("${client.cUnClientTemporaire},")
        csvContent.append("${client.auFilterFAB},")
        csvContent.append("${client.typeDeSonMagasine},")
        csvContent.append("${client.clientTypeMode},")
        csvContent.append("${client.caMarqueGpsEstOuvert},")
        csvContent.append("${client.latitude},")
        csvContent.append("${client.longitude},")
        csvContent.append("\"${client.title.replace("\"", "\"\"")}\",")
        csvContent.append("\"${client.snippet.replace("\"", "\"\"")}\",")
        csvContent.append("${client.actuelleEtat},")
        csvContent.append("\"${client.tagCeBonEstOuvertPourComptsIds.replace("\"", "\"\"")}\",")
        csvContent.append("\"${client.keyFireBase.replace("\"", "\"\"")}\",")
        csvContent.append("${client.dernierTimeTampsSynchronisationAvecFireBase}\n")
    }

    // Create file with fixed name
    val fileName = "M2Client.csv"
    val file = File(exportDir, fileName)

    // Write CSV content to file
    FileWriter(file).use { writer ->
        writer.write(csvContent.toString())
    }
}
