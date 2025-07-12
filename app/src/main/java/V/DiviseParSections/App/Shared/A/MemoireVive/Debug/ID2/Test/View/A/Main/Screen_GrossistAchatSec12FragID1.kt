package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui.Dialog_Filter_Client
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.List_GroupeAchatProduit
import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen_GrossistAchatSec12FragID1(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            modifier =
                Modifier.getSemanticsTag(
                    viewModel.aCentralFacade.repositorysMainGetter
                        .repo11AchatOperation
                        .sourceDatas, "repo11AchatOperation"
                ),
            title = { Text("Grossist Achat") },
            actions = {
                IconButton(onClick = { viewModel.updateShowMenu(!uiState.showMenu) }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }

                DropdownMenu(
                    expanded = uiState.showMenu,
                    onDismissRequest = { viewModel.updateShowMenu(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text("Filtrer par Client") },
                        onClick = {
                            viewModel.updateShowDialog(true)
                            viewModel.updateShowMenu(false)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Effacer Filtre") },
                        onClick = {
                            viewModel.clearClientFilter()
                            viewModel.updateShowMenu(false)
                        }
                    )
                }
            }
        )

        List_GroupeAchatProduit(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            viewModel = viewModel
        )
    }

    if (uiState.showDialog) {
        Dialog_Filter_Client(
            uiState = uiState,
            viewModel = viewModel,
            onDismiss = { viewModel.updateShowDialog(false) }
        )
    }
}

