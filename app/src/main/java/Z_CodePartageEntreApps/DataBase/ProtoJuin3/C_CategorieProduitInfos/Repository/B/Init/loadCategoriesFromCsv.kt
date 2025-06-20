package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.CategoriesTabelle
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.File

fun loadCategoriesFromCsv(): List<CategoriesTabelle> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "CategoriesTabelle.csv")

    if (!csvFile.exists()) {
        return emptyList()
    }

    val categories = mutableListOf<CategoriesTabelle>()
    var lineNumber = 0
    var isFirstLine = true

    try {
        csvFile.useLines { lines ->
            lines.forEach { line ->
                lineNumber++

                if (isFirstLine) {
                    isFirstLine = false
                    return@forEach
                }

                if (line.trim().isEmpty()) {
                    return@forEach
                }

                try {
                    val category = parseCsvLine(line)
                    categories.add(category)
                } catch (e: Exception) {
                    // Skip invalid lines
                }
            }
        }
    } catch (e: Exception) {
        return emptyList()
    }

    return categories
}

fun parseCsvLine(line: String): CategoriesTabelle {
    val values = parseCsvValues(line)

    if (values.size < 8) {
        throw IllegalArgumentException("Invalid CSV format: expected 8 columns, got ${values.size}")
    }

    return CategoriesTabelle(
        id = values[0].toLongOrNull() ?: System.currentTimeMillis(),
        catalogueParentId = values[1].toLongOrNull() ?: 0L,
        nom = values[2],
        position = values[3].toIntOrNull() ?: 0,
        displayedHeader = values[4].toBoolean(),
        itsHeldPourDeplacement = values[5].toBoolean(),
        cSelectionePourDeplace = values[6].toBoolean(),
        dernierTimeTampsSynchronisationAvecFireBase = values[7].toLongOrNull()
            ?: System.currentTimeMillis()
    )
}

fun parseCsvValues(line: String): List<String> {
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

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
