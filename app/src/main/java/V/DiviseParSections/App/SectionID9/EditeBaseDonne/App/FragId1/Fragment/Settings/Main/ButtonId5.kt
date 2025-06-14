package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ButtonId5(
    AppDatabase: AppDatabase = koinInject(),
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    showLabels: Boolean,
    onImportSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coroutineScope.launch {
                importCategoriesFromCsv(context, AppDatabase, it, onImportSuccess)
            }
        }
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Import Categories")
        FloatingActionButton(
            onClick = {
                // Launch file picker for CSV files
                filePickerLauncher.launch("text/*")
            },
            modifier = Modifier.size(40.dp),
            containerColor = Color.Green
        ) {
            Icon(Icons.Default.Upload, "Import Categories from CSV", tint = Color.White)
        }
    }
}

private suspend fun importCategoriesFromCsv(
    context: Context,
    appDatabase: AppDatabase,
    uri: Uri,
    onImportSuccess: () -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val categoriesDao = appDatabase.categoriesModelDao()
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            val categories = mutableListOf<CategoriesTabelle>()
            var lineNumber = 0
            var isFirstLine = true
            
            reader.useLines { lines ->
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
            
            if (categories.isNotEmpty()) {
                // Clear existing data and insert new data
                categoriesDao.deleteAll()
                categoriesDao.insertAll(categories)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Successfully imported ${categories.size} categories",
                        Toast.LENGTH_LONG
                    ).show()
                    onImportSuccess()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "No valid categories found in the CSV file",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Error importing CSV: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
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
        dernierTimeTampsSynchronisationAvecFireBase = values[7].toLongOrNull() ?: System.currentTimeMillis()
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
