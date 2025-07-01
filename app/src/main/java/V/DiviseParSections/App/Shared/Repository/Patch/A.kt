package V.DiviseParSections.App.Shared.Repository.Patch
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitInfosRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.BSetter.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BSetterP (
    val getter: AGetter,
    val bProduitDataBase_SubClassFunctionality: BProduitInfosRepository,
    val fVentCouleurOperationRepository: FVentCouleurOperationRepository,
    val hClientRepository: HClientRepository,
    val gBonVentRepository: GBonVentRepository,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    val navigationHandler: FragmentNavigationHandler
) {
    private val setterScope = CoroutineScope(Dispatchers.IO)
    val bClientsStateCompoRepository = getter.hClientRepository

    fun update_bOuvertDialogMapMarqueHClientKey(clientID: Long) {
        val clientKey = hClientRepository.datasValue.find { it.id == clientID }?.keyID

        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt

        val zCompt =
            clientKey?.let {
                currentZCompt?.copy(
                    bOuvertDialogMapMarqueHClientKey = it
                )
            }

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
    }

    val activePeriodKeyByParent = getter.parametresAppComptNonSaved.activePeriodKeyByParent
    val keyModelToOnVentHVentPeriodKeyByParent = Z_AppCompt.keyModelValVentParent + "-" + activePeriodKeyByParent

    fun client(clientOldID:Long) = hClientRepository.datasValue.find { it.id ==clientOldID }

    fun getKeyID8BonVent(
        clientOldID: Long,
        etate: GBonVent.EtateActuellementEst,
    ): String {
        val clientKeyByParent = client(clientOldID)?.getTempKeyByParent()
        val keyModelToClientKeyByParent = HClientInfos.keyModel + "-" + clientKeyByParent
        val keyModelToEtateKey = GBonVent.EtateActuellementEst.keyModel + "-" + etate

        return ("$keyModelToOnVentHVentPeriodKeyByParent--$keyModelToClientKeyByParent--$keyModelToEtateKey")
            .withOutFireBaseInvalidCharacters()
    }

    fun upsertBonVent(
        keyByParentBonVentOnClickButton: String = ""
    ) {
        val existingData = gBonVentRepository.datasValue.find {
            it.keyByParent == keyByParentBonVentOnClickButton
        }

        val data = existingData?.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
            ?: run {
                val getKeyByParentDe = getListDesParentKeys(keyByParentBonVentOnClickButton)
                val parentID2ClientKeyByParent = getKeyByParentDe[HClientInfos.keyModel]!!
                val client = hClientRepository.findHClientInfosByKeyDeClient(parentID2ClientKeyByParent)
                val parentID8C2TypeTransactionKeyByParent =
                    getKeyByParentDe[GBonVent.EtateActuellementEst.keyModel]!!

                GBonVent(
                    keyByParent = keyByParentBonVentOnClickButton,
                    parentID7VentPeriodeKeyByParent = keyModelToOnVentHVentPeriodKeyByParent,
                    parentHClientOldID = client.id,
                    parentID2ClientKeyByParent = parentID2ClientKeyByParent,
                    parentID8C2TypeTransactionKeyByParent = parentID8C2TypeTransactionKeyByParent,
                    etateActuellementEst = findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent)
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
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
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
                parentID2ClientKeyByParent = getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
                parentID7VentPeriodeKeyByParent = getListDesParentKeys("null")[Z_AppCompt.keyModelValVentParent] ?: "",
                parentID8C2TypeTransactionKeyByParent = getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
            )
        )
    }

    fun ajouteNewBonVent(
        key: String,
        clientOldId: Long,
        etate: GBonVent.EtateActuellementEst
    ) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }
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
                parentID2ClientKeyByParent = getListDesParentKeys("null")[GBonVent.keyModel] ?: "",
                parentID7VentPeriodeKeyByParent = getListDesParentKeys("null")[Z_AppCompt.keyModelValVentParent] ?: "",
                parentID8C2TypeTransactionKeyByParent = getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel] ?: ""
            )

            gBonVentRepository.addOrUpdateData(newBonVent)
        }
    }

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: GBonVent.EtateActuellementEst
    ) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }
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


    fun ouvreExistedDataEtNavigatePanie(keyID: String) {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentGBonVentKeyId = keyID,
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
        }
        navigateToCartScreen()
    }

    fun navigateToCartScreen() {
        setterScope.launch(Dispatchers.Main) {
            navigationHandler.navigateToCartScreen()
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

    fun cleanFermeAppComptOnVentBonVent() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentGBonVentKeyId = "",
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.addOrUpdateData(zCompt)
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


    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperationInfos? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val relatedCouleur = getter.getRelatedCouleur(produit, colorIndex)
        val zCompt = zAppComptRepositoryComposable.currentAppCompt

        fCouleurVentOperation?.let { existingOperation ->
            val updatedOperation = existingOperation.copy(quantityAchete = quantity)
            getter.fVentCouleurOperationRepository.addOrUpdateData(updatedOperation)
        } ?: zCompt?.let {
            getter.fVentCouleurOperationRepository.acheterUneCouleur(
                it, relatedCouleur, quantity
            )
        }
    }



    fun deleteAddMultiDatas() {
        val datas = bProduitDataBase_SubClassFunctionality.datasValue
        CoroutineScope(Dispatchers.IO).launch {
            bProduitDataBase_SubClassFunctionality.dao.deleteAll()
            bProduitDataBase_SubClassFunctionality.dao.insertAll(datas)

            ArticlesBasesStatsTable.safeRemoveRef()
            bProduitDataBase_SubClassFunctionality.ancienRepo.batchFireBaseUpdateArticlesBasesStatsTable(
                datas
            )
        }
    }

    fun deleteAddMultiClients() {
        val datas = bClientsStateCompoRepository.datasValue
        CoroutineScope(Dispatchers.IO).launch {
            hClientRepository.dataBaseFactoryFClient.dao.deleteAll()
            hClientRepository.dataBaseFactoryFClient.dao.insertAll(datas)

            HClientInfos.safeRemoveRef()

            hClientRepository.dataBaseFactoryFClient.batchFireBaseUpdate(
                datas
            )
        }
    }

    fun updateListRelativeVentCouleurPrixVent(produitKey: String?, newPrix: Double) {
        val ventCouleursDuProduitKey =
            fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
                .filter { it.parentBProduitInfosKeyId == produitKey }

        ventCouleursDuProduitKey.forEach { vent ->
            fVentCouleurOperationRepository.addOrUpdateData(
                vent.copy(
                    provisoireMonPrix = newPrix
                )
            )
        }
    }

    fun deleteVents(parentProduitOldId: Long) {
        val produitKey =
            getter.bProduitInfosRepository.datasValue.find { it.id == parentProduitOldId }?.keyID
        val ventCouleursDuProduitKey =
            fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
                .filter { it.parentBProduitInfosKeyId == produitKey }

        ventCouleursDuProduitKey.forEach { vent ->
            fVentCouleurOperationRepository.delete(
                vent
            )
        }
    }

    fun ventCouleursDuProduitKey(produitKey: String) =
        fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
            .filter { it.parentBProduitInfosKeyId == produitKey }

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) {
        ventCouleursDuProduitKey(produitKey).forEach { vent ->
            val newState =
                if (vent.etateDelivery == FCouleurVentOperationInfos.EtateDelivery.Trouve)
                    FCouleurVentOperationInfos.EtateDelivery.NonTrouve
                else FCouleurVentOperationInfos.EtateDelivery.Trouve

            fVentCouleurOperationRepository.addOrUpdateData(vent.copy(etateDelivery = newState))
        }
    }

    companion object {


        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}

