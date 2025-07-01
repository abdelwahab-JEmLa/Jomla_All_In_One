package V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.AGetter
import V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID9VentCouleurOperation.Repository.FVentCouleurOperationRepository

class VentOperations(
    private val getter: AGetter,
    private val fVentCouleurOperationRepository: FVentCouleurOperationRepository
) {
    val zAppComptRepositoryComposable = getter.zAppComptRepositoryComposable



    fun updateListRelativeVentCouleurPrixVent(
        produitKey: String?,
        newPrix: Double
    ) {
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
