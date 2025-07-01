package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository

fun ouvrireNewAppComptOnVentBonVentEtAddLeHelper(
    clientOldId: Long,
    newEtate: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    hClientRepository: HClientRepository,
    zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    gBonVentRepository: GBonVentRepository,
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }!!
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt!!
        val newTransactionKey = GBonVent.generePushKey()

        val zCompt = currentZCompt.copy(
            onVentGBonVentKeyId = newTransactionKey,
            onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
            onVentFClientKeyID = client.keyID,
            onVentFClientDebugNameKey = client.nom,
        )

        zAppComptRepositoryComposable.addOrUpdateData(zCompt)

        gBonVentRepository.addOrUpdateData(
            GBonVent(
                keyID = newTransactionKey,
                parentPeriodeVentKeyID = zCompt.onVentHVentPeriodKeyId,
                parentHClientKeyID = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentZAppComptCreateurKeyID = zCompt.keyID,
                etateActuellementEst = newEtate,
                parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValVentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )
        )
    }
