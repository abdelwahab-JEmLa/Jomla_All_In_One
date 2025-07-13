package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.OldDataBase_M1.Companion.get_old_Datas
import Z_CodePartageEntreApps.Ui.LoadingScreen
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject

@Preview
@Composable
private fun Preview_DataBaseInitFactory_1Produit() {
    Main_DataBaseInitFactory_1Produit()
}

@Composable
private fun Main_DataBaseInitFactory_1Produit(
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
                                "Delete Ref" else "Es-tu sûr de supprimer tout ?"
                        DropdownMenuItem(
                            text = { Text(title) },
                            onClick = {
                                if (safeCountClick == 0)
                                    safeCountClick++
                                else {
                                    ArticlesBasesStatsTable.safe_Remove_DataBase_Ref()
                                    showMenu = false
                                    safeCountClick = 0 // Reset counter
                                }
                            }
                        )
                        Item_2_Menu("Migrer quantité carton") {
                            showMenu = false
                            safeCountClick = 0 // Reset counter when menu closes
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
    var old_Datas by remember { mutableStateOf(emptyList<OldDataBase_M1>()) }
    var new_Datas by remember { mutableStateOf(emptyList<ArticlesBasesStatsTable>()) }
    var isLoading by remember { mutableStateOf(false) }

    // Fixed: Use LaunchedEffect instead of the typo "LunchedEffecte"
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            old_Datas = get_old_Datas()
            new_Datas = getData_AvecUpdated_Carton(old_Datas, aCentralFacade)
        } catch (e: Exception) {
            Log.e("Item_2_Menu", "Error loading old data: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    val title_Ac_Securite = when {
        isLoading -> "Chargement..."
        safeCountClick == 0 -> title
        else -> "Es-tu sûr de faire cela ?"
    }

    Card(
        modifier = Modifier
            .getSemanticsTag(old_Datas,"")
            .getSemanticsTag(new_Datas,"")
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
            text = { Text(title_Ac_Securite) },
            onClick = {
                if (isLoading) return@DropdownMenuItem

                if (safeCountClick == 0) {
                    safeCountClick++
                } else {
                    if (new_Datas.isNotEmpty()) {
                        batchFireBaseUpdateArticlesBasesStatsTable(new_Datas)
                    }
                    onClick_TO_Close_Menu()
                }
            }
        )
    }
}

// Fixed: Return the updated data instead of void, and made synchronous
fun getData_AvecUpdated_Carton(
    oldDatas: List<OldDataBase_M1>,
    aCentralFacade: ACentralFacade
): List<ArticlesBasesStatsTable> {
    return try {
        val updatedProducts = mutableListOf<ArticlesBasesStatsTable>()

        oldDatas.forEach { old ->
            val m1Produit_IN_New =
                aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue
                    .find { it.id == old.id }

            if (m1Produit_IN_New != null) {
                val updatedProduct = m1Produit_IN_New.copy(
                    quantite_Boit_Par_Carton = old.nmbrCaron
                )
                updatedProducts.add(updatedProduct)
            }
        }

        updatedProducts
    } catch (e: Exception) {
        Log.e("getData_AvecUpdated_Carton", "Error updating carton data: ${e.message}", e)
        emptyList()
    }
}

// Fixed: Better error handling and logging
fun batchFireBaseUpdateArticlesBasesStatsTable(datas: List<ArticlesBasesStatsTable>) {
    if (datas.isEmpty()) {
        Log.w("batchFireBaseUpdate", "No data to update")
        return
    }

    val detachedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    detachedScope.launch {
        try {
            val updates = mutableMapOf<String, Any>()

            datas.forEach { data ->
                updates[data.keyID] = data.toFirebaseMap()
            }

            val firebaseRef = Firebase.database.getReference(
                "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase/A_ProduitInfos/07_13/Datas"
            )

            firebaseRef.updateChildren(updates).await()
            Log.d("batchFireBaseUpdate", "Successfully updated ${datas.size} items in Firebase")

        } catch (e: Exception) {
            Log.e("batchFireBaseUpdate", "Error updating Firebase: ${e.message}", e)
            // Consider showing user-friendly error message here
        }
    }
}
