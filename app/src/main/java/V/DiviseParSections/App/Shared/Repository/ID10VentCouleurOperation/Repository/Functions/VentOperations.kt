package V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur

class VentOperations(
    private val getter: AGetter,
    private val fVentCouleurOperationRepository: Repo10OperationVentCouleur
) {
    val zAppComptRepositoryComposable = getter.id9AppComptRepository



    fun updateListRelativeVentCouleurPrixVent(
        produitKey: String?,
        newPrix: Double
    ) {
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
            getter.bProduitInfosRepository.datasValue.find { it.id == parentProduitOldId }?.keyID
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

}
