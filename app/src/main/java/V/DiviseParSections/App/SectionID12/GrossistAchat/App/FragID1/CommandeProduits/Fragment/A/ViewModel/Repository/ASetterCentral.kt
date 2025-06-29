package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
    val bProduitDataBase_SubClassFunctionality: BProduitInfosRepository,
    val gTransactionVentRepository: GBonVentRepository,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    val bClientsStateCompoRepository = getter.fClientRepository

    fun ouvrireUneNewTransactionVent(clientOldId: Long) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
        val currentZCompt = zAppComptRepositoryComposable.currentAppCompt!!
        val newTransactionKey = GBonVent.generePushKey()

        val zCompt = currentZCompt.copy(
            onVentGBonVentKeyId = newTransactionKey,
            onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",
            onVentFClientKeyID = client.keyID,
            onVentFClientAncienId = client.id,
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
                parentHClientKeyID = client.id,
                etateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        )
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
            getter.fVentCouleurOperationRepository.acheterUneCouleur(it, relatedCouleur, quantity)
        }
    }

    companion object {
        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }

    fun deleteAddMultiDatas() {
       val  datas= bProduitDataBase_SubClassFunctionality.datasValue
        CoroutineScope(Dispatchers.IO).launch {
            bProduitDataBase_SubClassFunctionality.dao.deleteAll()
            bProduitDataBase_SubClassFunctionality.dao.insertAll(datas)

            ArticlesBasesStatsTable.safeRemoveRef()
            bProduitDataBase_SubClassFunctionality.ancienRepo.batchFireBaseUpdateArticlesBasesStatsTable(datas)
        }
    }
}
