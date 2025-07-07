package Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.Init

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.MainRepositorysSetterFacade
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.DataBaseCreationFactoryGBonVent
import java.io.File

fun DataBaseCreationFactoryGBonVent.onLoadCategoriesFromCsv(): MutableList<M8BonVent> {
    val imagesProduitsLocalExternalStorageBasePath =
        "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
    val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "$repoEntityName.csv")

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
// For GBonVentEntity CSV
fun parseCsvLine(line: String): M8BonVent {
    val values = parseCsvValues(line)

    if (values.size < 12) { // Adjust based on GBonVentEntity fields
        throw IllegalArgumentException("Invalid CSV format: expected at least 12 columns, got ${values.size}")
    }

    return zAppcompt(values)
}


private fun zAppcompt(values: List<String>) = M8BonVent(
    parentID2ClientKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
    parentID7VentPeriodeKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent] ?: "",
    parentID8C2TypeTransactionKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
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
