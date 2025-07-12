package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel

class ViewModel_DataBaseInitFactory_1Produit(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    val mainRepo = aCentralFacade.repositorysMainGetter.repoM1ProduitInfos

    data class UiState(
        val value: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}

@Preview
@Composable
private fun Preview_DataBaseInitFactory_1Produit() {
    Main_DataBaseInitFactory_1Produit()
}

@Preview
@Composable
fun Main_DataBaseInitFactory_1Produit(
    viewModel: ViewModel_DataBaseInitFactory_1Produit = koinViewModel()
) {
    val loadingProgress = viewModel.aCentralFacade.repositorysMainGetter.loadingProgress ?: 0f
    when {
        loadingProgress < 1.0f -> LoadingScreen(loadingProgress)
        else -> MainScreen(viewModel)
    }
}

@Composable
private fun MainScreen(
    viewModel: ViewModel_DataBaseInitFactory_1Produit,
) {
    val datas = viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Top_App_Bar_With_DropdownMenu(viewModel)

            Box {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(datas) { produit ->
                        Item_M1Produit(
                            produit = produit,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Item_M1Produit(
    modifier: Modifier = Modifier,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModel_DataBaseInitFactory_1Produit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = produit.getDebugInfos(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Top_App_Bar_With_DropdownMenu(viewModel: ViewModel_DataBaseInitFactory_1Produit) {
    var showMenu by remember { mutableStateOf(false) }
    var safeCountClick by remember { mutableStateOf(0) }

    TopAppBar(
        title = { Text("1Produit") },
        actions = {
            IconButton(onClick = {
                showMenu = !showMenu
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                val title =
                    if (safeCountClick == 0)
                        "Delete Ref" else "esque t sure de supp tout "
                DropdownMenuItem(
                    text = { Text(title) },
                    onClick = {
                        if (safeCountClick == 0)
                            safeCountClick++
                        else {
                            ArticlesBasesStatsTable.safeRemoveRef()
                            showMenu = false
                        }
                    }
                )
            }
        }
    )
}
