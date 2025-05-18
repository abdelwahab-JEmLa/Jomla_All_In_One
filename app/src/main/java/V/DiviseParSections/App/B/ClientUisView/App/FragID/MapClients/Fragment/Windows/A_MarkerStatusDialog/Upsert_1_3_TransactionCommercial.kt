package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
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

    val existingBonAchat = viewModel.modelDatasSnapList_1_3_TransactionCommercial.find {
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
    sectionSqlRepository(
        sqlRepository = viewModel.sqlRepository,
        relatedClients = relatedClients!!,
    )
}

fun sectionSqlRepository(
    relatedClients: B_ClientDataBase,
    sqlRepository: InfosSqlDataBasesRepository
): Unit {
    val currentData = sqlRepository.modelListFlow.value.firstOrNull()

    // Convert B_ClientDataBase to B_ClientInfos
    val b_ClientInfos = currentData!!
        .b_ClientInfosList.find {
            it.id == relatedClients.id
        } ?: B_ClientInfos(
        id = relatedClients.id,
        nom = relatedClients.nom
    )

    sqlRepository.addoneClientInfos(b_ClientInfos)

    // Create updated list with the target client set to open
    val updatedClientList = currentData.b_ClientInfosList.map { cli ->
        if (cli.id == b_ClientInfos.id) {
            cli.copy(cLeDataOuvertDuParentList = true)
        } else {
            cli.copy(cLeDataOuvertDuParentList = false)
        }
    }.toMutableList()

    // Update clients with the correct parameter
    sqlRepository.updateMultiClientInfos(updatedClientList)
}
