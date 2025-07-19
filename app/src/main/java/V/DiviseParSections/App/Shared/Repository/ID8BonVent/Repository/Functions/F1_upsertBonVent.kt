package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.getListDesParentKeys
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt

fun upsertBonVent(
    keyByParentBonVentOnClickButton: String = "",
    gBonVentRepository: Repo8BonVent,
    hClientRepository: Repo2Client,
    parametresAppComptNonSaved: Repo18ParametresAppComptNonSaved,
) {
    val activePeriodKeyByParent = "parametresAppComptNonSaved.keyIdId7VentPeriod"
    val keyModelToOnVentHVentPeriodKeyByParent =
        Z_AppCompt.keyModelValID7VentParent + "-" + activePeriodKeyByParent

    val existingData = gBonVentRepository.datasValue.find {
        it.keyID == keyByParentBonVentOnClickButton
    }

    val data =
        existingData?.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
            ?: run {
                val getKeyByParentDe =
                    getListDesParentKeys(keyByParentBonVentOnClickButton)
                val parentID2ClientKeyByParent = getKeyByParentDe[M2Client.keyModel]!!
                val client =
                    hClientRepository.findHClientInfosByKeyDeClient(parentID2ClientKeyByParent)
                val parentID8C2TypeTransactionKeyByParent =
                    getKeyByParentDe[M8BonVent.EtateActuellementEst.keyModel]!!

                M8BonVent(
                    keyID = keyByParentBonVentOnClickButton,
                    parent_M14VentPeriod_KeyId = keyModelToOnVentHVentPeriodKeyByParent,
                    parent_M2Client_OldLongID = client.id,
                    parent_M2Client_KeyID = parentID2ClientKeyByParent,
                    parentID8C2TypeTransactionKeyByParent = parentID8C2TypeTransactionKeyByParent,
                    etateActuellementEst = findEtateParKeyByParent(
                        parentID8C2TypeTransactionKeyByParent
                    )
                )
            }

    gBonVentRepository.upsert(data)
}

private fun findEtateParKeyByParent(parentID8C2TypeTransactionKeyByParent: String): M8BonVent.EtateActuellementEst {
    return try {
        M8BonVent.EtateActuellementEst.valueOf(parentID8C2TypeTransactionKeyByParent)
    } catch (e: IllegalArgumentException) {
        when (parentID8C2TypeTransactionKeyByParent.uppercase()) {
            "ON_MODE_COMMEND_ACTUELLEMENT" -> M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            "A_COMMANDE_CONFIRME" -> M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            "POURVOIRPANIE" -> M8BonVent.EtateActuellementEst.PourVoirPanie
            "COMMANDE_LIVRAI" -> M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
            "AVEC_MARCHANDISE" -> M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE
            "ACHETEUR_NON_DISPO" -> M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
            "FERME" -> M8BonVent.EtateActuellementEst.FERME
            "A_EVITE" -> M8BonVent.EtateActuellementEst.A_EVITE
            "RAPPORT_AU_ENREGESTREMENT_VOCALE" -> M8BonVent.EtateActuellementEst.RAPPORT_AU_ENREGESTREMENT_VOCALE
            "ON_MODE_VOIRE_PANIE_ARTICLES" -> M8BonVent.EtateActuellementEst.ON_MODE_VOIRE_PANIE_ARTICLES
            "CIBLE" -> M8BonVent.EtateActuellementEst.Cible
            "CIBLE_PRIORITE_2" -> M8BonVent.EtateActuellementEst.CIBLE_PRIORITE_2
            "CIBLE_PRIORITE_3" -> M8BonVent.EtateActuellementEst.CIBLE_PRIORITE_3
            "CIBLE_POUR_2" -> M8BonVent.EtateActuellementEst.CIBLE_POUR_2
            else -> {
                // If no match found, return the default state
                M8BonVent.EtateActuellementEst.CreeMaisNonDefinie
            }
        }
    }
}
