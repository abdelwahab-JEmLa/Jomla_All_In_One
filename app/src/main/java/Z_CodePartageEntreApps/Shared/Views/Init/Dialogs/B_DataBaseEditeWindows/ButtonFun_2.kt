package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows

import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
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

@Composable
fun ButtonFun_2(
    viewModel: ViewModelA_ProduitModelButtons = koinViewModel(),
    onProgressUpdate: (Float) -> Unit,
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
                    text = "deleteRef()",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmation") },
            text = {
                Text(
                    text = """
                       fun deleteRef(onProgress: (Float) -> Unit = {}) {
                        try {
                            val reference = ref_HeadOfModels
                
                            // Clear current products first
                            reference
                                .child("produits")
                                .removeValue()
                
                            // Clear current products first
                            reference
                                .child("produit_DataBase")
                                .removeValue()
                        } catch (e: Exception) {
                            // Consider logging the error
                            // Log.e("DeleteRef", "Error deleting references", e)
                        }
                    }
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
                        try {
                            ref_HeadOfModels
                                .child("produits")
                                .removeValue()

                            ref_HeadOfModels
                                .child("prodiots")
                                .removeValue()

                            onProgressUpdate(1f) // Indicate full progress
                        } catch (e: Exception) {
                            onProgressUpdate(0f) // Indicate failure
                            // Consider adding error handling or logging
                        }
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
