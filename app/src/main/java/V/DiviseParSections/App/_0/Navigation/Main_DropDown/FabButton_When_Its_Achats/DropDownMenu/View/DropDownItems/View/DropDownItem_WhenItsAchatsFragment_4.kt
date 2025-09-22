package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenItsAchatsFragment_4(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    context: Context = LocalContext.current
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isFloatingButtonVisible = currentValues.afficheFloatingOutlinedSearcher_of_Achat
    var clickCount by remember { mutableStateOf(0) }
    val componentScope = remember { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFloatingButtonVisible) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (isFloatingButtonVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = if (isFloatingButtonVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            text = {
                Text(
                    text = if (clickCount == 0) nomFun else "Êtes-vous sûr ?",
                    color = if (isFloatingButtonVisible) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                if (clickCount == 0) {
                    // First click - show confirmation message
                    Toast.makeText(
                        context,
                        "Êtes-vous sûr ? Cliquez à nouveau pour confirmer.",
                        Toast.LENGTH_SHORT
                    ).show()
                    clickCount = 1
                } else {
                    // Second click - execute the main function

                    // Get current date in milliseconds (start of today)
                    val calendar = java.util.Calendar.getInstance()
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    calendar.set(java.util.Calendar.MINUTE, 0)
                    calendar.set(java.util.Calendar.SECOND, 0)
                    calendar.set(java.util.Calendar.MILLISECOND, 0)
                    val todayStartTime = calendar.timeInMillis

                    // Filter data based on creation time
                    val todayData = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { operation ->
                        operation.creationTimestamps >= todayStartTime
                    }

                    val beforeTodayData = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { operation ->
                        operation.creationTimestamps in 1..<todayStartTime
                    }

                    // Create initial info message
                    val initialMessage = buildString {
                        append("Données d'aujourd'hui: ${todayData.size}")
                        append("\nDonnées avant aujourd'hui: ${beforeTodayData.size}")
                        append("\nTotal: ${repositorysMainGetter.repo10OperationVentCouleur.datasValue.size}")
                        if (beforeTodayData.isNotEmpty()) {
                            append("\n\nSuppression des données avant aujourd'hui...")
                        }
                    }

                    Toast.makeText(
                        context,
                        initialMessage,
                        Toast.LENGTH_LONG
                    ).show()

                    // Delete data before today using the existing delete function
                    if (beforeTodayData.isNotEmpty()) {
                        componentScope.launch {
                            try {
                                var deletedCount = 0
                                beforeTodayData.forEach { operation ->
                                    repositorysMainGetter.repo10OperationVentCouleur.delete(operation)
                                    deletedCount++
                                }

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Suppression terminée: $deletedCount éléments supprimés",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Erreur lors de la suppression: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    // Reset click count and dismiss dropdown
                    clickCount = 0
                    onDismissDropdown()
                }
            }
        )
    }
}
