package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.MainList
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.EmptyDataMessage
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.GenerationProgressScreen
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.A.Main.B1CouleurOuGoutProduitDataBaseTestDatasViewModel
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.B1CouleurOuGoutProduitDataBaseRepository
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.FilterQuery
import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun B1CouleurOuGoutProduitDataBaseTestDatas(
    viewModel: B1CouleurOuGoutProduitDataBaseTestDatasViewModel = koinViewModel()
) {
    val datas = viewModel.b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val uiState by viewModel.uiState.collectAsState()
    val loadingProgress = viewModel.a_CentralDatasHandlerProtoJuin9.loadingProgress ?: 0f

    when {
        loadingProgress < 1.0f -> LoadingScreen(loadingProgress)
        uiState.isGeneratingData -> GenerationProgressScreen(
            uiState.progressCount,
            uiState.totalItems
        )

        else -> MainScreen(viewModel, datas, uiState, viewModel::genereDatasDepuitParent)
    }
}

@Composable
private fun MainScreen(
    viewModel: B1CouleurOuGoutProduitDataBaseTestDatasViewModel,
    datas: List<B1CouleurOuGoutProduitDataBase>,
    uiState: B1CouleurOuGoutProduitDataBaseTestDatasViewModel.UiState,
    onRefresh: () -> Unit
) {
    val repository = viewModel.b1CouleurOuGoutProduitDataBaseRepository

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Search bar
            SearchBar(repository = repository)

            Text("Total items: ${datas.size}", modifier = Modifier.padding(vertical = 8.dp))
            Text("Filtered items: ${repository.datasValueFiltred.size}", modifier = Modifier.padding(bottom = 8.dp))

            if (uiState.progressCount > 0) {
                Text(
                    "Generated ${uiState.progressCount} color variants",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (datas.isEmpty()) {
                EmptyDataMessage()
            } else {
                MainList(repository)
            }
        }

        MainOptions(
            repository,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onRefresh,
        )
    }
}

@Composable
private fun SearchBar(
    repository: B1CouleurOuGoutProduitDataBaseRepository
) {
    var searchText by remember { mutableStateOf("") }
    val currentFilter = repository.filterQuery.value
    val isSearchActive = currentFilter == FilterQuery.SearchText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
                if (newText.isNotBlank()) {
                    repository.setFilterTextSearch(newText)
                } else {
                    repository.clearFilters()
                }
            },
            label = { Text("Search colors, products, or images...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchText = ""
                            repository.clearFilters()
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }

    if (isSearchActive) {
        Text(
            text = "Active filter: Search for \"${repository.filterTextSearch.value}\"",
            modifier = Modifier.padding(bottom = 4.dp),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MainOptions(
    repository: B1CouleurOuGoutProduitDataBaseRepository,
    modifier: Modifier,
    onRefresh: () -> Unit
) {
    var refreshClickCount by remember { mutableStateOf(0) }
    var showSearchIcon by remember { mutableStateOf(true) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search toggle button
        FloatingActionButton(
            onClick = {
                showSearchIcon = !showSearchIcon
                if (showSearchIcon) {
                    // When switching to search mode, clear any existing filters
                    repository.clearFilters()
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                if (showSearchIcon) Icons.Default.Search else Icons.Default.Clear,
                contentDescription = if (showSearchIcon) "Show Search Options" else "Clear Filters"
            )
        }

        // Refresh button with double-click protection
        FloatingActionButton(
            onClick = {
                refreshClickCount++
                if (refreshClickCount >= 2) {
                    onRefresh()
                    refreshClickCount = 0
                }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = if (refreshClickCount == 0) "Click twice to generate data" else "Click once more to confirm"
            )
        }

        if (refreshClickCount == 1) {
            Text(
                text = "Click again to confirm",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
