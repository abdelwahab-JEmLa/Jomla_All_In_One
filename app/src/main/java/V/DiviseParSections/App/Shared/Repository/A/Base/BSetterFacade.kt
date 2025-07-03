package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.upsertVentCouleurOperation
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.getKeyID8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.upsertBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import com.google.firebase.database.DatabaseReference

class BSetterFacade(
    private val getter: AGetter,
    private val produitOperations: ProduitOperations,
    val id8BonVentOperations: BonVentOperations,
    private val clientOperations: ClientOperations,
    private val ventOperations: VentOperations,
) {
    val parametresAppComptNonSaved = getter.parametresAppComptNonSaved
    val hClientRepository = getter.iD2ClientRepository

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


    fun lenceNeveauBonVentFacade(keyHandBonVent: String, id8BonVent: GBonVent? =null) {
        val id8BonVentRepository= getter.id8BonVentRepository
        val datas= getter.id8BonVentRepository.datasValue
        id8BonVent?.let {
            id8BonVentRepository.defaultId8BonVent.copy(

            )
        }

        upsertBonVent(
            keyHandBonVent,
            gBonVentRepository = getter.id8BonVentRepository,
            hClientRepository = hClientRepository,
            parametresAppComptNonSaved
        )
    }

    fun upsertVentCouleurOperationFacade(
        fCouleurVentOperation: FCouleurVentOperationInfos? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int
    ) {
        upsertVentCouleurOperation(
            fCouleurVentOperation,
            produit,
            colorIndex,
            quantity,
            getter.id9AppComptRepository,
            getter,
        )
    }

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
