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

        // Generate new transaction key
        val newTransactionKey = GBonVent.generePushKey()

        val zCompt = currentZCompt.copy(
            onVentGBonVentKeyId = newTransactionKey,

            // Debug information to help identify which client this transaction belongs to
            onVentGBonVentDebugNameKey = "(${client.nom})=${client.id}",

            onVentFClientKeyID = client.keyID,

            onVentFClientAncienId = client.id,
            onVentFClientDebugNameKey = client.nom,
        )

        zAppComptRepositoryComposable.addOrUpdateData(zCompt)

        gTransactionVentRepository.addOrUpdateData(
            GBonVent(
                keyID = newTransactionKey,

                // Fixed: Use the period key from the updated zCompt
                parentPeriodeVentKeyID = zCompt.onVentHPeriodVentKeyId,

                // Fixed: Use the client's old ID directly
                parentHClientOldID = clientOldId,

                // Fixed: Use the correct field name and value
                parentZAppComptCreateurKeyID = zCompt.keyID,

                // Additional fields that should be set
                nomClientConcerned = client.nom,
                parentHClientKeyID = client.id, // Assuming client has an id field

                // Set the state to indicate this is a new transaction
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
            if (existingOperation.quantityAchete != quantity) {
                val updatedOperation = existingOperation.copy(
                    quantityAchete = quantity,
                )
                getter.fVentCouleurOperationRepository.addOrUpdateData(updatedOperation)
            } else {
                getter.fVentCouleurOperationRepository.addOrUpdateData(existingOperation)
            }
        } ?: run {
            if (data != null) {
                getter.fVentCouleurOperationRepository.acheterUneCouleur(
                    ouvertData = data,
                    relatedCouleur = relatedCouleur,
                    quantity = quantity
                )
            }
        }
    }

    companion object {
        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}
