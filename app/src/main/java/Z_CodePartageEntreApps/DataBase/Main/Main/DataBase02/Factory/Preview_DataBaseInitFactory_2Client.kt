package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.Ui.LoadingScreen
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
fun Preview_DataBaseInitFactory_2Client() {
    Main_DataBaseInitFactory_2Client()
}

@Composable
fun Main_DataBaseInitFactory_2Client(
    aCentralFacade: ACentralFacade = koinInject(),
) {
    val loadingProgress = aCentralFacade.repositorysMainGetter.loadingProgress ?: 0f

    when {
        loadingProgress < 1.0f -> LoadingScreen(loadingProgress)
        else -> MainScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    aCentralFacade: ACentralFacade = koinInject(),
) {
    var isLoading by remember { mutableStateOf(false) }

    val repoDatas = aCentralFacade.repositorysMainGetter.repo2Client.datasValue

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            var showMenu by remember { mutableStateOf(false) }
            var safeCountClick by remember { mutableIntStateOf(0) }

            TopAppBar(
                modifier = Modifier,
                title = {
                    Text("Datas",
                        modifier = Modifier.getSemanticsTag(
                            repoDatas,""
                        )) },
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
                        Item_1_Menu(
                            title = "export",
                            isLoading = isLoading,
                        ) {
                            showMenu = false
                            safeCountClick = 0
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun Item_1_Menu(
    title: String,
    isLoading: Boolean = false,
    aCentralFacade: ACentralFacade = koinInject(),
    onClick_TO_Close_Menu: () -> Unit,
) {
    var safeCountClick by remember { mutableIntStateOf(0) }
    var isEditingRef by remember { mutableStateOf(false) }
    var refValue by remember { mutableStateOf("07_16") }

    val title_Ac_Securite = when {
        safeCountClick == 0 -> title
        else -> "Es-tu sûr de faire cela ?"
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
            text = {
                if (isEditingRef) {
                    Row {
                        OutlinedTextField(
                            value = refValue,
                            onValueChange = { refValue = it },
                            label = { Text("Référence") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                isEditingRef = false
                                if (safeCountClick > 0) {
                                    onClick_TO_Close_Menu()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Confirmer",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    Text(title_Ac_Securite)
                }
            },
            onClick = {
                if (isLoading) return@DropdownMenuItem

                if (safeCountClick == 0) {
                    safeCountClick++
                } else {
                    if (isEditingRef) {
                        onClick_TO_Close_Menu()
                    } else {
                        isEditingRef = true
                    }
                }
            }
        )
    }
}

fun batchFireBaseUpdateArticlesBasesStatsTablet(datas: List<M2Client>) {
    if (datas.isEmpty()) {
        Log.w("batchFireBaseUpdate", "No data to update")
        return
    }


        val updates = mutableMapOf<String, Any>()

        datas.forEach { data ->
        }
}

