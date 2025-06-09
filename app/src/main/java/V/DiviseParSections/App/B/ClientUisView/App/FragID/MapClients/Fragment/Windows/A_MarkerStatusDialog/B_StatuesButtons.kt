package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial.Companion.addOrIgnorTagCeBonEstOuvertPourComptsIds
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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


@Composable
fun C3_TransactionCommercial.EtateActuellementEst.Button(
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
                upsertLenceCommandeRepoGroupedProtoAvanJuin3(
                    viewModel,
                    clientId,
                    Etate
                )
                val data = viewModel.groupeRepositorysProtoAvJuin3
                    .repositorys_Model
                    .c3_BonAchate_Repository
                    .getOuvert_1_3_TransactionCommercial()

                viewModel.groupeRepositorysProtoAvJuin3
                    .upsertUneDataEtReturnVID(
                        data?.copy(tagCeBonEstOuvertPourComptsIds = "false")
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



fun upsertLenceAutresStatesRepoGroupedProtoAvanJuin3(
    viewModel: ViewModel_MapClients_App2FragID1,
    relatedClientID: Long,
    newEtate: C3_TransactionCommercial.EtateActuellementEst,
) {
    val _0_0_HeadOfRepositorys_Repository = viewModel.groupeRepositorysProtoAvJuin3

    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (relatedClientID)
    }

    val repositorysModel =
        _0_0_HeadOfRepositorys_Repository.repositorys_Model

    val activeIdDeA5Vendeur = repositorysModel.activeIdDeA5Vendeur
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == activeIdDeA5Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L

    val existingBonAchat = viewModel.c3_BonAchate_List.find {
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
                && it.etateActuellementEst == newEtate
    }

    if (existingBonAchat != null) {
        val updatedTags = addOrIgnorTagCeBonEstOuvertPourComptsIds(existingBonAchat, activeIdDeA5Vendeur, existingBonAchat)

        val updatedBonAchat = existingBonAchat.copy(
            tagCeBonEstOuvertPourComptsIds = updatedTags,
            timestamps = System.currentTimeMillis(),
           /* heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())     */
        )
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            updatedBonAchat
        ) { vid ->
            repositorysModel.activeVId_C3_BonAchate_Repository.value = updatedBonAchat.vid
        }

    } else {
        viewModel.groupeRepositorysProtoAvJuin3.upsertUneDataEtReturnVID(
            C3_TransactionCommercial(
                tagCeBonEstOuvertPourComptsIds = activeIdDeA5Vendeur.toString(),
                clientAcheteurID = clientId,
                nomClientConcerned = relatedClients?.nom!!,
                parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                etateActuellementEst = newEtate,
               /* heurDebutInString = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date())   */
            )
        ) { vid ->
            if (newEtate == C3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == C3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) repositorysModel.activeVId_C3_BonAchate_Repository.value = 0
            else
                repositorysModel.activeVId_C3_BonAchate_Repository.value = vid
        }

    }
}
