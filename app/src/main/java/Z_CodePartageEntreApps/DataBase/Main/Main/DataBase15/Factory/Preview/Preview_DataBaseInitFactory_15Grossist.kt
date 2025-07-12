package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef
import V.DiviseParSections.App.Shared.Repository.Repo15.Repository.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Old.DataBase.Dialog_Old_DataBase
import Z_CodePartageEntreApps.Ui.LoadingScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel

class Preview_DataBaseInitFactory_15Grossist(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    data class Old_DataBase(
        val bonDuSupplierSu: String = "",
        val classmentSupplier: Int = 0,
        val couleurSu: String = "",
        val currentCreditBalance: Int = 0,
        val idSupplierSu: Int = 0,
        val ignoreItProdects: Boolean = false,
        val longTermCredit: Boolean = false,
        val nameInFrenche: String = "",
        val nomSupplierSu: String = "",
        val nomVocaleArabeDuSupplier: String = "",
        val supplierNameInFrenche: String = ""
    ) {
        companion object {
            val ref = centralRef.child("/AncienDataBase/M15Grossist/1_1")
        }
    }

    data class UiState(
        val affiche_Dialog_Old_DataBase: Boolean = false,
        val oldDataBase: List<Old_DataBase> = emptyList(),
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val snapshot = Old_DataBase.ref.get().await()
                val oldDataList = mutableListOf<Old_DataBase>()

                // Handle the case where the data might be a single object or a list
                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        val oldData = child.getValue(Old_DataBase::class.java)
                        oldData?.let { oldDataList.add(it) }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    oldDataBase = oldDataList
                )
            } catch (e: Exception) {
                // Handle error if needed
                _uiState.value = _uiState.value.copy(
                    oldDataBase = emptyList()
                )
            }
        }
    }

    fun showOldDataBaseDialog() {
        _uiState.value = _uiState.value.copy(affiche_Dialog_Old_DataBase = true)
    }

    fun hideOldDataBaseDialog() {
        _uiState.value = _uiState.value.copy(affiche_Dialog_Old_DataBase = false)
    }
}

@Preview
@Composable
private fun Preview_DataBaseInitFactory_15Grossist() {
    Main_DataBaseInitFactory_15Grossist()
}

@Composable
private fun Main_DataBaseInitFactory_15Grossist(
    viewModel: Preview_DataBaseInitFactory_15Grossist = koinViewModel()
) {
    val loadingProgress = viewModel.aCentralFacade.repositorysMainGetter.loadingProgress ?: 0f
    when {
        loadingProgress < 1.0f -> LoadingScreen(loadingProgress)
        else -> MainScreen(viewModel)
    }
}

@Composable
private fun MainScreen(
    viewModel: Preview_DataBaseInitFactory_15Grossist,
) {
    val uiState by viewModel.uiState.collectAsState()
    val datas = viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Top_App_Bar_With_DropdownMenu(viewModel)

            Box {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(datas) { item ->
                        Item_M15Grossist(
                            item = item,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
        if (uiState.affiche_Dialog_Old_DataBase) {
            Dialog_Old_DataBase(viewModel)
        }
    }
}

@Composable
private fun Item_M15Grossist(
    modifier: Modifier = Modifier,
    item: M15Grossist,
    viewModel: Preview_DataBaseInitFactory_15Grossist
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = item.get_DebugInfos(),
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = item.nom,
                modifier = Modifier.padding(16.dp)
            )

        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Top_App_Bar_With_DropdownMenu(viewModel: Preview_DataBaseInitFactory_15Grossist) {
    var showMenu by remember { mutableStateOf(false) }
    var safeCountClick by remember { mutableStateOf(0) }

    TopAppBar(
        title = { Text("15Grossist") },
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
                            M15Grossist.safeRemoveRef()
                            showMenu = false
                        }
                    }
                )

                DropdownMenuItem(
                    text = { Text("Show Old Database") },
                    onClick = {
                        viewModel.showOldDataBaseDialog()
                        showMenu = false
                    }
                )
            }
        }
    )
}
