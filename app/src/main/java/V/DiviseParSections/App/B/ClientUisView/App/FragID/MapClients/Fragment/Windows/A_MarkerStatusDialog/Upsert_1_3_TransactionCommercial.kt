package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun upsert_1_3_TransactionCommercial(
    viewModel: ViewModel_MapClients_App2FragID1,
    relatedClientID: Long,
    newEtate: _1_3_TransactionCommercial.EtateActuellementEst,
    cJustPourVoirPanie: Boolean = false,
): Unit {
    val _0_0_HeadOfRepositorys_Repository = viewModel.repo_0_0_HeadSQLRepositorys

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
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
                && it.etateActuellementEst == newEtate
    }

    if (existingBonAchat != null) {
        // Update the existing BonAchat
        val updatedBonAchat = existingBonAchat.copy(
            cJustPourVoirPanie = cJustPourVoirPanie,
        )
        viewModel.repo_0_0_HeadSQLRepositorys.upsertUneDataEtReturnVID(
            updatedBonAchat
        ) { vid ->
            repositorysModel.activeVId_1_3_TransactionCommercial.value = updatedBonAchat.vid
        }

    } else {
        viewModel.repo_0_0_HeadSQLRepositorys.upsertUneDataEtReturnVID(
            _1_3_TransactionCommercial(
                cJustPourVoirPanie = cJustPourVoirPanie,
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
            if (newEtate == _1_3_TransactionCommercial.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == _1_3_TransactionCommercial.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) repositorysModel.activeVId_1_3_TransactionCommercial.value = 0
            else
                repositorysModel.activeVId_1_3_TransactionCommercial.value = vid
        }

    }
}
