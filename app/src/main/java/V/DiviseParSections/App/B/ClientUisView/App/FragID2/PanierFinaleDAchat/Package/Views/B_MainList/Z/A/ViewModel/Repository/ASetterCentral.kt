package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import com.google.firebase.database.DatabaseReference


class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
    val gTransactionVentRepository: GTransactionVentRepository,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    fun genereUnPushKeyFireBase(ref: DatabaseReference) = ref.push().key.toString()

    val bClientsStateCompoRepository = getter.fClientRepository

    fun ouvrireUneNewTransactionVent(clientOldId: Long) {
        setParameterAuAppCompt(clientOldId)

        val zAppComptRepositoryComposableOuvertData = zAppComptRepositoryComposable.ouvertData
        if (zAppComptRepositoryComposableOuvertData != null) {
            gTransactionVentRepository.addOrUpdateData(
                with(zAppComptRepositoryComposableOuvertData) {
                    GTransactionVent(
                        keyID = ouvertGTransactionVentKeyId,

                        parentPeriodeVentKeyID = ouvertHPeriodVentKeyId,
                        parentPeriodeVentStartTimestamp = ouvertHPeriodVentTimestamp,

                        parentHClientKeyID = ouvertClientOnVentAncienId,
                        parentZAppComptNom = keyID
                    )
                }
            )
        }
    }

    private fun setParameterAuAppCompt(clientOldId: Long) {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
        zAppComptRepositoryComposable.addOrUpdateData(
            zAppComptRepositoryComposable.ouvertData!!.copy(
                ouvertGTransactionVentKeyId = genereUnPushKeyFireBase(Z_AppCompt.ref),

                onVentFClientKeyID = client.keyID,

                ouvertClientOnVentAncienId = client.id,
                ouvertClientOnVentDebugNameKey = client.nom,
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
                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(updatedOperation)
            } else {
                getter.fCouleurAchatOperationRepositoryComposable.addOrUpdateData(existingOperation)
            }
        } ?: run {
            if (data != null) {
                getter.fCouleurAchatOperationRepositoryComposable.acheterUneCouleur(
                    ouvertData = data,
                    relatedCouleur = relatedCouleur,
                    quantity = quantity
                )
            }
        }
    }
}
