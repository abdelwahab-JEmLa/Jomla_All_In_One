package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedVarsHandlerFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.upsertVentCouleurOperation
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.getKeyID8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.upsertBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import com.google.firebase.database.DatabaseReference

class MainRepositorysSetterFacade(
    private val getter: MainRepositorysGetterFacade,

    val focusedVarsHandlerFacade: FocusedVarsHandlerFacade,

    private val produitOperations: ProduitOperations,
    val id8BonVentOperations: BonVentOperations,
    private val clientOperations: ClientOperations,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo13TarificationInfos: Repo13TarificationInfos,

    private val ventOperations: VentOperations,
) {
    val id8BonVentRepository = getter.id8BonVentRepository
    val parametresAppComptNonSaved = getter.parametresAppComptNonSaved
    val hClientRepository = getter.iD2ClientRepository

    fun saveTariff_Et_RelateIt_Au_Vents_Correspond(
        focused_M13TarificationInfos_Pour_Produit: M13TarificationInfos?,
        m10OperationVentCouleurs: List<M10OperationVentCouleur>
    ) {
        focused_M13TarificationInfos_Pour_Produit?.let {
            addOrUpdateGroAliTariff(it)

            val listFocusedM10OpeVentCouleurParPrixDifineur =
                m10OperationVentCouleurs.map { listVent ->
                    listVent.copy(
                        parentM13TarificationDebugInfos = focused_M13TarificationInfos_Pour_Produit.getDebugInfos(),
                        parentM13TarificationKeyID = focused_M13TarificationInfos_Pour_Produit.keyID,
                        provisoireMonPrix = focused_M13TarificationInfos_Pour_Produit.prixCurrency
                    )
                }

            updateListM10OperationVentCouleur(
                listFocusedM10OpeVentCouleurParPrixDifineur = listFocusedM10OpeVentCouleurParPrixDifineur
            )
        }
    }

    fun dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey() =
        id8BonVentOperations.dismissSansRegleCommandBOuvertDialogMapMarqueHClientKey()

    fun ajouteNewBonVent(key: String, clientOldId: Long, etate: M8BonVent.EtateActuellementEst) =
        id8BonVentOperations.ajouteNewBonVent(key, clientOldId, etate)

    fun updateComptAppErExistKey(
        key: String,
        clientOldId: Long,
        etate: M8BonVent.EtateActuellementEst
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

    fun getKeyID8BonVentSetter(clientId: Long, etate: M8BonVent.EtateActuellementEst): String =
        getKeyID8BonVent(
            clientId, etate,
            parametresAppComptNonSaved = parametresAppComptNonSaved,
            hClientRepository = hClientRepository,
        )


    fun lenceNeveauBonVentFacade(keyHandBonVent: String, m8BonVent: M8BonVent? = null) {
        val newData = m8BonVent?.copy(creationTimestamps = System.currentTimeMillis())

        if (newData != null) {
            focusedVarsHandlerFacade.set.addNewM8BonVent(m8BonVent)
        } else {
            upsertBonVent(
                keyHandBonVent,
                gBonVentRepository = getter.id8BonVentRepository,
                hClientRepository = hClientRepository,
                parametresAppComptNonSaved
            )
        }
    }

    fun upsertVentCouleurOperationFacade(
        fCouleurVentOperation: M10OperationVentCouleur? = null,
        produit: ArticlesBasesStatsTable,
        colorIndex: Int,
        quantity: Int
    ) {
        upsertVentCouleurOperation(
            fCouleurVentOperation,
            produit,
            colorIndex,
            quantity,
            getter.repo9AppCompt,
            getter,
        )
    }

    fun updateListRelativeVentCouleurPrixVent(
        listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>,
        m1produitInfos: ArticlesBasesStatsTable?,
        newPrix: Double
    ) {
        if (m1produitInfos != null) {
            ventOperations.updateListRelativeVentCouleurPrixVent(m1produitInfos.keyID, newPrix)
        }
    }

    fun updateListM10OperationVentCouleur(listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>) {
        listFocusedM10OpeVentCouleurParPrixDifineur.forEach {
            repo10OperationVentCouleur.addOrUpdateData(it)
        }
    }


    fun deleteVents(parentProduitOldId: Long) = ventOperations.deleteVents(parentProduitOldId)

    fun toggleEtateDeliveryNonTrouveVentOu(produitKey: String) =
        ventOperations.toggleEtateDeliveryNonTrouveVentOu(produitKey)

    fun addAuRepoM9AppComptParFacade(defaultGeneratedCompt: Z_AppCompt) {
        repo9AppCompt.add(defaultGeneratedCompt)
    }

    fun addOrUpdateGroAliTariff(latestTariffLocalData: M13TarificationInfos) {
        repo13TarificationInfos.upsert(latestTariffLocalData)
    }

    companion object {
        fun getListDesParentKeys(keyByParent: String): Map<String, String> =
            Regex("(\\w+)-(\\w+)").findAll(keyByParent).associate { match ->
                val (key, value) = match.destructured
                key to value
            }

        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }
    }
}
