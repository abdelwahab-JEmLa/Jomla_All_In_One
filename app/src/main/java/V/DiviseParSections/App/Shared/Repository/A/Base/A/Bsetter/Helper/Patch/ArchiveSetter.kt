package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.Patch
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BSetterP (
    val getter: AGetter,
    val bProduitDataBase_SubClassFunctionality: RepoM1ProduitInfos,
    val fVentCouleurOperationRepository: Repo10OperationVentCouleur,
    val hClientRepository: Repo2Client,
    val gBonVentRepository: Repo8BonVent,
    val zAppComptRepositoryComposable: Repo9AppCompt,
    val navigationHandler: FragmentNavigationHandler
) {
    private val setterScope = CoroutineScope(Dispatchers.IO)
    val bClientsStateCompoRepository = getter.iD2ClientRepository

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
            zAppComptRepositoryComposable.upsert(zCompt)
        }
    }

    val activePeriodKeyByParent = getter.parametresAppComptNonSaved.keyIdId7VentPeriod
    val keyModelToOnVentHVentPeriodKeyByParent = Z_AppCompt.keyModelValID7VentParent + "-" + activePeriodKeyByParent

    fun client(clientOldID:Long) = hClientRepository.datasValue.find { it.id ==clientOldID }

    fun getKeyID8BonVent(
        clientOldID: Long,
        etate: M8BonVent.EtateActuellementEst,
    ): String {
        val clientKeyByParent = client(clientOldID)?.getTempKeyByParent()
        val keyModelToClientKeyByParent = HClientInfos.keyModel + "-" + clientKeyByParent
        val keyModelToEtateKey = M8BonVent.EtateActuellementEst.keyModel + "-" + etate

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
                    getKeyByParentDe[M8BonVent.EtateActuellementEst.keyModel]!!

                M8BonVent(
                    keyByParent = keyByParentBonVentOnClickButton,
                    parentID7VentPeriodeKeyByParent = keyModelToOnVentHVentPeriodKeyByParent,
                    parentHClientOldID = client.id,
                    parentID2ClientKeyByParent = parentID2ClientKeyByParent,
                    parentID8C2TypeTransactionKeyByParent = parentID8C2TypeTransactionKeyByParent,
                    etateActuellementEst = findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent)
                )
            }

        gBonVentRepository.upsert(data)
    }

    private fun findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent: String): M8BonVent.EtateActuellementEst {
        return try {
            // Try to find the enum value by name
            M8BonVent.EtateActuellementEst.valueOf(parentID8C2TypeTransactionKeyByParent)
        } catch (e: IllegalArgumentException) {
            // If direct valueOf fails, try to match by ordinal or other patterns
            when (parentID8C2TypeTransactionKeyByParent.uppercase()) {
                "ON_MODE_COMMEND_ACTUELLEMENT" -> M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                "A_COMMANDE_CONFIRME" -> M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                "POURVOIRPANIE" -> M8BonVent.EtateActuellementEst.PourVoirPanie
                "COMMANDE_LIVRAI" -> M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                "AVEC_MARCHANDISE" -> M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE
                "ACHETEUR_NON_DISPO" -> M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
                "FERME" -> M8BonVent.EtateActuellementEst.FERME
                "A_EVITE" -> M8BonVent.EtateActuellementEst.A_EVITE
                "RAPPORT_AU_ENREGESTREMENT_VOCALE" -> M8BonVent.EtateActuellementEst.RAPPORT_AU_ENREGESTREMENT_VOCALE
                "ON_MODE_VOIRE_PANIE_ARTICLES" -> M8BonVent.EtateActuellementEst.ON_MODE_VOIRE_PANIE_ARTICLES
                "CIBLE" -> M8BonVent.EtateActuellementEst.Cible
                "CIBLE_PRIORITE_2" -> M8BonVent.EtateActuellementEst.CIBLE_PRIORITE_2
                "CIBLE_PRIORITE_3" -> M8BonVent.EtateActuellementEst.CIBLE_PRIORITE_3
                "CIBLE_POUR_2" -> M8BonVent.EtateActuellementEst.CIBLE_POUR_2
                else -> {
                    // If no match found, return the default state
                    M8BonVent.EtateActuellementEst.CreeMaisNonDefinie
                }
            }
        }
    }

    fun ouvrireNewAppComptOnVentBonVentEtAddLe(
        clientOldId: Long,
        newEtate: M8BonVent.EtateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
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
                parentM7VentPeriodKeyId = zCompt.onVentHVentPeriodKeyId,
                parentM2ClientInfosKey = client.keyID,
                parentHClientOldID = clientOldId,
                nomClientConcerned = client.nom,
                parentKeyId9AppComptInfos = zCompt.keyID,
                etateActuellementEst = newEtate,
                parentID2ClientKeyByParent = getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
                parentID7VentPeriodeKeyByParent = getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent] ?: "",
                parentID8C2TypeTransactionKeyByParent = getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
            )
        )
    }

    fun ajouteNewBonVent(
        key: String,
        clientOldId: Long,
        etate: M8BonVent.EtateActuellementEst
    ) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }
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
                parentID2ClientKeyByParent = getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
                parentID7VentPeriodeKeyByParent = getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent] ?: "",
                parentID8C2TypeTransactionKeyByParent = getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
            )

            gBonVentRepository.upsert(newBonVent)
        }
    }

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: M8BonVent.EtateActuellementEst
    ) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }
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


    fun ouvreExistedDataEtNavigatePanie(keyID: String) {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentM8BonVentKey = keyID,
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.upsert(zCompt)
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
            onVentM8BonVentKey = "",
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

    fun cleanFermeAppComptOnVentBonVent() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentM8BonVentKey = "",
            bOuvertDialogMapMarqueHClientKey = ""
        )

        if (zCompt != null) {
            zAppComptRepositoryComposable.upsert(zCompt)
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


    fun te(
        fCouleurVentOperation: M10OperationVentCouleur? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val relatedCouleur = getter.getRelatedCouleur(produit, colorIndex)
        val zCompt = zAppComptRepositoryComposable.currentAppCompt

        fCouleurVentOperation?.let { existingOperation ->
            val updatedOperation = existingOperation.copy(quantityAchete = quantity)
            getter.repo10OperationVentCouleur.addOrUpdateData(updatedOperation)
        } ?: zCompt?.let {
            getter.repo10OperationVentCouleur.acheterUneCouleur(
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
            hClientRepository.dataBaseCreationFactory.dao.deleteAll()
            hClientRepository.dataBaseCreationFactory.dao.insertAll(datas)

            HClientInfos.safeRemoveRef()

            hClientRepository.dataBaseCreationFactory.batchFireBaseUpdate(
                datas
            )
        }
    }

    fun updateListRelativeVentCouleurPrixVent(produitKey: String?, newPrix: Double) {
        val ventCouleursDuProduitKey =
            fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
                .filter { it.parentM1ProduitInfosKeyId == produitKey }

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
            getter.repoM1ProduitInfos.datasValue.find { it.id == parentProduitOldId }?.keyID
        val ventCouleursDuProduitKey =
            fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
                .filter { it.parentM1ProduitInfosKeyId == produitKey }

        ventCouleursDuProduitKey.forEach { vent ->
            fVentCouleurOperationRepository.delete(
                vent
            )
        }
    }

    fun ventCouleursDuProduitKey(produitKey: String) =
        fVentCouleurOperationRepository.datasFilteredParCurrentHVentPeriod
            .filter { it.parentM1ProduitInfosKeyId == produitKey }

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) {
        ventCouleursDuProduitKey(produitKey).forEach { vent ->
            val newState =
                if (vent.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve)
                    M10OperationVentCouleur.EtateDelivery.NonTrouve
                else M10OperationVentCouleur.EtateDelivery.Trouve

            fVentCouleurOperationRepository.addOrUpdateData(vent.copy(etateDelivery = newState))
        }
    }

    companion object {


        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}

