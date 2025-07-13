package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.OldDataBase_M1.Companion.get_old_Datas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class ViewModel_DataBaseInitFactory_1Produit(
    val aCentralFacade: ACentralFacade,
) : ViewModel() {
    data class UiState(
        val value: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}

@Preview @Composable private fun Preview_DataBaseInitFactory_1Produit() { MainScreen() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    aCentralFacade: ACentralFacade = koinInject(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            var showMenu by remember { mutableStateOf(false) }
            var safeCountClick by remember { mutableIntStateOf(0) }

            val datasValue =
               aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue
            val quantite_Boit_Par_Carton = datasValue.filter {
                it.quantite_Boit_Par_Carton > 1
            }

            TopAppBar(
                modifier = Modifier
                    .getSemanticsTag(
                        quantite_Boit_Par_Carton, "quantite_Boit_Par_Carton"
                    ),
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
                        Item_2_Menu("Migre quanCarton") {
                            showMenu = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun Item_2_Menu(
    title: String,
    aCentralFacade: ACentralFacade = koinInject(),
    onClick_TO_Close_Menu: () -> Unit,
) {
    var safeCountClick by remember { mutableIntStateOf(0) }
    val title_Ac_Securite = if (safeCountClick == 0) title else "esque t sure de Ca !!! "
    val viewModelScope = rememberCoroutineScope()

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
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            text = { Text(title_Ac_Securite) },
            onClick = {
                if (safeCountClick == 0)
                    safeCountClick++
                else {
                    viewModelScope.launch {
                        val oldDatas = get_old_Datas()
                        oldDatas.forEach { old ->
                            val m1Produit_IN_New =
                                aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue
                                    .find { it.id == old.id }

                            if (m1Produit_IN_New != null) {
                                aCentralFacade.repositorysMainSetter.m1Produit_Update(
                                    m1Produit_IN_New.copy(
                                        quantite_Boit_Par_Carton = old.nmbrCaron
                                    )
                                )
                            }
                        }
                    }
                    onClick_TO_Close_Menu()
                }
            }
        )
    }
}
