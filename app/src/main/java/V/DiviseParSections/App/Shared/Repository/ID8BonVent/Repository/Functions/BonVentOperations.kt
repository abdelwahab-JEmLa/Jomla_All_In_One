package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

class BonVentOperations(
    private val getter: AGetter,
    private val gBonVentRepository: Repo8BonVent,
    private val zAppComptRepositoryComposable: Repo9AppCompt
) {
    val hClientRepository = getter.iD2ClientRepository
    val parametresAppComptNonSaved = getter.parametresAppComptNonSaved
    val activePeriodKeyByParent = parametresAppComptNonSaved.keyIdId7VentPeriod

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
                id8BonVentonVentKey = key,
                id8BonVentDebugNameKey = "(${client.nom})=${client.id}",
            )

            zAppComptRepositoryComposable.upsert(updatedZCompt)

            val newBonVent = GBonVent(
                keyID = key,
                parentKeyId7VentPeriod = currentZCompt.onVentHVentPeriodKeyId,
                parentM2ClientInfosKey = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentKeyId9AppComptInfos = currentZCompt.keyID,
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
                id8BonVentonVentKey = key,
                id8BonVentDebugNameKey = "(${client.nom})=${client.id}",
            )

            zAppComptRepositoryComposable.upsert(updatedZCompt)

            // Update the existing BonVent
            val updatedBonVent = existingBonVent.copy(
                etateActuellementEst = etate,
                nomClientConcerned = client.nom,
                parentM2ClientInfosKey = client.keyID,
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
            zAppComptRepositoryComposable.upsert(zCompt)
        }
    }

    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            id8BonVentonVentKey = "",
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.upsert(zCompt)
        }
    }

    fun clear_bOuvertDialogMapMarqueHClientKey() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.upsert(zCompt)
        }
    }
}
