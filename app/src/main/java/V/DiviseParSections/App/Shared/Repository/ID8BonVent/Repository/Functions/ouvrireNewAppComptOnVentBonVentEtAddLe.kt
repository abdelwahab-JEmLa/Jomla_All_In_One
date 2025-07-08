package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.Set
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

fun ouvrireNewAppComptOnVentBonVentEtAddLeHelper(
    clientOldId: Long,
    newEtate: M8BonVent.EtateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    hClientRepository: Repo2Client,
    zAppComptRepositoryComposable: Repo9AppCompt,
    gBonVentRepository: Repo8BonVent,
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }!!
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt!!
        val newTransactionKey = M8BonVent.generePushKey()

        val zCompt = currentZCompt.copy(
            onVentM8BonVentKey = newTransactionKey,
            onVentM8BonVentDebugInfos = "(${client.nom})=${client.id}",
        )

        zAppComptRepositoryComposable.upsert(zCompt)

        gBonVentRepository.upsert(
            M8BonVent(
                keyID = newTransactionKey,
                parentM7VentPeriodKeyId = zCompt.current_OnVent_M14VentPeriode_KeyID,
                parentM2ClientInfosKey = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentKeyId9AppComptInfos = zCompt.keyID,
                etateActuellementEst = newEtate,
                parentID2ClientKeyByParent = Set.getListDesParentKeys("null")[M8BonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = Set.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = Set.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )
        )
    }
