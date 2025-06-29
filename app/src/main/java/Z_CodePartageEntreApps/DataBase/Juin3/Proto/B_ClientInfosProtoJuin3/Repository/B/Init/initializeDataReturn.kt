package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.B.Init

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.HClientInfos
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.FireBase.getFirebaseData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL.isRoomEmpty
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

suspend fun DataBaseFactoryFClient.initializeDataReturn(): List<HClientInfos> {
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
                val csvData = loadClientsFromCsv()
                if (csvData.isNotEmpty()) {
                    dao.insertAll(csvData)
                }
                csvData
            }
        } else {
            val csvData = loadClientsFromCsv()
            if (csvData.isNotEmpty()) {
                dao.insertAll(csvData)
            }
            csvData
        }
    } else {
        dao.getAll()
    }
}

private fun loadClientsFromCsv(): List<HClientInfos> {
    val csvBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(csvBasePath, "HClientInfos.csv")

    if (!csvFile.exists()) {
        return emptyList()
    }

    val clients = mutableListOf<HClientInfos>()
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
                    val client = parseCsvLineToClient(line)
                    clients.add(client)
                } catch (e: Exception) {
                    // Skip invalid lines
                }
            }
        }
    } catch (e: Exception) {
        return emptyList()
    }

    return clients
}

private fun parseCsvLineToClient(line: String): HClientInfos {
    val values = parseCsvValues(line)

    // Adjust the expected column count based on your CSV structure
    if (values.size < 20) { // Minimum required fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 20 columns, got ${values.size}")
    }

    return HClientInfos(
        id = values[0].toLongOrNull() ?: 0L,
        nom = values[1].takeIf { it.isNotBlank() } ?: "Non Defini",
        cretionTimestamps = values[2].toLongOrNull() ?: System.currentTimeMillis(),
        numTelephone = values[3],
        couleur = values[4].takeIf { it.isNotBlank() } ?: "#FFFFFF",
        bonDuClientsSu = values[5],
        currentCreditBalance = values[6].toDoubleOrNull() ?: 0.0,
        positionDonClientsList = values[7].toIntOrNull() ?: 0,
        cUnClientTemporaire = values[8].toBooleanStrictOrNull() ?: true,
        auFilterFAB = values[9].toBooleanStrictOrNull() ?: false,
        typeDeSonMagasine = values[10].let {
            try {
                HClientInfos.TypeDeSonMagasine.valueOf(it)
            } catch (e: Exception) {
                HClientInfos.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
            }
        },
        clientTypeMode = values[11].let {
            try {
                HClientInfos.ClientTypeMode.valueOf(it)
            } catch (e: Exception) {
                HClientInfos.ClientTypeMode.NEVEAU
            }
        },
        caMarqueGpsEstOuvert = values[12].toBooleanStrictOrNull() ?: false,
        latitude = values[13].toDoubleOrNull() ?: 0.0,
        longitude = values[14].toDoubleOrNull() ?: 0.0,
        title = values[15],
        snippet = values[16],
        actuelleEtat = values[17].let {
            try {
                HClientInfos.DernierEtatAAffiche.valueOf(it)
            } catch (e: Exception) {
                HClientInfos.DernierEtatAAffiche.NON_DEFINI
            }
        },
        tagCeBonEstOuvertPourComptsIds = values[18],
        keyFireBase = values[19],
        dernierTimeTampsSynchronisationAvecFireBase = values[20].toLongOrNull() ?: 0
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
