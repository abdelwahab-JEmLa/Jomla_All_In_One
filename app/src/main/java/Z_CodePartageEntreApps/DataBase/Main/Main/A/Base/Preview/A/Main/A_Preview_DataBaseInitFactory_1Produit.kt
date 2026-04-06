package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.A.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.Item_2_Menu
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.OldDataBase_M1
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.OldDataBase_M1.Companion.get_old_Datas
import Z_CodePartageEntreApps.Ui.LoadingScreen
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Preview
@Composable
fun Preview_DataBaseInitFactory_1Produit() {
    Main_DataBaseInitFactory_1Produit()
}

@Composable
fun Main_DataBaseInitFactory_1Produit(
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
    var old_Datas by remember { mutableStateOf(emptyList<OldDataBase_M1>()) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            old_Datas = get_old_Datas()
        } catch (e: Exception) {
            Log.e("MainScreen", "Error loading data: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    val repoDatas = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue
    val currentProducts = aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue
    val new_Edited_Datas = old_Datas.mapNotNull { old ->
        val m1Produit_IN_New = currentProducts.find { it.id.toInt() == old.idArticle }
        m1Produit_IN_New?.copy(quantite_Boit_Par_Carton = old.nmbrCaron)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            var showMenu by remember { mutableStateOf(false) }
            var safeCountClick by remember { mutableIntStateOf(0) }

            val quantite_Boit_Par_Carton = repoDatas.filter {
                it.quantite_Boit_Par_Carton > 1
            }

            TopAppBar(
                modifier = Modifier
                    .getSemanticsTag(quantite_Boit_Par_Carton, "quantite_Boit_Par_Carton", 3)
                    .getSemanticsTag(new_Edited_Datas.find { it.nom.contains("mor") }, "liy"),
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
                                "Delete Ref" else "Es-tu sûr de supprimer tout ?"
                        DropdownMenuItem(
                            text = { Text(title) },
                            onClick = {
                                if (safeCountClick == 0)
                                    safeCountClick++
                                else {
                                    M01Produit.safe_Remove_DataBase_Ref()
                                    showMenu = false
                                    safeCountClick = 0
                                }
                            }
                        )
                        Item_2_Menu(
                            "Migrer quantité carton",
                            isLoading = isLoading,
                            old_Datas = old_Datas,
                        ) {
                            showMenu = false
                            safeCountClick = 0
                        }
                        Item_3_Menu(
                            title = "export",
                            isLoading = isLoading,
                            dataToExport = new_Edited_Datas
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
