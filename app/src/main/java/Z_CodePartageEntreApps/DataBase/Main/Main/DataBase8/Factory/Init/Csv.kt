package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import java.io.File

fun DataBaseInitFactory_8BonVent.onLoadCategoriesFromCsv(): MutableList<M8BonVent> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "")

    if (!csvFile.exists()) {
        return mutableListOf() // Return empty list instead of nothing
    }

    val datas = mutableListOf<M8BonVent>()
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
// For Entity_8BonVent CSV
fun parseCsvLine(line: String): M8BonVent {
    val values = parseCsvValues(line)

    if (values.size < 12) { // Adjust based on Entity_8BonVent fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 12 columns, got ${values.size}")
    }

    return zAppcompt(values)
}


private fun zAppcompt(values: List<String>) = M8BonVent(
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
