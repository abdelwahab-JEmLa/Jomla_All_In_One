package P0_MainScreen.Ui.Main.AppNavHost

import Views.P1.Ui.ArticlesGrid.ScrollHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

private const val TAG = "id2"

@Composable
fun CatalogSelectionDialog(
    onDismiss: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    viewModelInitApp: ViewModelInitApp,
    scrollHandler: ScrollHandler = koinInject()
) {
    Log.d(TAG, "CatalogSelectionDialog composable created")
    scrollHandler.logCurrentState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner un catalogue") },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Catalog options in the dialog
                androidx.compose.foundation.layout.Column {
                    TextButton(
                        onClick = { 
                            Log.d(TAG, "Cosmétiques catalog selected (ID: 148)")
                            onCatalogSelected(148L)
                            Log.d(TAG, "About to call scrollHandler.scrollToCategory(148L)")
                            scrollHandler.scrollToCategory(148L)
                            Log.d(TAG, "After scrollHandler.scrollToCategory(148L)")
                            scrollHandler.logCurrentState()
                        }
                    ) {
                        Text("Catalogue Cosmétiques")
                    }

                    TextButton(
                        onClick = { 
                            Log.d(TAG, "Confiseries catalog selected (ID: 149)")
                            onCatalogSelected(149L)
                            Log.d(TAG, "About to call scrollHandler.scrollToCategory(149L)")
                            scrollHandler.scrollToCategory(149L)
                            Log.d(TAG, "After scrollHandler.scrollToCategory(149L)")
                            scrollHandler.logCurrentState()
                        }
                    ) {
                        Text("Catalogue Confiseries")
                    }

                    TextButton(
                        onClick = { 
                            Log.d(TAG, "Téléphones catalog selected (ID: 150)")
                            onCatalogSelected(150L)
                            Log.d(TAG, "About to call scrollHandler.scrollToCategory(150L)")
                            scrollHandler.scrollToCategory(150L)
                            Log.d(TAG, "After scrollHandler.scrollToCategory(150L)")
                            scrollHandler.logCurrentState()
                        }
                    ) {
                        Text("Catalogue Téléphones")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Annuler")
            }
        }
    )
}
