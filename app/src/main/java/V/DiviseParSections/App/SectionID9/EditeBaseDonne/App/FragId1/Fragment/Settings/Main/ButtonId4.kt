package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import android.content.Intent
import android.os.Environment
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
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        if (showLabels) Text("Export Categories")
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    exportCategoriesToCsv(context, AppDatabase)
                }
            },
            modifier = Modifier.size(40.dp),
            containerColor =  Color.Blue
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

            // Create file name with timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val fileName = "categories_export_$timestamp.csv"

            // Save to Downloads folder
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            // Write CSV content to file
            FileWriter(file).use { writer ->
                writer.write(csvContent.toString())
            }

            // Show success message and open file
            withContext(Dispatchers.Main) {
                shareFile(context, file)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error - you might want to show a Toast or Snackbar here
        }
    }
}

private fun shareFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val shareIntent = Intent.createChooser(intent, "Export Categories CSV")
        context.startActivity(shareIntent)

    } catch (e: Exception) {
        e.printStackTrace()
        // Fallback: try to open the file directly
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file),
                "text/csv"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}
