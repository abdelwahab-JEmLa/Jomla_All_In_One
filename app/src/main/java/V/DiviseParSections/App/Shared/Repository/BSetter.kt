package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.VentOperations

class BSetter(
    private val produitOperations: ProduitOperations,
    private val bonVentOperations: BonVentOperations,
    private val clientOperations: ClientOperations,
    private val ventOperations: VentOperations,
) {

    fun ouvrireNewAppComptOnVentBonVentEtAddLe(clientOldId: Long, newEtate: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) = bonVentOperations.ouvrireNewAppComptOnVentBonVentEtAddLe(clientOldId, newEtate)

    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() = bonVentOperations.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()

    fun ajouteNewBonVent(key: String, clientOldId: Long, etate: GBonVent.EtateActuellementEst) = bonVentOperations.ajouteNewBonVent(key, clientOldId, etate)

    fun updateComptAppErExistKey(key: String, clientOldId: Long, etate: GBonVent.EtateActuellementEst) = bonVentOperations.updateComptAppErExistKey(key, clientOldId, etate)

    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey() = bonVentOperations.clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey()

    fun cleanFermeAppComptOnVentBonVent() = bonVentOperations.clear_bOuvertDialogMapMarqueHClientKey()

    fun update_bOuvertDialogMapMarqueHClientKey(clientID: Long) = clientOperations.update_bOuvertDialogMapMarqueHClientKey(clientID)

    fun ouvreExistedDataEtNavigatePanie(keyID: String) = clientOperations.ouvreExistedDataEtNavigatePanie(keyID)

    fun deleteAddMultiClients() = clientOperations.deleteAddMultiClients()
    fun deleteAddMultiDatas() = produitOperations.deleteAddMultiDatas()

    fun getKeyID8BonVent(clientId: Long, etate: GBonVent.EtateActuellementEst): String = bonVentOperations.getKeyID8BonVent(clientId, etate)

    fun upsertBonVent(keyHandBonVent: String) = bonVentOperations.upsertBonVent(keyHandBonVent)

    fun acheterACaSetterCentral(fCouleurVentOperation: FCouleurVentOperationInfos? = null, produit: ArticlesBasesStatsTable, colorIndex: Int, quantity: Int) = ventOperations.acheterACaSetterCentral(fCouleurVentOperation, produit, colorIndex, quantity)

    fun updateListRelativeVentCouleurPrixVent(produitKey: String?, newPrix: Double) = ventOperations.updateListRelativeVentCouleurPrixVent(produitKey, newPrix)

    fun deleteVents(parentProduitOldId: Long) = ventOperations.deleteVents(parentProduitOldId)

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) = ventOperations.toggleEtateDeliveryNonTrouveVentOu(produitKey)

    companion object {
        fun regexReturnParentKeysMap(input: String): Map<String, String> =
            Regex("(\\w+)-(\\w+)").findAll(input).associate { match ->
            val (key, value) = match.destructured
            key to value
        }

        fun genereUnPushKeyFireBase(ref: Any): String = "generated_key_${System.currentTimeMillis()}"
    }
}
