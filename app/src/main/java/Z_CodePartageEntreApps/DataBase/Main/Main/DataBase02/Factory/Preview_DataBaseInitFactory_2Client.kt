package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
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
    repo2Client: Repo2Client = aCentralFacade.repositorysMainGetter.repo2Client,
) {
    var isLoading by remember { mutableStateOf(false) }

    val repoDatas = repo2Client.datasValue

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            var showMenu by remember { mutableStateOf(false) }
            TopAppBar(
                modifier = Modifier,
                title = {
                    Text(
                        "Datas",
                        modifier = Modifier.getSemanticsTag(
                            repoDatas, ""
                        )
                    )
                },
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
                            repo2Client = repo2Client,
                            title = "export",
                            isLoading = isLoading,
                        ) {
                            showMenu = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Item_1_Menu(
    repo2Client: Repo2Client,
    title: String,
    isLoading: Boolean = false,
    onClick_TO_Close_Menu: () -> Unit,
) {
    val datas = repo2Client.datasValue
    var safeCountClick by remember { mutableIntStateOf(0) }

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
                Text(
                    title_Ac_Securite,
                    modifier = Modifier
                        .getSemanticsTag(datas, "")
                )
            },
            onClick = {
                if (isLoading) return@DropdownMenuItem
                if (safeCountClick == 0) {
                    safeCountClick++
                } else {
                    M2Client.safeRemoveRef {
                        batchFireBaseUpdate(datas)
                        onClick_TO_Close_Menu()
                    }
                    safeCountClick = 0
                }
            }
        )
    }
}
fun batchFireBaseUpdate(datas: List<M2Client>) {
    if (datas.isEmpty()) {
        Log.w("batchFireBaseUpdate", "No data to update")
        return
    }

    val updates = mutableMapOf<String, Any>()
    datas.forEach { data ->
        val preparedData = data.with_Trigger_RealTime()
        val clientPath = preparedData.keyID

        updates[clientPath] = mapOf(
            "keyID" to preparedData.keyID,
            "nom" to preparedData.nom,
            "numTelephone" to preparedData.numTelephone,
            "currentCreditBalance" to preparedData.currentCreditBalance,
            "dernierTimeTampsSynchronisationAvecFireBase" to preparedData.dernierTimeTampsSynchronisationAvecFireBase,
            "creationTimestamps" to preparedData.creationTimestamps,
            "couleur" to preparedData.couleur,
            "bonDuClientsSu" to preparedData.bonDuClientsSu,
            "positionDonClientsList" to preparedData.positionDonClientsList,
            "cUnClientTemporaire" to preparedData.cUnClientTemporaire,
            "auFilterFAB" to preparedData.auFilterFAB,
            "typeDeSonMagasine" to preparedData.typeDeSonMagasine.name,
            "clientTypeMode" to preparedData.clientTypeMode.name,
            "caMarqueGpsEstOuvert" to preparedData.caMarqueGpsEstOuvert,
            "latitude" to preparedData.latitude,
            "longitude" to preparedData.longitude,
            "title" to preparedData.title,
            "snippet" to preparedData.snippet,
            "actuelleEtat" to preparedData.actuelleEtat.name,
            "edite_Exact_Gps_est_fait" to preparedData.edite_Exact_Gps_est_fait,
            "tagCeBonEstOuvertPourComptsIds" to preparedData.tagCeBonEstOuvertPourComptsIds,
            "id" to preparedData.id,
            "keyByParent" to preparedData.keyByParent,
            "bsonObjectId" to preparedData.bsonObjectId
        )
    }

    // Execute the batch update using the correct Firebase reference
    M2Client.ref.updateChildren(updates)
        .addOnSuccessListener {
            Log.d("batchFireBaseUpdate", "Successfully updated ${datas.size} clients")
        }
        .addOnFailureListener { exception ->
            Log.e("batchFireBaseUpdate", "Failed to update clients", exception)
        }
}
