package Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.I_CategorieProduits.Z_ButtonsFunctions

import Z_CodePartageEntreApps.Windows.A.B_DataBaseEdite.Windows.I_CategorieProduits.ViewModel_I_CategorieProduits
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// Define add_New constant TAG for logging
const val TAG = "ProductUpdateLog"

@Composable
fun ButtonFun_1_populateB_ClientDataBaseParSonAncien(
    viewModel: ViewModel_I_CategorieProduits = koinViewModel(),
    onProgressUpdate: (Float) -> Unit,
    nameFunciotn: String
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showConfirmationDialog = true },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    nameFunciotn,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(nameFunciotn) },
            text = {
                Text(
                    text = """
                        
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
                    coroutineScope.launch {
                        viewModel.populateModelearSonAncien()

                        showConfirmationDialog = false
                    }
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
