package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import com.google.firebase.database.DatabaseReference

class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
    val gTransactionVentRepository: GBonVentRepository,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    val bClientsStateCompoRepository = getter.fClientRepository

    fun ouvrireUneNewTransactionVent(clientOldId: Long) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
        val currentZCompt = zAppComptRepositoryComposable.ouvertData!!
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
                parentPeriodeVentKeyID = zCompt.onVentHPeriodVentKeyId,
                parentHClientOldID = clientOldId,
                parentZAppComptCreateurKeyID = zCompt.keyID,
                nomClientConcerned = client.nom,
                parentHClientKeyID = client.id,
                etateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        )
    }


    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperation? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val relatedCouleur = getter.getRelatedCouleur(produit, colorIndex)
        val data = zAppComptRepositoryComposable.ouvertData

        fCouleurVentOperation?.let { existingOperation ->
            val updatedOperation = existingOperation.copy(quantityAchete = quantity)
            getter.fVentCouleurOperationRepository.addOrUpdateData(updatedOperation)
        } ?: data?.let {
            getter.fVentCouleurOperationRepository.acheterUneCouleur(it, relatedCouleur, quantity)
        }
    }

    companion object {
        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}
