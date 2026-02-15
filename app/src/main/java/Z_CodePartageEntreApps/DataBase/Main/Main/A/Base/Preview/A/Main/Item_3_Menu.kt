package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.Preview.A.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject

@Composable
fun Item_3_Menu(
    title: String,
    isLoading: Boolean = false,
    dataToExport: List<ArticlesBasesStatsTable> = emptyList(),
    aCentralFacade: ACentralFacade = koinInject(),
    repoM1Produit: RepoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
    onClick_TO_Close_Menu: () -> Unit,
) {
    var safeCountClick by remember { mutableIntStateOf(0) }
    var isEditingRef by remember { mutableStateOf(false) }
    var refValue by remember { mutableStateOf("07_16") }

    val title_Ac_Securite = when {
        safeCountClick == 0 -> title
        else -> "Es-tu sûr de faire cela ?"
    }

    val data = repoM1Produit.datasValue
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
                                    batchFireBaseUpdateArticlesBasesStatsTable(dataToExport, refValue)
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
                        // Confirm and launch batch with defined ref
                        batchFireBaseUpdateArticlesBasesStatsTable(dataToExport, refValue)
                        onClick_TO_Close_Menu()
                    } else {
                        // Switch to outlined field for editing ref
                        isEditingRef = true
                    }
                }
            }
        )
    }
}

fun batchFireBaseUpdateArticlesBasesStatsTable(datas: List<ArticlesBasesStatsTable>, ref: String) {
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
                "00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/AncienDataBase/A_ProduitInfos/$ref/Datas"
            )

            firebaseRef.updateChildren(updates).await()
            Log.d("batchFireBaseUpdate", "Successfully updated ${datas.size} items in Firebase")

        } catch (e: Exception) {
            Log.e("batchFireBaseUpdate", "Error updating Firebase: ${e.message}", e)
        }
    }
}
