package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.BProduitInfosRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVent
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.GBonVentRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.HClientInfos
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.HClientRepository
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ZAppCompt_RepositoryComposable
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BSetter(
    val getter: AGetter,
    val bProduitDataBase_SubClassFunctionality: BProduitInfosRepository,
    val fVentCouleurOperationRepository: FVentCouleurOperationRepository,
    val hClientRepository: HClientRepository,
    val gTransactionVentRepository: GBonVentRepository,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    val navigationHandler: FragmentNavigationHandler
) {

    private val setterScope = CoroutineScope(Dispatchers.IO)

    val bClientsStateCompoRepository = getter.hClientRepository

    fun updateDialogMapMarque(clientID :Long) {
        val clientKey = hClientRepository.datasValue.find { it.id == clientID }?.keyID
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt!!

        val zCompt = if (clientKey.isNullOrBlank()) {
            currentZCompt.copy(
                bOuvertDialogMapMarqueHClientKey = ""
            )
        } else {
            currentZCompt.copy(
                bOuvertDialogMapMarqueHClientKey = clientKey
            )
        }

        zAppComptRepositoryComposable.addOrUpdateData(zCompt)
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

        gTransactionVentRepository.addOrUpdateData(
            GBonVent(
                keyID = newTransactionKey,
                parentPeriodeVentKeyID = zCompt.onVentHVentPeriodKeyId,
                parentHClientOldID = clientOldId,
                parentZAppComptCreateurKeyID = zCompt.keyID,
                nomClientConcerned = client.nom,
                parentHClientKeyID = client.keyID,
                etateActuellementEst = newEtate
            )
        )
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

    fun cleanFermeAppComptOnVentBonVent() {
        val zCompt = zAppComptRepositoryComposable.currentAppCompt?.copy(
            onVentGBonVentKeyId = "",
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

    companion object {
        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
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


}
