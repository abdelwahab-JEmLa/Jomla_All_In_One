package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.isRoomEmpty
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

suspend fun A_ProduitInfosRepository.initializeDataReturn(): List<ArticlesBasesStatsTable> {
    return if (isRoomEmpty()) {
        val hasInternetConnection = isInternetAvailable(context)

        if (hasInternetConnection) {
            val firebaseData = suspendCancellableCoroutine { continuation ->
                getFirebaseData { dataFB ->
                    continuation.resume(dataFB)
                }
            }

            if (firebaseData.isNotEmpty()) {
                dao.insertAll(firebaseData)
                firebaseData
            } else {
                val csvData = loadArticlesFromCsv()
                if (csvData.isNotEmpty()) {
                    dao.insertAll(csvData)
                }
                csvData
            }
        } else {
            val csvData = loadArticlesFromCsv()
            if (csvData.isNotEmpty()) {
                dao.insertAll(csvData)
            }
            csvData
        }
    } else {
        dao.getAll()
    }
}

private fun A_ProduitInfosRepository.loadArticlesFromCsv(): List<ArticlesBasesStatsTable> {
    val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "ArticlesBasesStatsTable.csv")

    if (!csvFile.exists()) {
        return emptyList()
    }

    val articles = mutableListOf<ArticlesBasesStatsTable>()
    var isFirstLine = true

    try {
        csvFile.useLines { lines ->
            lines.forEach { line ->
                if (isFirstLine) {
                    isFirstLine = false
                    return@forEach
                }

                if (line.trim().isEmpty()) {
                    return@forEach
                }

                try {
                    val article = parseCsvLine(line)
                    articles.add(article)
                } catch (e: Exception) {
                    // Skip invalid lines
                }
            }
        }
    } catch (e: Exception) {
        return emptyList()
    }

    return articles
}
private fun parseCsvLine(line: String): ArticlesBasesStatsTable {
    val values = parseCsvValues(line)

    if (values.size < 44) {
        throw IllegalArgumentException("Invalid CSV format: expected 44 columns, got ${values.size}")
    }

    return ArticlesBasesStatsTable(
        id = values[0].toLongOrNull() ?: 0L,
        idParentCategorie = values[1].toLongOrNull(),
        nom = values[2],
        nombreUniteInt = values[3].toIntOrNull() ?: 0,
        nombreProduitDonSonCarton = values[4].toIntOrNull() ?: 0,
        dernierFireBaseUpdateTimestamps = values[5].toLongOrNull() ?: System.currentTimeMillis(),
        prixVent = values[6].toDoubleOrNull() ?: 0.0,
        prixAchat = values[7].toDoubleOrNull() ?: 0.0,
        clientPrixVentUnite = values[8].toDoubleOrNull() ?: 0.0,
        actualiseSonImage = values[9].toIntOrNull() ?: 0,
        actualiseSonImageTest2 = values[10].toIntOrNull() ?: 0,
        disponibilityEtates = values[11].let {
            try { DisponibilityEtates.valueOf(it) } catch (e: Exception) { DisponibilityEtates.DISPO }
        },
        keyFireBase = values[12],
        nomArab = values[13],
        autreNomDarticle = values[14].takeIf { it.isNotBlank() },

        // Additional fields that were missing (indices 15-43)
        couleur1 = values[15].takeIf { it.isNotBlank() },
        idcolor1 = values[16].toLongOrNull() ?: 0,
        couleur2 = values[17].takeIf { it.isNotBlank() },
        idcolor2 = values[18].toLongOrNull() ?: 0,
        couleur3 = values[19].takeIf { it.isNotBlank() },
        idcolor3 = values[20].toLongOrNull() ?: 0,
        couleur4 = values[21].takeIf { it.isNotBlank() },
        idcolor4 = values[22].toLongOrNull() ?: 0,
        nomCategorie2 = values[23].takeIf { it.isNotBlank() },
        affichageUniteState = values[24].toBooleanStrictOrNull() ?: false,
        commmentSeVent = values[25].takeIf { it.isNotBlank() },
        afficheBoitSiUniter = values[26].takeIf { it.isNotBlank() },
        minQuan = values[27].toIntOrNull() ?: 0,
        monBenfice = values[28].toDoubleOrNull() ?: 0.0,
        neaon2 = values[29],
        catalogeParentID = values[30].toLongOrNull() ?: 0,
        funChangeImagsDimention = values[31].toBooleanStrictOrNull() ?: false,
        nomCategorie = values[32],
        neaon1 = values[33].toDoubleOrNull() ?: 0.0,
        lastUpdateState = values[34],
        cartonState = values[35],
        dateCreationCategorie = values[36],
        prixDeVentTotaleChezClient = values[37].toDoubleOrNull() ?: 0.0,
        benficeTotaleEntreMoiEtClien = values[38].toDoubleOrNull() ?: 0.0,
        benificeTotaleEn2 = values[39].toDoubleOrNull() ?: 0.0,
        monPrixAchatUniter = values[40].toDoubleOrNull() ?: 0.0,
        monPrixVentUniter = values[41].toDoubleOrNull() ?: 0.0,
        articleHaveUniteImages = values[42].toBooleanStrictOrNull() ?: false,
        itsNewArrivale = values[43].toBooleanStrictOrNull() ?: false,
        imageDimention = if (values.size > 44) values[44] else "",
        idForSearchArticles = if (values.size > 45) values[45].toLongOrNull() ?: 0 else 0
    )
}

private fun parseCsvValues(line: String): List<String> {
    val values = mutableListOf<String>()
    val currentValue = StringBuilder()
    var insideQuotes = false
    var i = 0

    while (i < line.length) {
        val char = line[i]

        when {
            char == '"' -> {
                if (insideQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    currentValue.append('"')
                    i++
                } else {
                    insideQuotes = !insideQuotes
                }
            }
            char == ',' && !insideQuotes -> {
                values.add(currentValue.toString())
                currentValue.clear()
            }
            else -> {
                currentValue.append(char)
            }
        }
        i++
    }

    values.add(currentValue.toString())
    return values
}

private fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
