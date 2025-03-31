package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.BProto_ClientsDataBaseButton

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ButFun_1_populateB_ClientDataBaseParSonAncien(
    viewModel: ViewModel_BProto_ClientsDataBase = koinViewModel(),
    onProgressUpdate: (Float) -> Unit,
    nameFunciotn: String
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showConfirmationDialog = true },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    nameFunciotn,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(nameFunciotn) },
            text = {
                Text(
                    text = """
                             $nameFunciotn
                                 fun populateB_ClientDataBaseParSonAncien() {
        viewModelScope.launch {
            try {
                // Get data from old database structure
                val ancienDataList = getAncienDataBase()

                if (ancienDataList.isEmpty()) {
                    Log.d("ViewModel_BProto", "No ancien data found to migrate")
                    return@launch
                }

                // Transform old data structure to new structure
                val newDataList = ancienDataList.map { ancienData ->
                    Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase(
                        id = ancienData.id,
                        nom = ancienData.nom,
                        numTelephone = ancienData.statueDeBase.numTelephone,
                        couleur = ancienData.statueDeBase.couleur,
                        bonDuClientsSu = ancienData.statueDeBase.bonDuClientsSu,
                        currentCreditBalance = ancienData.statueDeBase.currentCreditBalance,
                        positionDonClientsList = ancienData.statueDeBase.positionDonClientsList,
                        cUnClientTemporaire = ancienData.statueDeBase.cUnClientTemporaire,
                        auFilterFAB = ancienData.statueDeBase.auFilterFAB,
                        typeDeSonMagasine = when (ancienData.statueDeBase.typeDeSonMagasine) {
                            B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                            B_ClientsDataBase.StatueDeBase.TypeDeSonMagasine.AlIMENTATION_GENERALE ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.AlIMENTATION_GENERALE
                            else ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.TypeDeSonMagasine.ATAYAT_MOUKASSARAT
                        },
                        clientTypeMode = when (ancienData.etatesMutable.clientTypeMode) {
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.NEVEAU ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.NEVEAU
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.ANCIEN ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.ANCIEN
                            B_ClientsDataBase.EtatesMutable.ClientTypeMode.EVITE ->
                                Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase.ClientTypeMode.EVITE
                        },
                        latitude = ancienData.gpsLocation.latitude,
                        longitude = ancienData.gpsLocation.longitude,
                        title = ancienData.gpsLocation.title,
                        snippet = ancienData.gpsLocation.snippet,
                        actuelleEtat = mapActuelleEtat(ancienData.gpsLocation.actuelleEtat)
                    )
                }

                // Create a SnapshotStateList from the converted data
                val snapshotList = androidx.compose.runtime.snapshots.SnapshotStateList<Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase>()
                snapshotList.addAll(newDataList)

                // Update repository with new data structure
                mainRepo.updateMultiDatas(snapshotList)

                Log.d("ViewModel_BProto", "Successfully migrated {newDataList.size} clients from ancien database")
            } catch (e: Exception) {
                Log.e("ViewModel_BProto", "Error migrating ancien database: {e.message}", e)
            }
        }
    }

                    """.trimIndent(),
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.1f))
                        .padding(8.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        launch {
                            viewModel.mainRepo.progressRepo.collect { progress ->
                                onProgressUpdate(progress)
                            }
                        }

                        viewModel.populateB_ClientDataBaseParSonAncien()

                        showConfirmationDialog = false
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
