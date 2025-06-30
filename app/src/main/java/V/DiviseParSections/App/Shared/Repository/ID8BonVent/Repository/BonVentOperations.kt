package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.BSetter
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.Patch.BSetterP
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class BonVentOperations(
    private val getter: AGetter,
    private val gBonVentRepository: GBonVentRepository,
    private val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable
) {
    val hClientRepository = getter.hClientRepository
    val activePeriodKeyByParent = getter.parametresAppComptNonSaved.activePeriodKeyByParent
    val keyModelToOnVentHVentPeriodKeyByParent =
        Z_AppCompt.keyModelValID7 + "-" + activePeriodKeyByParent

    fun client(clientOldID: Long) = hClientRepository.datasValue.find { it.id == clientOldID }

    @Composable
    fun ViewKey(
        idClient: Long?,
    ) {
        val keyHandBonVentOnClickButton = getKeyID8BonVent(idClient)
        Text(keyHandBonVentOnClickButton)
    }

    fun getKeyID8BonVent(
        clientOldID: Long? = null,
        etate: GBonVent.EtateActuellementEst? = null,
    ): String {
        val keyModelToClientKeyByParent =
            clientOldID?.let { HClientInfos.keyModel + "-" + client(clientOldID)?.getTempKeyByParent() }
        val keyModelToEtateKey =
            etate?.let { "--" + GBonVent.EtateActuellementEst.keyModel + "-" + it.name }
                ?: ""

        return ("$keyModelToOnVentHVentPeriodKeyByParent--$keyModelToClientKeyByParent$keyModelToEtateKey")
            .withOutFireBaseInvalidCharacters()
    }

    fun upsertBonVent(
        keyByParentBonVentOnClickButton: String = ""
    ) {
        val existingData = gBonVentRepository.datasValue.find {
            it.keyByParent == keyByParentBonVentOnClickButton
        }

        val data =
            existingData?.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
                ?: run {
                    val getKeyByParentDe =
                        BSetterP.regexReturnParentKeysMap(keyByParentBonVentOnClickButton)
                    val parentID2ClientKeyByParent = getKeyByParentDe[HClientInfos.keyModel]!!
                    val client =
                        hClientRepository.findHClientInfosByKeyByParent(parentID2ClientKeyByParent)
                    val parentID8C2TypeTransactionKeyByParent =
                        getKeyByParentDe[GBonVent.EtateActuellementEst.keyModel]!!

                    GBonVent(
                        keyByParent = keyByParentBonVentOnClickButton,
                        parentID7VentPeriodeKeyByParent = keyModelToOnVentHVentPeriodKeyByParent,
                        parentHClientOldID = client.id,
                        parentID2ClientKeyByParent = parentID2ClientKeyByParent,
                        parentID8C2TypeTransactionKeyByParent = parentID8C2TypeTransactionKeyByParent,
                        etateActuellementEst = findEtateParKeyByParent(
                            parentID8C2TypeTransactionKeyByParent
                        )
                    )
                }

        gBonVentRepository.addOrUpdateData(data)
    }

    private fun findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent: String): GBonVent.EtateActuellementEst {
        return try {
            // Try to find the enum value by name
            GBonVent.EtateActuellementEst.valueOf(parentID8C2TypeTransactionKeyByParent)
        } catch (e: IllegalArgumentException) {
            // If direct valueOf fails, try to match by ordinal or other patterns
            when (parentID8C2TypeTransactionKeyByParent.uppercase()) {
                "ON_MODE_COMMEND_ACTUELLEMENT" -> GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                "A_COMMANDE_CONFIRME" -> GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                "POURVOIRPANIE" -> GBonVent.EtateActuellementEst.PourVoirPanie
                "COMMANDE_LIVRAI" -> GBonVent.EtateActuellementEst.COMMANDE_LIVRAI
                "AVEC_MARCHANDISE" -> GBonVent.EtateActuellementEst.AVEC_MARCHANDISE
                "ACHETEUR_NON_DISPO" -> GBonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
                "FERME" -> GBonVent.EtateActuellementEst.FERME
                "A_EVITE" -> GBonVent.EtateActuellementEst.A_EVITE
                "RAPPORT_AU_ENREGESTREMENT_VOCALE" -> GBonVent.EtateActuellementEst.RAPPORT_AU_ENREGESTREMENT_VOCALE
                "ON_MODE_VOIRE_PANIE_ARTICLES" -> GBonVent.EtateActuellementEst.ON_MODE_VOIRE_PANIE_ARTICLES
                "CIBLE" -> GBonVent.EtateActuellementEst.Cible
                "CIBLE_PRIORITE_2" -> GBonVent.EtateActuellementEst.CIBLE_PRIORITE_2
                "CIBLE_PRIORITE_3" -> GBonVent.EtateActuellementEst.CIBLE_PRIORITE_3
                "CIBLE_POUR_2" -> GBonVent.EtateActuellementEst.CIBLE_POUR_2
                else -> {
                    // If no match found, return the default state
                    GBonVent.EtateActuellementEst.CreeMaisNonDefinie
                }
            }
        }
    }

    fun ouvrireNewAppComptOnVentBonVentEtAddLe(
        clientOldId: Long,
        newEtate: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
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
                parentID2ClientKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetter.regexReturnParentKeysMap("null")[Z_AppCompt.keyModelValID7]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.EtateActuellementEst.keyModel]
                    ?: ""
            )
        )
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
                parentID2ClientKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.keyModel]
                    ?: "",
                parentID7VentPeriodeKeyByParent = BSetter.regexReturnParentKeysMap("null")[Z_AppCompt.keyModelValID7]
                    ?: "",
                parentID8C2TypeTransactionKeyByParent = BSetter.regexReturnParentKeysMap("null")[GBonVent.EtateActuellementEst.keyModel]
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
