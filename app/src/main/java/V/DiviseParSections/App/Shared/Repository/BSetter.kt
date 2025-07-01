package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.Bsetter.Helper.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.getKeyID8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.upsertBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import com.google.firebase.database.DatabaseReference

class BSetter(
    private val getter: AGetter,
    private val produitOperations: ProduitOperations,
    val id8BonVentOperations: BonVentOperations,
    private val clientOperations: ClientOperations,
    private val ventOperations: VentOperations,
) {
    val parametresAppComptNonSaved = getter.parametresAppComptNonSaved
    val hClientRepository = getter.hClientRepository


    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() =
        id8BonVentOperations.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()

    fun ajouteNewBonVent(key: String, clientOldId: Long, etate: GBonVent.EtateActuellementEst) =
        id8BonVentOperations.ajouteNewBonVent(key, clientOldId, etate)

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: GBonVent.EtateActuellementEst
    ) = id8BonVentOperations.updateComptAppErExistKey(key, clientOldId, etate)

    fun clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey() =
        id8BonVentOperations.clear_onVentGBonVentKeyId_EtbOuvertDialogMapMarqueHClientKey()

    fun cleanFermeAppComptOnVentBonVent() =
        id8BonVentOperations.clear_bOuvertDialogMapMarqueHClientKey()

    fun update_bOuvertDialogMapMarqueHClientKey(clientID: Long) =
        clientOperations.update_bOuvertDialogMapMarqueHClientKey(clientID)

    fun ouvreExistedDataEtNavigatePanie(keyID: String) =
        clientOperations.ouvreExistedDataEtNavigatePanie(keyID)

    fun deleteAddMultiClients() = clientOperations.deleteAddMultiClients()
    fun deleteAddMultiDatas() = produitOperations.deleteAddMultiDatas()

    fun getKeyID8BonVentSetter(clientId: Long, etate: GBonVent.EtateActuellementEst): String =
        getKeyID8BonVent(
            clientId, etate,
            parametresAppComptNonSaved = parametresAppComptNonSaved,
            hClientRepository = hClientRepository,
        )

    fun upsertBonVentSetter(keyHandBonVent: String) {
        upsertBonVent(
            keyHandBonVent,
            gBonVentRepository = getter.gBonVentRepository,
            hClientRepository = hClientRepository,
            parametresAppComptNonSaved
        )
    }

    fun acheterACaSetterCentral(
        fCouleurVentOperation: FCouleurVentOperationInfos? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int
    ) = ventOperations.acheterACaSetterCentral(fCouleurVentOperation, produit, colorIndex, quantity)

    fun updateListRelativeVentCouleurPrixVent(produitKey: String?, newPrix: Double) =
        ventOperations.updateListRelativeVentCouleurPrixVent(produitKey, newPrix)

    fun deleteVents(parentProduitOldId: Long) = ventOperations.deleteVents(parentProduitOldId)

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) =
        ventOperations.toggleEtateDeliveryNonTrouveVentOu(produitKey)

    companion object {
        fun getListDesParentKeys(keyByParent :String): Map<String, String> =
            Regex("(\\w+)-(\\w+)").findAll(keyByParent).associate { match ->
                val (key, value) = match.destructured
                key to value
            }

        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}

