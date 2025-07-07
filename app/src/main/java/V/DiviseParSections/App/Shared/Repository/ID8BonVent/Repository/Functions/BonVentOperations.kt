package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.MainRepositorysSetterFacade
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

class BonVentOperations(
    private val getter: MainRepositorysGetterFacade,
    private val gBonVentRepository: Repo8BonVent,
    private val zAppComptRepositoryComposable: Repo9AppCompt
) {
    val hClientRepository = getter.repo2Client
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
        etate: M8BonVent.EtateActuellementEst
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt

        if (client != null && currentZCompt != null) {
            val updatedZCompt = currentZCompt.copy(
                onVentM8BonVentKey = key,
                onVentM8BonVentDebugInfos = "(${client.nom})=${client.id}",
            )

            zAppComptRepositoryComposable.upsert(updatedZCompt)

            val newBonVent = M8BonVent(
                keyID = key,
                parentM7VentPeriodKeyId = currentZCompt.onVentHVentPeriodKeyId,
                parentM2ClientInfosKey = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentKeyId9AppComptInfos = currentZCompt.keyID,
                etateActuellementEst = etate,
                parentID2ClientKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[M8BonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = MainRepositorysSetterFacade.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )

            gBonVentRepository.upsert(newBonVent)
        }
    }

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: M8BonVent.EtateActuellementEst
    ) {
        val client = hClientRepository.datasValue.find { it.id == clientOldId }
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt
        val existingBonVent = gBonVentRepository.datasValue.find { it.keyID == key }

        if (client != null && currentZCompt != null && existingBonVent != null) {
            // Update the current app compt
            val updatedZCompt = currentZCompt.copy(
                onVentM8BonVentKey = key,
                onVentM8BonVentDebugInfos = "(${client.nom})=${client.id}",
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
}
