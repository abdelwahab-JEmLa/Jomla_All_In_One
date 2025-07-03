package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.ID2ClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Id8BonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Id9AppComptRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

fun ouvrireNewAppComptOnVentBonVentEtAddLeHelper(
    clientOldId: Long,
    newEtate: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    hClientRepository: ID2ClientRepository,
    zAppComptRepositoryComposable: Id9AppComptRepository,
    gBonVentRepository: Id8BonVentRepository,
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }!!
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt!!
        val newTransactionKey = GBonVent.generePushKey()

        val zCompt = currentZCompt.copy(
            onVentId8BonVentKeyId = newTransactionKey,
            onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
        )

        zAppComptRepositoryComposable.addOrUpdateData(zCompt)

        gBonVentRepository.upsert(
            GBonVent(
                keyID = newTransactionKey,
                parentPeriodeVentKeyID = zCompt.onVentHVentPeriodKeyId,
                parentId2ClientInfosKeyID = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentZAppComptCreateurKeyID = zCompt.keyID,
                etateActuellementEst = newEtate,
                parentID2ClientKeyByParent = BSetterFacade.getListDesParentKeys("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetterFacade.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )
        )
    }
