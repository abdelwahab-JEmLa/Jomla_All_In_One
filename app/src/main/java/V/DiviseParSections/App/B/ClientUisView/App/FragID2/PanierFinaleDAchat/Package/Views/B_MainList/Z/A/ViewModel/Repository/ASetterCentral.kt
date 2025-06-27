package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository


class ASetterCentral(
    val getter: ACentralCompoRepositoryProtoJuin9,
) {
    fun getRelatedCouleur(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        getter.getRelatedCouleur(produit, colorIndex)

    val zAppComptRepositoryComposable = getter.zAppComptRepositoryComposable
    val bClientsStateCompoRepository = getter.bClientsStateCompoRepository

    fun ouvrireUnBonVent(clientOldId: Long): Unit {
        val client = bClientsStateCompoRepository.datasValue.find { it.id == clientOldId }!!
        zAppComptRepositoryComposable.addOrUpdateData(
            zAppComptRepositoryComposable.ouvertData!!.copy(
                ouvertClientOnVentKey = client.keyID,
                ouvertClientOnVentAncienId = client.id,
                ouvertClientOnVentNom = client.nom,
            )
        )
    }

    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperation? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int,
    ) {
        val relatedCouleur = getRelatedCouleur(produit, colorIndex)
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

    fun updateQuantity(ventKey: String): Unit {

    }
}
