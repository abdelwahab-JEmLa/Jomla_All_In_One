package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository

class BonVentOperations(
    private val getter: AGetter,
    private val gBonVentRepository: GBonVentRepository,
    private val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable
) {
    val hClientRepository = getter.hClientRepository
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
                onVentGBonVentKeyId = key,
                onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
                onVentFClientKeyID = client.keyID,
                onVentFClientDebugNameKey = client.nom,
            )

            zAppComptRepositoryComposable.addOrUpdateData(updatedZCompt)

            val newBonVent = GBonVent(
                keyID = key,
                parentPeriodeVentKeyID = currentZCompt.onVentHVentPeriodKeyId,
                parentHClientKeyID = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentZAppComptCreateurKeyID = currentZCompt.keyID,
                etateActuellementEst = etate,
                parentID2ClientKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetter.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )

            gBonVentRepository.addOrUpdateData(newBonVent)
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
                onVentGBonVentKeyId = key,
                onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
                onVentFClientKeyID = client.keyID,
                onVentFClientDebugNameKey = client.nom,
            )

            zAppComptRepositoryComposable.addOrUpdateData(updatedZCompt)

            // Update the existing BonVent
            val updatedBonVent = existingBonVent.copy(
                etateActuellementEst = etate,
                nomClientConcerned = client.nom,
                parentHClientKeyID = client.keyID,
                parentHClientOldID = clientOldId
            )

            gBonVentRepository.addOrUpdateData(updatedBonVent)
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
            onVentFClientKeyID = "",
            onVentFClientDebugNameKey = "",
            onVentGBonVentKeyId = "",
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
