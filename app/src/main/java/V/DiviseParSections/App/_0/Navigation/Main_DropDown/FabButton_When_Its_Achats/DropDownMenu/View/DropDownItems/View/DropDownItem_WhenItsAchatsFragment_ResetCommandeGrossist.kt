package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
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
fun DropDownItem_WhenItsAchatsFragment_ResetCommandeGrossist(
    nomFun: String = "Réinitialiser a_cammende_depuit_grossist",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = koinInject(),
    context: Context = LocalContext.current
) {
    var clickCount by remember { mutableStateOf(0) }
    val componentScope = remember { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            text = {
                Text(
                    text = if (clickCount == 0) nomFun else "Êtes-vous sûr ?",
                    color = MaterialTheme.colorScheme.onSurface
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
                    // Second click - execute the reset
                    val allColors = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                    val colorsToReset = allColors.filter { it.a_cammende_depuit_grossist != 0 }

                    if (colorsToReset.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Aucune couleur à réinitialiser (toutes les commandes sont déjà à 0)",
                            Toast.LENGTH_SHORT
                        ).show()
                        clickCount = 0
                        onDismissDropdown()
                        return@DropdownMenuItem
                    }

                    val initialMessage = buildString {
                        append("Couleurs à réinitialiser: ${colorsToReset.size}")
                        append("\nTotal des couleurs: ${allColors.size}")
                        append("\n\nRéinitialisation des commandes grossiste...")
                    }

                    Toast.makeText(
                        context,
                        initialMessage,
                        Toast.LENGTH_LONG
                    ).show()

                    componentScope.launch {
                        try {
                            var resetCount = 0
                            colorsToReset.forEach { color ->
                                val updatedColor = color.copy(
                                    a_cammende_depuit_grossist = 0,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                                repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(updatedColor)
                                resetCount++
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Réinitialisation terminée: $resetCount couleurs mises à jour",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la réinitialisation: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    clickCount = 0
                    onDismissDropdown()
                }
            }
        )
    }
}
