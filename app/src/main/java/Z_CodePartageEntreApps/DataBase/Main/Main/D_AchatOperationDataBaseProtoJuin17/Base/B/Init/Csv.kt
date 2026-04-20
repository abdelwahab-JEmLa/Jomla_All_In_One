package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import org.mongodb.kbson.BsonObjectId
import java.io.File

fun onLoadCategoriesFromCsvD_AchatOperation(): MutableList<M10OperationVentCouleur> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "M10OperationVentCouleur.csv")

    if (!csvFile.exists()) {
        return mutableListOf() // Return empty list instead of nothing
    }

    val datas = mutableListOf<M10OperationVentCouleur>()
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
// For M10OperationVentCouleur CSV
fun parseCsvLine(line: String): M10OperationVentCouleur {
    val values = parseCsvValues(line)

    if (values.size < 12) { // Adjust based on M10OperationVentCouleur fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 12 columns, got ${values.size}")
    }

    return parsedData(values)
}


private fun parsedData(values: List<String>) = M10OperationVentCouleur(
    keyID = values.getOrElse(0) { BsonObjectId().toHexString() },
    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis(),
    parent_M9AppCompt_KeyID = values.getOrElse(5) { "" },
    parent_M8BonVent_KeyId = values.getOrElse(3) { "" },
    parent_M1Produit_KeyId = values.getOrElse(4) { "" },
    parentProduitInfosOldId = values.getOrElse(7) { "0" }.toLongOrNull() ?: 0L,
    etateActuellementEst = try {
        M10OperationVentCouleur.EtateActuellementEst.valueOf(values.getOrElse(9) { "ClickOuvre" })
    } catch (e: IllegalArgumentException) {
        M10OperationVentCouleur.EtateActuellementEst.CreeSlote
    },
    quantity = values.getOrElse(8) { "0" }.toIntOrNull() ?: 0,
    provisoireMonPrix = values.getOrElse(10) { "0.0" }.toDoubleOrNull() ?: 0.0,
    parentClientInfosKeyID = values.getOrElse(6) { "" }
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
