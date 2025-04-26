package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.content.Context
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

fun upsert_1_3_TransactionCommercial(
    viewModel: ViewModel_MapClients_App2FragID1,
    relatedClientID: Long,
    etateActuellementEst: _1_3_TransactionCommercial.EtateActuellementEst,
): Unit {
    val _0_0_HeadOfRepositorys_Repository = viewModel._0_0_HeadOfRepositorys_Repository

    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (relatedClientID)
    }
    val repositorysModel =
        _0_0_HeadOfRepositorys_Repository.repositorys_Model

    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L
    // Check if a BonAchat already exists for this client in the active period
    val existingBonAchat = viewModel.modelDatasSnapList_1_3_BonAchat.find {
        it.clientAcheteurID == clientId && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
    }

    if (existingBonAchat != null) {
        // Update the existing BonAchat
        val updatedBonAchat = existingBonAchat.copy(
            etateActuellementEst = etateActuellementEst,
            heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
        )
        viewModel._0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID(
            updatedBonAchat
        ) { vid ->
            repositorysModel.activeId_1_3_BonAchat.value = vid
        }

    } else {
        viewModel._0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID(
            _1_3_TransactionCommercial(
                clientAcheteurID = clientId,
                nomClientConcerned = relatedClients?.nom!!,
                parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                etateActuellementEst = etateActuellementEst,
                heurDebutInString = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date())
            )
        ) { vid ->
            repositorysModel.activeId_1_3_BonAchat.value = vid
        }

    }


}

@Composable
fun _1_3_TransactionCommercial.EtateActuellementEst.Button(
    coroutineScope: CoroutineScope,
    viewModel: ViewModel_MapClients_App2FragID1,
    clientId: Long,
    context: Context,
) {
    val Etate =
        this
    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                upsert_1_3_TransactionCommercial(
                    viewModel,
                    clientId,
                    Etate
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    Etate.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    Etate.color
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
                contentDescription = Etate.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(Etate.nomArabe)
        }
    }
}


@Composable
fun CommandButton(
    modifier: Modifier = Modifier,
    relatedClients: B_ClientDataBase?,
    coroutineScope: CoroutineScope,
    existingBonAchat: _1_3_TransactionCommercial?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
    ceComptVendeurInsertBonsAchatAuPeriodID: Long?,
    selectedMarker: Marker,
    viewModel: ViewModel_MapClients_App2FragID1,
    onUpdateLongAppSetting: () -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    initetateActuellementEst1: _1_3_TransactionCommercial.EtateActuellementEst,
    cJustPourVoirPanie: Boolean = false,
) {
    val etateActuellementEst1 =
        if (cJustPourVoirPanie) _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_VOIRE_PANIE_ARTICLES
        else
            initetateActuellementEst1

    FilledTonalButton(
        onClick = {
            coroutineScope.launch {
                if (existingBonAchat != null) {
                    // Update the existing BonAchat
                    val updatedBonAchat = existingBonAchat.copy(
                        etateActuellementEst = initetateActuellementEst1,

                        heurDebutInString = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date()),
                        cJustPourVoirPanie = cJustPourVoirPanie
                    )
                    viewModel._0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID(
                        updatedBonAchat
                    ) { vid ->
                        repositorysModel.activeId_1_3_BonAchat.value = vid
                    }

                } else {
                    viewModel._0_0_HeadOfRepositorys_Repository.upsertUneDataEtReturnVID(
                        _1_3_TransactionCommercial(
                            etateActuellementEst = initetateActuellementEst1,

                            clientAcheteurID = clientId,
                            nomClientConcerned = relatedClients?.nom!!,
                            parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                            heurDebutInString = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(Date()),
                            cJustPourVoirPanie = cJustPourVoirPanie
                        )
                    ) { vid ->
                        repositorysModel.activeId_1_3_BonAchat.value = vid
                    }

                }

                val selectedMarkedID = selectedMarker.id.toLong()
                viewModel.updateLongAppSetting(selectedMarkedID)

                // Finish and dismiss the dialog
                onUpdateLongAppSetting()
                onDismiss()

            }
        },
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst1.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    etateActuellementEst1.color
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
            Text(etateActuellementEst1.name)
        }
    }
}
