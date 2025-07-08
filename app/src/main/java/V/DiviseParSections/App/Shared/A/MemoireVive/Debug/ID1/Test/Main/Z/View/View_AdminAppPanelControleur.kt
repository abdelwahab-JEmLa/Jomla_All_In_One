package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View.Z.List.A_MainListView
import V.DiviseParSections.App.Shared.Repository.A.Base.CentralFacade
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_AdminAppPanelControleur(val  aCentralFacade: CentralFacade, ) : ViewModel()

@Composable
fun View_AdminAppPanelControleur(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AdminAppPanelControleur = koinInject(),
) {
    val defaultGeneratedCompt = Z_AppCompt(
        nom = "Abdelwahab"
    ).apply {
        nomsMutableTags = addStringAuNomsMutableTags("Abdelwahab").joinToString(",")
    }


    Box(modifier = modifier.fillMaxSize()) {
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

        FloatingActionButton(
            onClick = {
                viewModel.aCentralFacade.set.addAuRepoM9AppComptParFacade(defaultGeneratedCompt)
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
    }
}


