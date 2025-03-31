package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.A

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.koin.androidx.compose.koinViewModel

@Composable
fun ButFun_3_toggleBackupTrigger(
    viewModel: ViewModelA_ProduitModelButtons = koinViewModel(),
    onProgressUpdate: (Float) -> Unit,
    nameFunciotn: String
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    // Get the backup state from the ViewModel
    val backupState by viewModel.backupState.collectAsState(initial = false)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showConfirmationDialog = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = nameFunciotn,
                style = MaterialTheme.typography.titleMedium
            )

            // Simplified icon implementation with higher z-index to ensure visibility
            Icon(
                imageVector = if (backupState) Icons.Filled.CheckCircle else Icons.Filled.Backup,
                contentDescription = if (backupState) "Backup Completed Today" else "Backup Needed",
                tint = if (backupState) Color.Green else Color.Red.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(24.dp)
                    .zIndex(1f)
            )
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(nameFunciotn) },
            text = {
                Text(
                    text = """
                             $nameFunciotn
                             
                             ${if (backupState) "A backup has already been done today." else "No backup has been performed today."}
                    """.trimIndent(),
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.1f))
                        .padding(8.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.toggleBackupTrigger()
                    onProgressUpdate(1f) // Set progress to 100% when backup action completes
                    showConfirmationDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
