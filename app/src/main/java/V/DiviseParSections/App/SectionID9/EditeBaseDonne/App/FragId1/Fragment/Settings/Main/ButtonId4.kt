package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
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
import java.io.File
import java.io.FileWriter

@Composable
fun ButtonId4(
    AppDatabase: AppDatabase = koinInject(),
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    showLabels: Boolean,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showLabels) Text("Export Categories ")
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    exportCategoriesToCsv(context, AppDatabase)
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor =Color.Blue
        ) {
            Icon(Icons.Default.Download, "Export Categories to CSV", tint = Color.White)
        }
    }
}

private suspend fun exportCategoriesToCsv(context: Context, appDatabase: AppDatabase) {
    withContext(Dispatchers.IO) {
        try {
            // Get all categories from database
            val categories = appDatabase.categoriesModelDao().getAll()

            // Create CSV content
            val csvContent = StringBuilder()

            // Add CSV header
            csvContent.append("id,catalogueParentId,nom,position,displayedHeader,itsHeldPourDeplacement,cSelectionePourDeplace,dernierTimeTampsSynchronisationAvecFireBase\n")

            // Add category data
            categories.forEach { category ->
                csvContent.append("${category.id},")
                csvContent.append("${category.catalogueParentId},")
                csvContent.append("\"${category.nom.replace("\"", "\"\"")}\",") // Escape quotes in CSV
                csvContent.append("${category.position},")
                csvContent.append("${category.displayedHeader},")
                csvContent.append("${category.itsHeldPourDeplacement},")
                csvContent.append("${category.cSelectionePourDeplace},")
                csvContent.append("${category.dernierTimeTampsSynchronisationAvecFireBase}\n")
            }

            // Create the specific directory path
            val imagesProduitsLocalExternalStorageBasePath = "/storage/emulated/0/Abdelwahab_jeMla.com/RoomDataBasesCsv"
            val exportDir = File(imagesProduitsLocalExternalStorageBasePath)

            // Create directory if it doesn't exist
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            // Create file with fixed name
            val fileName = "CategoriesTabelle.csv"
            val file = File(exportDir, fileName)

            // Write CSV content to file
            FileWriter(file).use { writer ->
                writer.write(csvContent.toString())
            }

            // Show success message
            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    context,
                    "Categories exported successfully to: ${file.absolutePath}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            // Log error and show error toast
            e.printStackTrace()
            android.util.Log.e("ButtonId4", "Error exporting categories to CSV", e)

            withContext(Dispatchers.Main) {
                android.widget.Toast.makeText(
                    context,
                    "Error exporting CSV: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
