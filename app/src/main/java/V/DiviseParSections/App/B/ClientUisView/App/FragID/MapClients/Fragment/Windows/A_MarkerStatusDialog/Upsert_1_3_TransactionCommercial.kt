package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial.Companion.addOrIgnorTagCeBonEstOuvertPourComptsIds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun upsertLenceCommandeRepoGroupedProtoAvanJuin3(
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
            heurDebutInString = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date())
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
                heurDebutInString = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(Date())
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
