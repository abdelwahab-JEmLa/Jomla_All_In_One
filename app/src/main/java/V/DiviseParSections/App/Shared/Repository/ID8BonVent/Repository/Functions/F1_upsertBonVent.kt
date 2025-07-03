package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.A.Base.BSetterFacade.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.ID2ClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Id8BonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

fun upsertBonVent(
    keyByParentBonVentOnClickButton: String = "",
    gBonVentRepository: Id8BonVentRepository,
    hClientRepository: ID2ClientRepository,
    parametresAppComptNonSaved: ParametresAppComptNonSaved,
) {
    val activePeriodKeyByParent = parametresAppComptNonSaved.activePeriodKeyByParent
    val keyModelToOnVentHVentPeriodKeyByParent =
        Z_AppCompt.keyModelValID7VentParent + "-" + activePeriodKeyByParent

    val existingData = gBonVentRepository.datasValue.find {
        it.keyByParent == keyByParentBonVentOnClickButton
    }

    val data =
        existingData?.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
            ?: run {
                val getKeyByParentDe =
                    getListDesParentKeys(keyByParentBonVentOnClickButton)
                val parentID2ClientKeyByParent = getKeyByParentDe[HClientInfos.keyModel]!!
                val client =
                    hClientRepository.findHClientInfosByKeyDeClient(parentID2ClientKeyByParent)
                val parentID8C2TypeTransactionKeyByParent =
                    getKeyByParentDe[GBonVent.EtateActuellementEst.keyModel]!!

                GBonVent(
                    keyByParent = keyByParentBonVentOnClickButton,
                    parentID7VentPeriodeKeyByParent = keyModelToOnVentHVentPeriodKeyByParent,
                    parentHClientOldID = client.id,
                    parentID2ClientKeyByParent = parentID2ClientKeyByParent,
                    parentID8C2TypeTransactionKeyByParent = parentID8C2TypeTransactionKeyByParent,
                    etateActuellementEst = findEtateParKeyByParent(
                        parentID8C2TypeTransactionKeyByParent
                    )
                )
            }

    gBonVentRepository.upsert(data)
}

private fun findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent: String): GBonVent.EtateActuellementEst {
    return try {
        GBonVent.EtateActuellementEst.valueOf(parentID8C2TypeTransactionKeyByParent)
    } catch (e: IllegalArgumentException) {
        when (parentID8C2TypeTransactionKeyByParent.uppercase()) {
            "ON_MODE_COMMEND_ACTUELLEMENT" -> GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            "A_COMMANDE_CONFIRME" -> GBonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            "POURVOIRPANIE" -> GBonVent.EtateActuellementEst.PourVoirPanie
            "COMMANDE_LIVRAI" -> GBonVent.EtateActuellementEst.COMMANDE_LIVRAI
            "AVEC_MARCHANDISE" -> GBonVent.EtateActuellementEst.AVEC_MARCHANDISE
            "ACHETEUR_NON_DISPO" -> GBonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
            "FERME" -> GBonVent.EtateActuellementEst.FERME
            "A_EVITE" -> GBonVent.EtateActuellementEst.A_EVITE
            "RAPPORT_AU_ENREGESTREMENT_VOCALE" -> GBonVent.EtateActuellementEst.RAPPORT_AU_ENREGESTREMENT_VOCALE
            "ON_MODE_VOIRE_PANIE_ARTICLES" -> GBonVent.EtateActuellementEst.ON_MODE_VOIRE_PANIE_ARTICLES
            "CIBLE" -> GBonVent.EtateActuellementEst.Cible
            "CIBLE_PRIORITE_2" -> GBonVent.EtateActuellementEst.CIBLE_PRIORITE_2
            "CIBLE_PRIORITE_3" -> GBonVent.EtateActuellementEst.CIBLE_PRIORITE_3
            "CIBLE_POUR_2" -> GBonVent.EtateActuellementEst.CIBLE_POUR_2
            else -> {
                // If no match found, return the default state
                GBonVent.EtateActuellementEst.CreeMaisNonDefinie
            }
        }
    }
}
