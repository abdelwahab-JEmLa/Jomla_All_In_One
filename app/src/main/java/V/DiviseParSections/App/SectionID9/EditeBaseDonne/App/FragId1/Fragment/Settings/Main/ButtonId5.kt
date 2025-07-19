package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import android.content.Context
import android.widget.Toast
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
import java.io.File

@Composable
fun ButtonId5(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    list_M1Produit: List<ArticlesBasesStatsTable> = viewModel.aCentralFacade.repoMainGetter.repo1ProduitInfos.datasValue,
    list_CategoriesTabelle: List<CategoriesTabelle> = viewModel.aCentralFacade.repoMainGetter.repoM16CategorieProduit.datasValue,
    showLabels: Boolean,
    onImportSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var clickCount by remember { mutableStateOf(0) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val text =
            when (clickCount) {
              0->  "Delete ADD l_Produit Et l_Categorie"
              else ->  "T Sure"
            }

        if (showLabels) {
            Text(text)
        }

        FloatingActionButton(
            onClick = {
                when (clickCount) {
                    0 -> clickCount++
                    1 -> coroutineScope.launch {
                        viewModel.deleteAddMultiCategories(list_CategoriesTabelle)
                        viewModel.deleteAddMultiProduits(list_M1Produit)
                        //  viewModel.deleteAddMultiClients()
                    }
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor = Color.Green
        ) {
            Icon(Icons.Default.Upload, "Import Categories from CSV", tint = Color.White)
        }
    }
}

private suspend fun importCategoriesFromCsv(
    context: Context
): List<CategoriesTabelle> {
    return withContext(Dispatchers.IO) {
        try {
            // Use the same path as ButtonId4
            val imagesProduitsLocalExternalStorageBasePath =
                "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
            val csvFile = File(imagesProduitsLocalExternalStorageBasePath, "CategoriesTabelle.csv")

            // Check if file exists
            if (!csvFile.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "CSV file not found at: ${csvFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@withContext emptyList<CategoriesTabelle>()
            }

            val categories = mutableListOf<CategoriesTabelle>()
            var lineNumber = 0
            var isFirstLine = true

            csvFile.useLines { lines ->
                lines.forEach { line ->
                    lineNumber++

                    // Skip header line
                    if (isFirstLine) {
                        isFirstLine = false
                        return@forEach
                    }

                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        return@forEach
                    }

                    try {
                        val category = parseCsvLine(line)
                        categories.add(category)
                    } catch (e: Exception) {
                        // Log error for specific line but continue processing
                        println("Error parsing line $lineNumber: $line - ${e.message}")
                    }
                }
            }

            if (categories.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "No valid categories found in the CSV file",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            categories

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Error importing CSV: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            emptyList<CategoriesTabelle>()
        }
    }
}

private fun parseCsvLine(line: String): CategoriesTabelle {
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
                    // Escaped quote (double quote)
                    currentValue.append('"')
                    i++ // Skip next quote
                } else {
                    // Toggle quote state
                    insideQuotes = !insideQuotes
                }
            }

            char == ',' && !insideQuotes -> {
                // Field separator
                values.add(currentValue.toString())
                currentValue.clear()
            }

            else -> {
                currentValue.append(char)
            }
        }
        i++
    }

    // Add the last value
    values.add(currentValue.toString())

    return values
}
