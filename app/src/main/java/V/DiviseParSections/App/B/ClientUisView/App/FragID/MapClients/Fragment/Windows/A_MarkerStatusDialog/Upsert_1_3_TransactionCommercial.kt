package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.C3_BonAchate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun upsert_1_3_TransactionCommercial(
    viewModel: ViewModel_MapClients_App2FragID1,
    relatedClientID: Long,
    newEtate: C3_BonAchate.EtateActuellementEst,
    cJustPourVoirPanie: Boolean = false,
) {
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

    val existingBonAchat = viewModel.c3_BonAchate_List.find {
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
                && it.etateActuellementEst == newEtate
    }

    if (existingBonAchat != null) {
        val updatedBonAchat = existingBonAchat.copy(
            cJustPourVoirPanie = cJustPourVoirPanie,
        )
        viewModel.repo_0_0_HeadSQLRepositorys.upsertUneDataEtReturnVID(
            updatedBonAchat
        ) { vid ->
            repositorysModel.activeVId_C3_BonAchate_Repository.value = updatedBonAchat.vid
        }

    } else {
        viewModel.repo_0_0_HeadSQLRepositorys.upsertUneDataEtReturnVID(
            C3_BonAchate(
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
            if (newEtate == C3_BonAchate.EtateActuellementEst.COMMANDE_LIVRAI
                || newEtate == C3_BonAchate.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) repositorysModel.activeVId_C3_BonAchate_Repository.value = 0
            else
                repositorysModel.activeVId_C3_BonAchate_Repository.value = vid
        }

    }
}
