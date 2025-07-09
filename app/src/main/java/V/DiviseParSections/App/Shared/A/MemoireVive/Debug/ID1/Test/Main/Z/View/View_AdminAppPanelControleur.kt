package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.A_MainListView
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ScreenM14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_AdminAppPanelControleur(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun View_AdminAppPanelControleur(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AdminAppPanelControleur = koinInject(),
) {
    var showM14VentPeriod by remember { mutableStateOf(false) }

    val defaultGeneratedCompt = Z_AppCompt(
        nom = "Abdelwahab"
    ).apply {
        nomsMutableTags = addStringAuNomsMutableTags("Abdelwahab").joinToString(",")
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (showM14VentPeriod) {
            // Show ScreenM14VentPeriod
            ScreenM14VentPeriod(modifier = modifier)
        } else {
            // Show the main content
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    A_MainListView(
                        viewModel,
                    )
                }
            }
        }

        // FAB for adding default compte - only show when not in M14VentPeriod view
        if (!showM14VentPeriod) {
            FloatingActionButton(
                onClick = {
                   // viewModel.aCentralFacade.set.addAuRepoM9AppComptParFacade(defaultGeneratedCompt)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Default Compte",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // FAB for showing ScreenM14VentPeriod
            FloatingActionButton(
                onClick = {
                    showM14VentPeriod = !showM14VentPeriod
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = if (showM14VentPeriod) "Hide M14 Vent Period" else "Show M14 Vent Period",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

    }
}
