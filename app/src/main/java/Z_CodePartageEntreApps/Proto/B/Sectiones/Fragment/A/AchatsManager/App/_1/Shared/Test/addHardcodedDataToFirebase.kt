package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Test

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository

suspend fun addHardcodedDataToFirebase(
    _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    _1_3_BonAchat_Repository: _1_3_BonAchat_Repository,
    _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository,
    active: Boolean = false
) {     /*
    try {
        // Create _1_4_PeriodeVent test data first (top-level entity)
        val periodeVentTestData = listOf(
            _1_4_PeriodeVent(
                vid = 601L,
                startDateInString = "2025-01-01",
                endDateInString = "2025-01-31"
            ),
            _1_4_PeriodeVent(
                vid = 602L,
                startDateInString = "2025-02-01",
                endDateInString = "2025-02-28"
            ),
            _1_4_PeriodeVent(
                vid = 603L,
                startDateInString = "2025-03-01",
                endDateInString = "2025-03-31"
            ),
            _1_4_PeriodeVent(
                vid = 604L,
                startDateInString = "2025-04-01",
                endDateInString = "2025-04-30"
            )
        )

        // Create _1_3_BonAchat test data with relations to _1_4_PeriodeVent
        val bonAchatTestData = listOf(
            // Original entries
            _1_3_BonAchat(
                vid = 501L,
                clientAchteurID = 301L,
                parent_1_4_PeriodeVentVid = 601L,
                heurDebutInString = "08:00",
                heurFinInString = "17:00"
            ),
            _1_3_BonAchat(
                vid = 502L,
                clientAchteurID = 302L,
                parent_1_4_PeriodeVentVid = 602L,
                heurDebutInString = "09:00",
                heurFinInString = "18:00"
            ),
            _1_3_BonAchat(
                vid = 503L,
                clientAchteurID = 303L,
                parent_1_4_PeriodeVentVid = 602L,
                heurDebutInString = "10:00",
                heurFinInString = "19:00"
            ),
            _1_3_BonAchat(
                vid = 504L,
                clientAchteurID = 304L,
                parent_1_4_PeriodeVentVid = 603L,
                heurDebutInString = "08:30",
                heurFinInString = "16:30"
            ),
            _1_3_BonAchat(
                vid = 505L,
                clientAchteurID = 305L,
                parent_1_4_PeriodeVentVid = 604L,
                heurDebutInString = "09:30",
                heurFinInString = "17:30"
            ),
            _1_3_BonAchat(
                vid = 506L,
                clientAchteurID = 306L,
                parent_1_4_PeriodeVentVid = 604L,
                heurDebutInString = "07:00",
                heurFinInString = "15:00"
            ),
            _1_3_BonAchat(
                vid = 507L,
                clientAchteurID = 307L,
                parent_1_4_PeriodeVentVid = 604L,
                heurDebutInString = "11:00",
                heurFinInString = "20:00"
            )
        )

        // Create _1_2_ProduitAcheteOperation test data with relations to _1_3_BonAchat
        val produitTestData = listOf(
            // Original entries
            _1_2_ProduitAcheteOperation(
                vid = 201L,
                produitAcheterID = 1001L,
                parent_1_3_BonAchat = 501L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            ),
            _1_2_ProduitAcheteOperation(
                vid = 202L,
                produitAcheterID = 1002L,
                parent_1_3_BonAchat = 501L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_2_ProduitAcheteOperation(
                vid = 203L,
                produitAcheterID = 1003L,
                parent_1_3_BonAchat = 502L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_2_ProduitAcheteOperation(
                vid = 204L,
                produitAcheterID = 1004L,
                parent_1_3_BonAchat = 502L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            ),
            _1_2_ProduitAcheteOperation(
                vid = 205L,
                produitAcheterID = 1005L,
                parent_1_3_BonAchat = 503L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_2_ProduitAcheteOperation(
                vid = 206L,
                produitAcheterID = 1006L,
                parent_1_3_BonAchat = 503L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
            ),
            _1_2_ProduitAcheteOperation(
                vid = 207L,
                produitAcheterID = 1007L,
                parent_1_3_BonAchat = 504L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_2_ProduitAcheteOperation(
                vid = 208L,
                produitAcheterID = 1008L,
                parent_1_3_BonAchat = 505L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
            ),
            _1_2_ProduitAcheteOperation(
                vid = 209L,
                produitAcheterID = 1009L,
                parent_1_3_BonAchat = 506L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            ),
            _1_2_ProduitAcheteOperation(
                vid = 210L,
                produitAcheterID = 1010L,
                parent_1_3_BonAchat = 507L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_2_ProduitAcheteOperation(
                vid = 250L,
                produitAcheterID = 1050L,
                parent_1_3_BonAchat = 501L,
                etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            )
        )

        // Create _1_1_CouleurAcheteOperation test data with relations to _1_2_ProduitAcheteOperation
        val couleurTestData = listOf(
            // Original entries
            _1_1_CouleurAcheteOperation(
                vid = 1L,
                couleurId = 101L,
                parent_1_2_ProduitAcheteOperationID = 201L,
                totaleQuantity = 50
            ),
            _1_1_CouleurAcheteOperation(
                vid = 2L,
                couleurId = 102L,
                parent_1_2_ProduitAcheteOperationID = 201L,
                totaleQuantity = 30
            ),
            _1_1_CouleurAcheteOperation(
                vid = 3L,
                couleurId = 103L,
                parent_1_2_ProduitAcheteOperationID = 202L,
                totaleQuantity = 25
            ),
            _1_1_CouleurAcheteOperation(
                vid = 4L,
                couleurId = 104L,
                parent_1_2_ProduitAcheteOperationID = 203L,
                totaleQuantity = 40
            ),
            _1_1_CouleurAcheteOperation(
                vid = 5L,
                couleurId = 105L,
                parent_1_2_ProduitAcheteOperationID = 204L,
                totaleQuantity = 35,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_1_CouleurAcheteOperation(
                vid = 6L,
                couleurId = 106L,
                parent_1_2_ProduitAcheteOperationID = 204L,
                totaleQuantity = 45,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            ),
            _1_1_CouleurAcheteOperation(
                vid = 7L,
                couleurId = 107L,
                parent_1_2_ProduitAcheteOperationID = 205L,
                totaleQuantity = 60,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_1_CouleurAcheteOperation(
                vid = 8L,
                couleurId = 108L,
                parent_1_2_ProduitAcheteOperationID = 206L,
                totaleQuantity = 20,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
            ),
            _1_1_CouleurAcheteOperation(
                vid = 9L,
                couleurId = 109L,
                parent_1_2_ProduitAcheteOperationID = 207L,
                totaleQuantity = 55,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_1_CouleurAcheteOperation(
                vid = 10L,
                couleurId = 110L,
                parent_1_2_ProduitAcheteOperationID = 208L,
                totaleQuantity = 15,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPP_AU_PANIER_FINALE
            ),
            _1_1_CouleurAcheteOperation(
                vid = 11L,
                couleurId = 111L,
                parent_1_2_ProduitAcheteOperationID = 209L,
                totaleQuantity = 70,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
            ),
            _1_1_CouleurAcheteOperation(
                vid = 12L,
                couleurId = 112L,
                parent_1_2_ProduitAcheteOperationID = 210L,
                totaleQuantity = 65,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CONFIRME
            ),
            _1_1_CouleurAcheteOperation(
                vid = 15L,
                couleurId = 120L,
                parent_1_2_ProduitAcheteOperationID = 250L,
                totaleQuantity = 65,
                etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CONFIRME
            )
        )
        if (active) {
            // Update repositories in order from top level to bottom level
            // First _1_4_PeriodeVent (top level)
            withContext(Dispatchers.IO) {
                val snapListPeriodeVent = mutableStateListOf<_1_4_PeriodeVent>()
                snapListPeriodeVent.addAll(periodeVentTestData)
                _1_4_PeriodeVent_Repository.updateMultiDatas(snapListPeriodeVent)
                Log.d(
                    TAG,
                    "Added ${periodeVentTestData.size} hardcoded PeriodeVent items to repository"
                )
            }

            // Then _1_3_BonAchat (depends on _1_4_PeriodeVent)
            withContext(Dispatchers.IO) {
                val snapListBonAchat = mutableStateListOf<_1_3_BonAchat>()
                snapListBonAchat.addAll(bonAchatTestData)
                _1_3_BonAchat_Repository.updateMultiDatas(snapListBonAchat)
                Log.d(
                    TAG,
                    "Added ${bonAchatTestData.size} hardcoded BonAchat items to repository"
                )
            }

            // Then _1_2_ProduitAcheteOperation (depends on _1_3_BonAchat)
            withContext(Dispatchers.IO) {
                val snapListProduit = mutableStateListOf<_1_2_ProduitAcheteOperation>()
                snapListProduit.addAll(produitTestData)
                _1_2_ProduitAcheteOperation_Repository.updateMultiDatas(snapListProduit)
                Log.d(
                    TAG,
                    "Added ${produitTestData.size} hardcoded ProduitAcheteOperation items to repository"
                )
            }

            // Finally _1_1_CouleurAcheteOperation (depends on _1_2_ProduitAcheteOperation)
            withContext(Dispatchers.IO) {
                val snapListCouleur = mutableStateListOf<_1_1_CouleurAcheteOperation>()
                snapListCouleur.addAll(couleurTestData)
                _1_1_CouleurAcheteOperation_Repository.updateMultiDatas(snapListCouleur)
                Log.d(
                    TAG,
                    "Added ${couleurTestData.size} hardcoded CouleurAcheteOperation items to repository"
                )
            }
        }
    } catch (e: Exception) {
    } finally {

 */
}
