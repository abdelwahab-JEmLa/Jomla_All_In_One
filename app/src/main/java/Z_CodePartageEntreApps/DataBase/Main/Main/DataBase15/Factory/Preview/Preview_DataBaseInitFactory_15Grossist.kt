package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Old.DataBase.OldDataBase15Grossist
import Z_CodePartageEntreApps.Ui.LoadingScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    data class UiState(
        val oldDataBase: List<OldDataBase15Grossist> = emptyList(),
        var safeCountClick: Int = 0,
        val dropDown_I2_safeCountClick: Int = 0,
        var showMenu: Boolean = false,
    )

    val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val snapshot = OldDataBase15Grossist.ref.get().await()
                val oldDataList = mutableListOf<OldDataBase15Grossist>()

                if (snapshot.exists()) {
                    snapshot.children.forEach { child ->
                        val oldData = child.getValue(OldDataBase15Grossist::class.java)
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


    fun updateSafe() {
        _uiState.value = _uiState.value.copy(safeCountClick = _uiState.value.safeCountClick + 1)
    }

    fun active_ShowMenu() {
        _uiState.value = _uiState.value
            .copy(showMenu = true)
    }

    fun desactive_ShowMenu() {
        _uiState.value = _uiState.value
            .copy(showMenu = false)
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
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(Modifier.padding(top = 20.dp))
            Top_App_Bar_With_DropdownMenu(viewModel)
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Top_App_Bar_With_DropdownMenu(viewModel: Preview_DataBaseInitFactory_15Grossist) {
    val datas = viewModel.aCentralFacade.repositorysMainGetter.repo15Grossist.datasValue
    TopAppBar(
        modifier = Modifier.getSemanticsTag(datas, "datas"),
        title = { Text("15Grossist") },
        actions = {
            IconButton(onClick = {
                viewModel.active_ShowMenu()
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }
            val uiState by viewModel.uiState.collectAsState()
            DropdownMenu(
                expanded = uiState.showMenu,
                onDismissRequest = { uiState.showMenu = false }
            ) {
                I1_DropdownMenuItem(viewModel)
                I3_EcraseMigre_DropdownMenuItem(viewModel)
            }
        }
    )
}

@Composable
private fun I3_EcraseMigre_DropdownMenuItem(viewModel: Preview_DataBaseInitFactory_15Grossist) {
    val uiState by viewModel.uiState.collectAsState()
    val nomFn = "EcraseMigre"
    val title =
        if (uiState.dropDown_I2_safeCountClick == 0) nomFn else "esque t sure de $nomFn tout "

    val oldDataBase = uiState.oldDataBase
    Card(
        modifier = Modifier
            .getSemanticsTag(oldDataBase, "oldDataBase")
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = { Text(title) },
            onClick = {
                if (uiState.dropDown_I2_safeCountClick == 0)
                    viewModel._uiState.value = viewModel._uiState.value.copy(
                        dropDown_I2_safeCountClick = viewModel._uiState.value.dropDown_I2_safeCountClick + 1
                    )
                else {
                    val repositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter

                    //   repositorysMainSetter.repo15Grossist_deleteMulti()

                    oldDataBase.map { old ->
                        repositorysMainSetter.repo15Grossist_add_New(
                            M15Grossist.get_default().copy(
                                nom = old.nomSupplierSu,
                                couleur_In_Str = old.couleurSu
                            )
                        )
                    }

                    viewModel._uiState.value =
                        viewModel._uiState.value.copy(dropDown_I2_safeCountClick = 0)
                    viewModel.desactive_ShowMenu()
                }
            }
        )
    }
}

@Composable
private fun I1_DropdownMenuItem(viewModel: Preview_DataBaseInitFactory_15Grossist) {
    val uiState by viewModel.uiState.collectAsState()
    val title =
        if (uiState.safeCountClick == 0)
            "Delete Ref" else "esque t sure de supp tout "
    DropdownMenuItem(
        text = { Text(title) },
        onClick = {
            if (uiState.safeCountClick == 0)
                viewModel.updateSafe()
            else {
                M15Grossist.safeRemoveRef()

                viewModel._uiState.value = viewModel._uiState.value.copy(
                    safeCountClick = 0
                )
                viewModel.desactive_ShowMenu()
            }
        }
    )
}
