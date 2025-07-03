package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Id8BonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Id9AppComptRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

class BonVentOperations(
    private val getter: AGetter,
    private val gBonVentRepository: Id8BonVentRepository,
    private val zAppComptRepositoryComposable: Id9AppComptRepository
) {
    val hClientRepository = getter.iD2ClientRepository
    val parametresAppComptNonSaved = getter.parametresAppComptNonSaved
    val activePeriodKeyByParent = parametresAppComptNonSaved.activePeriodKeyByParent

    fun client(clientOldID: Long) = hClientRepository.datasValue.find { it.id == clientOldID }

    // Fixed: Separate the logic into a non-Composable function and a Composable function
    fun getViewClientKeyByParent(idClient: Long?): String {
        return getKeyID8BonVent(
            idClient,
            parametresAppComptNonSaved = parametresAppComptNonSaved,
            hClientRepository = hClientRepository,)
    }

    fun ajouteNewBonVent(
        key: String,
        clientOldId: Long,
        etate: GBonVent.EtateActuellementEst
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt

        if (client != null && currentZCompt != null) {
            val updatedZCompt = currentZCompt.copy(
                onVentId8BonVentKeyId = key,
                onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
            )

            zAppComptRepositoryComposable.addOrUpdateData(updatedZCompt)

            val newBonVent = GBonVent(
                keyID = key,
                parentPeriodeVentKeyID = currentZCompt.onVentHVentPeriodKeyId,
                parentId2ClientInfosKeyID = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentZAppComptCreateurKeyID = currentZCompt.keyID,
                etateActuellementEst = etate,
                parentID2ClientKeyByParent = BSetterFacade.getListDesParentKeys("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetterFacade.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )

            gBonVentRepository.upsert(newBonVent)
        }
    }

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: GBonVent.EtateActuellementEst
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt
        val existingBonVent = gBonVentRepository.datasValue.find { it.keyID == key }

        if (client != null && currentZCompt != null && existingBonVent != null) {
            // Update the current app compt
            val updatedZCompt = currentZCompt.copy(
                onVentId8BonVentKeyId = key,
                onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
            )

            zAppComptRepositoryComposable.addOrUpdateData(updatedZCompt)

            // Update the existing BonVent
            val updatedBonVent = existingBonVent.copy(
                etateActuellementEst = etate,
                nomClientConcerned = client.nom,
                parentId2ClientInfosKeyID = client.keyID,
                parentHClientOldID = clientOldId
            )

            gBonVentRepository.upsert(updatedBonVent)
        }
    }

    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
    }

    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentId8BonVentKeyId = "",
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
    }

    fun clear_bOuvertDialogMapMarqueHClientKey() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
    }
}
