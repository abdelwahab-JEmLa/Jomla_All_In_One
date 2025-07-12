package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.A.Main.Components.Ui.AppBar.Settings

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
        modifier = Modifier
            .getSemanticsTag(
                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode,
                "currentActiveFocuced_M14VentPeriode"
            )
            .getSemanticsTag(
                data.map { it.parent_M14VentPeriod_KeyId },
                "repo10OperationVentCouleur"
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

            val vents = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                .filtered_ListM10Vent_BY_Curr_M14VentPeriod
            val achats_Depuit_M11AchatOperation_List =
                viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation
                    .genere_Achats_Depuit_M11AchatOperation_List(
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                            .currentActiveFocuced_M14VentPeriode,
                        vents
                    )

            DropdownMenu(
                modifier = Modifier
                    .getSemanticsTag(
                        data = vents,
                        nomVal = "vents"
                    )
                    .getSemanticsTag(
                        achats_Depuit_M11AchatOperation_List,
                        "achats_Depuit_M11AchatOperation_List"
                    ),
                expanded = uiState.showMenu,
                onDismissRequest = { viewModel.updateShowMenu(false) }
            ) {
                //  dropdown item - Delete operations
                Repo11AchatOperation_deleteMulti(viewModel)

                //  dropdown item - Add operations
                Card(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    DropdownMenuItem(
                        modifier = Modifier
                            .getSemanticsTag(
                                data = achats_Depuit_M11AchatOperation_List,
                                nomVal = "achats_Depuit_M11AchatOperation_List"
                            ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = { Text("genere_Achats_Depuit_M11AchatOperation_List()") },
                        onClick = {
                            achats_Depuit_M11AchatOperation_List.map {
                                viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_add_New(
                                    it
                                )
                            }
                            viewModel.updateShowMenu(false)
                        }
                    )
                }


                // Third dropdown item - Filter operations
                Card(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        text = { Text("Filtrer par Client") },
                        onClick = {
                            viewModel.updateShowDialog(true)
                            viewModel.updateShowMenu(false)
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun Repo11AchatOperation_deleteMulti(viewModel: GrossistAchatSec12FragID1_ViewModel) {
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = { Text("Supprimer par Période") },
            onClick = {
                val keyID = viewModel.aCentralFacade
                    .focusedActiveValuesFacade.focusedValuesGetter.currentActiveFocuced_M14VentPeriode
                    ?.keyID

                // Fixed: Only delete if keyID is not null
                keyID?.let { nonNullKeyID ->
                    viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_deleteMulti(
                        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation
                            .datasValue.filter {
                                it.parent_M14VentPeriod_KeyID == nonNullKeyID
                            }
                    )
                }

                viewModel.updateShowMenu(false)
            }
        )
    }
}
