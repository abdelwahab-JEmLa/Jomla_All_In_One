package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._14_TransactionStatue
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FERME(
    coroutineScope: CoroutineScope,
    relatedClients: B_ClientDataBase?,
    viewModel: ViewModel_MapClients_App2FragID1,
    onDismiss: () -> Unit,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
    context: Context,
) {
    val FERME = B_ClientDataBase.DernierEtatAAffiche.FERME
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = FERME
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
            //----------------------------------------------------------------------------------------/
            _01_Upsert_013_Acheteurs(
                repositorysModel,
                clientId,
                _14_TransactionStatue.EtateTransaction.FERME,
                relatedClients?.nom!!,
                viewModel.repo_01_VentsHistoriquesDataBase
            )
            //----------------------------------------------------------------------------------------/
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    FERME.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    FERME.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = FERME.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(FERME.nomArabe)
        }
    }
}

@Composable
fun AVEC_MARCHANDISE(
    coroutineScope: CoroutineScope,
    relatedClients: B_ClientDataBase?,
    viewModel: ViewModel_MapClients_App2FragID1,
    onDismiss: () -> Unit,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
    context: Context,
) {
    val AVEC_MARCHANDISE = B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                relatedClients?.actuelleEtat = AVEC_MARCHANDISE
                viewModel.updateData(relatedClients!!)
                onDismiss()
            }
            //----------------------------------------------------------------------------------------/
            _01_Upsert_013_Acheteurs(
                repositorysModel,
                clientId,
                _14_TransactionStatue.EtateTransaction.AVEC_MARCHANDISE,
                relatedClients?.nom!!,
                viewModel.repo_01_VentsHistoriquesDataBase
            )
            //----------------------------------------------------------------------------------------/
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    AVEC_MARCHANDISE.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    AVEC_MARCHANDISE.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = AVEC_MARCHANDISE.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(AVEC_MARCHANDISE.nomArabe)
        }
    }
}

@Composable
fun ACHETEUR_NON_DISPO(
    coroutineScope: CoroutineScope,
    selectedMarker: Marker?,
    relatedClients: B_ClientDataBase?,
    viewModel: ViewModel_MapClients_App2FragID1,
    onDismiss: () -> Unit,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
    context: Context,
) {
    val ACHETEUR_NON_DISPO = B_ClientDataBase.DernierEtatAAffiche.ACHETEUR_NON_DISPO
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                Log.d(
                    "MarkerStatusDialog",
                    "Setting client ${selectedMarker?.id} to state: ACHETEUR_NON_DISPO"
                )
                relatedClients?.actuelleEtat = ACHETEUR_NON_DISPO
                viewModel.updateData(relatedClients!!)
                onDismiss()

                //----------------------------------------------------------------------------------------/
                _01_Upsert_013_Acheteurs(
                    repositorysModel,
                    clientId,
                    _14_TransactionStatue.EtateTransaction.ACHETEUR_NON_DISPO,
                    relatedClients.nom,
                    viewModel.repo_01_VentsHistoriquesDataBase
                )
                //----------------------------------------------------------------------------------------/
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    ACHETEUR_NON_DISPO.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    ACHETEUR_NON_DISPO.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = ACHETEUR_NON_DISPO.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(ACHETEUR_NON_DISPO.nomArabe)
        }
    }
}

@Composable
fun CommandButton(
    coroutineScope: CoroutineScope,
    existingBonAchat: _1_3_TransactionCommercial?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
    ceComptVendeurInsertBonsAchatAuPeriodID: Long?,
    selectedMarker: Marker,
    viewModel: ViewModel_MapClients_App2FragID1,
    onUpdateLongAppSetting: () -> Unit,
    onDismiss: () -> Unit,
    relatedClients: B_ClientDataBase?,
    context: Context,
) {
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                if (existingBonAchat != null) {
                    // Update the existing BonAchat
                    val updatedBonAchat = existingBonAchat.copy(
                        etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        heurDebutInString = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date())
                    )

                    // Use upsert to update the existing record
                    repositorysModel.repository_1_3_TransactionCommercial.upsertUneDataEtReturnVID(
                        updatedBonAchat
                    ) { vid ->
                        repositorysModel.activeId_1_3_BonAchat.value = vid
                    }
                } else {
                    // Create a new BonAchat if none exists
                    repositorysModel.repository_1_3_TransactionCommercial.addDataAndReturneItVID(
                        _1_3_TransactionCommercial(
                            clientAcheteurID = clientId,
                            parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                            etateActuellementEst = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                            heurDebutInString = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(Date())
                        )
                    ) { newVid ->
                        // Update the MutableStateFlow with the new value
                        repositorysModel.activeId_1_3_BonAchat.value = newVid
                    }
                }

                val selectedMarkedID = selectedMarker.id.toLong()
                viewModel.updateLongAppSetting(selectedMarkedID)

                // Finish and dismiss the dialog
                onUpdateLongAppSetting()
                onDismiss()

                //----------------------------------------------------------------------------------------/
                _01_Upsert_013_Acheteurs(
                    repositorysModel,
                    clientId,
                    _14_TransactionStatue.EtateTransaction.COMMANDE_LENCE,
                    relatedClients?.nom!!,
                    viewModel.repo_01_VentsHistoriquesDataBase
                )
                //----------------------------------------------------------------------------------------/
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Mode Commande",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Mode Commande")
        }
    }
}
