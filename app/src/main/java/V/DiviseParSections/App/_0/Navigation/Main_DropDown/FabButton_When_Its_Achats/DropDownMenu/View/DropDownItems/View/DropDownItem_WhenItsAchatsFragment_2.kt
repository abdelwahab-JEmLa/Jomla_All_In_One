package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.DropDownItems.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
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
import org.koin.compose.koinInject

@Composable
fun DropDownItem_WhenItsAchatsFragment_2(
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    context: Context = LocalContext.current
) {
    val bonsSansTimeTampsEnd = repositorysMainGetter.repo8BonVent.datasValue.filter { it.confirmeCommande_TimeTamp == 0L }
    var needsConfirmation by remember { mutableStateOf(false) }

    fun executeBusinessLogic() {
        // Execute the bon vent logic
        bonsSansTimeTampsEnd.forEach { bon ->
            repositorysMainSetter.update_M8BonVent(
                bon.copy(confirmeCommande_TimeTamp = bon.creationTimestamps + 1000L) // 1 second in milliseconds
            )
        }
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (needsConfirmation) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (needsConfirmation) Icons.Default.Warning else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (needsConfirmation) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            },
            text = {
                Text(
                    text = if (needsConfirmation) "Êtes-vous sûr?" else nomFun,
                    color = if (needsConfirmation) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            onClick = {
                if (!needsConfirmation) {
                    needsConfirmation = true
                } else {
                    try {
                        executeBusinessLogic()

                        Toast.makeText(
                            context,
                            "Fonction '$nomFun' exécutée avec succès (${bonsSansTimeTampsEnd.size} bons mis à jour)",
                            Toast.LENGTH_LONG
                        ).show()

                        onDismissDropdown()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Erreur lors de l'exécution: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()

                        // Reset confirmation state on error
                        needsConfirmation = false
                    }
                }
            }
        )
    }
}
