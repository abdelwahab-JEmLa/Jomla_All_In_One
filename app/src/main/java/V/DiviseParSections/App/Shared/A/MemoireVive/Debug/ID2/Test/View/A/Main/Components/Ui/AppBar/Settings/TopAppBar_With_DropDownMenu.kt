package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui.AppBar.Settings

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBar_With_DropDownMenu(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    uiState: GrossistAchatSec12FragID1_ViewModel.UiState
) {
    val repositorysMainGetter = viewModel.aCentralFacade.repositorysMainGetter
    val data = repositorysMainGetter.repo10OperationVentCouleur.datasValue
    TopAppBar(
        modifier =
            Modifier
                .getSemanticsTag(
                    viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode,
                    "currentActiveFocuced_M14VentPeriode"
                )
                .getSemanticsTag(
                    data.map { it.parent_M14VentPeriod_KeyId },
                    "repo10OperationVentCouleur"
                )
                .getSemanticsTag(
                    repositorysMainGetter.repo11AchatOperation.sourceDatas,
                    "repo11AchatOperation"
                )
                .height(30.dp),
        title = {
            Text(
                "Grossist Achat",
                fontSize = 14.sp
            )
        },
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
}
