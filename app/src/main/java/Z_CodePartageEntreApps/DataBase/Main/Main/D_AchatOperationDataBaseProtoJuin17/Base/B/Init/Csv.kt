package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.D_AchatOperation
import org.mongodb.kbson.BsonObjectId
import java.io.File

fun onLoadCategoriesFromCsvD_AchatOperation(): MutableList<D_AchatOperation> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "D_AchatOperation.csv")

    if (!csvFile.exists()) {
        return mutableListOf() // Return empty list instead of nothing
    }

    val datas = mutableListOf<D_AchatOperation>()
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
                    datas.add(category)
                } catch (e: Exception) {
                    throw IllegalStateException("No data available from  or CSV")
                }
            }
        }
        return datas
    } catch (e: Exception) {
        throw IllegalStateException("No data available from  or CSV")
    }
}
// For D_AchatOperation CSV
fun parseCsvLine(line: String): D_AchatOperation {
    val values = parseCsvValues(line)

    if (values.size < 12) { // Adjust based on D_AchatOperation fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 12 columns, got ${values.size}")
    }

    return zAppcompt(values)
}


private fun zAppcompt(values: List<String>) = D_AchatOperation(
    bsonObjectId = values.getOrElse(0) { BsonObjectId().toHexString() },
    creationTimesTamp = values.getOrElse(1) { System.currentTimeMillis().toString() }.toLongOrNull() ?: System.currentTimeMillis(),
    nomImageFichieOuApellationDuCouleur = values.getOrElse(2) { "" },
    parentBonVentObjectId = values.getOrElse(3) { "" },
    parentProduitBsonObjectId = values.getOrElse(4) { "" },
    parentComptVendeurCreateurObjectId = values.getOrElse(5) { "" },
    clientParentObjectId = values.getOrElse(6) { "" },
    produitAcheterAncienID = values.getOrElse(7) { "0" }.toLongOrNull() ?: 0L,
    quantityAchete = values.getOrElse(8) { "0" }.toIntOrNull() ?: 0,
    etateActuellementEst = try {
        D_AchatOperation.EtateActuellementEst.valueOf(values.getOrElse(9) { "Affiche" })
    } catch (e: IllegalArgumentException) {
        D_AchatOperation.EtateActuellementEst.Affiche
    },
    provisoireMonPrix = values.getOrElse(10) { "0.0" }.toDoubleOrNull() ?: 0.0,
    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
)


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
