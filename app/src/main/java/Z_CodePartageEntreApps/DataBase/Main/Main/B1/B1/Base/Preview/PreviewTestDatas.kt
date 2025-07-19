package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.FilterQuery
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.MainList
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.EmptyDataMessage
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.Ui.GenerationProgressScreen
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.A.Main.B1CouleurOuGoutProduitDataBaseTestDatasViewModel
import Z_CodePartageEntreApps.Ui.LoadingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    datas: List<M3CouleurProduitInfos>,
    uiState: B1CouleurOuGoutProduitDataBaseTestDatasViewModel.UiState,
    onRefresh: () -> Unit
) {
    val repository = viewModel.b1CouleurOuGoutProduitDataBaseRepository

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            SearchBar(repository)

            // Only show filtered count when text filter is active
            if (repository.filterQuery.value == FilterQuery.SearchText) {
                Text(
                    "Total: ${datas.size} | Filtered: ${repository.datasValueFiltered.size}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (uiState.progressCount > 0) Text(
                "Generated ${uiState.progressCount} variants",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (datas.isEmpty()) EmptyDataMessage() else MainList(repository)
        }
        MainOptions(repository, Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp), onRefresh)
    }
}

@Composable
private fun SearchBar(repository: Repo03CouleurProduitInfos) {
    var searchText by remember { mutableStateOf("") }
    val isSearchActive = repository.filterQuery.value == FilterQuery.SearchText

    OutlinedTextField(
        value = searchText,
        onValueChange = {
            searchText = it
            if (it.isNotBlank()) repository.setFilterTextSearch(it) else repository.clearFilters()
        },
        label = { Text("Search...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        trailingIcon = {
            if (searchText.isNotEmpty()) IconButton({ searchText = ""; repository.clearFilters() })
            { Icon(Icons.Default.Clear, null) }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        singleLine = true
    )

    if (isSearchActive) Text(
        "Searching: \"${repository.filterTextSearch.value}\"",
        modifier = Modifier.padding(bottom = 4.dp),
        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun MainOptions(
    repository: Repo03CouleurProduitInfos,
    modifier: Modifier,
    onRefresh: () -> Unit
) {
    var refreshClickCount by remember { mutableIntStateOf(0) }
    var showSearchIcon by remember { mutableStateOf(true) }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FloatingActionButton(
            onClick = {
                showSearchIcon = !showSearchIcon; if (showSearchIcon) repository.clearFilters()
            },
            modifier = Modifier.size(48.dp)
        ) { Icon(if (showSearchIcon) Icons.Default.Search else Icons.Default.Clear, null) }

        FloatingActionButton(
            onClick = {
                refreshClickCount++; if (refreshClickCount >= 2) {
                onRefresh(); refreshClickCount = 0
            }
            },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                if (refreshClickCount == 1)
                    Icons.Default.Warning
                else
                    Icons.Default.Refresh,
                null
            )
        }

        if (refreshClickCount == 1) Text(
            "Click again", style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(4.dp)
        )
    }
}
