package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_CouleurVentOperation
import org.mongodb.kbson.BsonObjectId
import java.io.File

fun onLoadCategoriesFromCsvD_AchatOperation(): MutableList<D_CouleurVentOperation> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "D_CouleurVentOperation.csv")

    if (!csvFile.exists()) {
        return mutableListOf() // Return empty list instead of nothing
    }

    val datas = mutableListOf<D_CouleurVentOperation>()
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
// For D_CouleurVentOperation CSV
fun parseCsvLine(line: String): D_CouleurVentOperation {
    val values = parseCsvValues(line)

    if (values.size < 12) { // Adjust based on D_CouleurVentOperation fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 12 columns, got ${values.size}")
    }

    return parsedData(values)
}


private fun parsedData(values: List<String>) = D_CouleurVentOperation(
    id = values.getOrElse(0) { BsonObjectId().toHexString() },
    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis(),
    nomImageFichieOuApellationDuCouleur = values.getOrElse(2) { "" },
    parentBonVentId = values.getOrElse(3) { "" },
    parentProduitId = values.getOrElse(4) { "" },
    parentZAppComptID = values.getOrElse(5) { "" },
    parentClientId = values.getOrElse(6) { "" },
    parentProduitAncienId = values.getOrElse(7) { "0" }.toLongOrNull() ?: 0L,
    quantityAchete = values.getOrElse(8) { "0" }.toIntOrNull() ?: 0,
    etateActuellementEst = try {
        D_CouleurVentOperation.EtateActuellementEst.valueOf(values.getOrElse(9) { "ClickOuvre" })
    } catch (e: IllegalArgumentException) {
        D_CouleurVentOperation.EtateActuellementEst.CreeSlote
    },
    provisoireMonPrix = values.getOrElse(10) { "0.0" }.toDoubleOrNull() ?: 0.0
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
