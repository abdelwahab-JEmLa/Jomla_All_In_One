package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import android.util.Log

class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
    val fCouleurAchatOperationRepositoryComposable: FAchatOperationCouleurRepositoryComposable,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    companion object {
        private const val TAG = "ASetterCentral"
    }

    fun getRelatedCouleur(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        getter.getRelatedCouleur(produit, colorIndex)

    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperation? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        Log.d(TAG, "=== acheterACaSetterCentral START ===")
        Log.d(TAG, "Input parameters:")
        Log.d(TAG, "  fCouleurVentOperation: ${fCouleurVentOperation?.let { "keyID=${it.keyID}, quantity=${it.quantityAchete}" } ?: "null"}")
        Log.d(TAG, "  produit.id: ${produit.id}")
        Log.d(TAG, "  colorIndex: $colorIndex")
        Log.d(TAG, "  quantity: $quantity")

        val relatedCouleur = getRelatedCouleur(produit, colorIndex)
        Log.d(TAG, "relatedCouleur.key: ${relatedCouleur.key}")

        val data = zAppComptRepositoryComposable
            .ouvrireProduitEtCouleurVent(
                produit,
                relatedCouleur = relatedCouleur,
            )

        Log.d(TAG, "ouvrireProduitEtCouleurVent returned data:")
        Log.d(TAG, "  bsonObjectId: ${data.bsonObjectId}")
        Log.d(TAG, "  ouvertF2BonVentId: ${data.ouvertF2BonVentId}")

        fCouleurVentOperation?.let { existingOperation ->
            Log.d(TAG, "Using existing FCouleurVentOperation:")
            Log.d(TAG, "  keyID: ${existingOperation.keyID}")
            Log.d(TAG, "  quantityAchete: ${existingOperation.quantityAchete}")
            Log.d(TAG, "  etateActuellementEst: ${existingOperation.etateActuellementEst}")

            // Check if we need to update the quantity in the existing operation
            if (existingOperation.quantityAchete != quantity) {
                Log.d(TAG, "Quantity mismatch! Existing: ${existingOperation.quantityAchete}, New: $quantity")
                val updatedOperation = existingOperation.copy(
                    quantityAchete = quantity,
                    etateActuellementEst = if (quantity == 0)
                        FCouleurVentOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
                    else
                        FCouleurVentOperation.EtateActuellementEst.ChoisiQuantityConfirme
                )
                Log.d(TAG, "Updated operation:")
                Log.d(TAG, "  quantityAchete: ${updatedOperation.quantityAchete}")
                Log.d(TAG, "  etateActuellementEst: ${updatedOperation.etateActuellementEst}")

                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedOperation)
            } else {
                Log.d(TAG, "Quantity matches, using operation as-is")
                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(existingOperation)
            }
        } ?: run {
            Log.d(TAG, "No existing FCouleurVentOperation, creating new one")
            Log.d(TAG, "Calling acheterUneCouleur with quantity: $quantity")

            fCouleurAchatOperationRepositoryComposable
                .acheterUneCouleur(
                    ouvertData = data,
                    relatedCouleur = relatedCouleur,
                    quantity = quantity
                )
        }

        Log.d(TAG, "=== acheterACaSetterCentral END ===")
    }
}
