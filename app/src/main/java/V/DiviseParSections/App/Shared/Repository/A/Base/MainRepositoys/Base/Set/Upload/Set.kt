package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.Get
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.VentOperations
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.upsertVentCouleurOperation
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions.BonVentOperations
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import com.google.firebase.database.DatabaseReference

class Set(
    private val getter: Get,
    private val ventOperations: VentOperations,
    val bonVentOperations: BonVentOperations,
    private val focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    private val produitOperations: ProduitOperations,
    private val clientOperations: ClientOperations,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
) {
    private val get = focusedVarsHandlerFacade.get

    fun saveTariff_Et_RelateIt_Au_Vents_Correspond(
        focused_M13TarificationInfos_Pour_Produit: M13TarificationInfos? =
            get.focused_M13TarificationInfos_Pour_Produit,
        m10OperationVentCouleurs: List<M10OperationVentCouleur> =
            get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit
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


    fun ouvreExistedDataEtNavigatePanie(keyID: String) =
        clientOperations.ouvreExistedDataEtNavigatePanie(keyID)

    fun deleteAddMultiClients() = clientOperations.deleteAddMultiClients()
    fun deleteAddMultiDatas() = produitOperations.deleteAddMultiDatas()


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
        repo9AppCompt.addNew(defaultGeneratedCompt)
    }

    fun addOrUpdateGroAliTariff(latestTariffLocalData: M13TarificationInfos) {
        repo13TarificationInfos.upsert(latestTariffLocalData)
    }

    fun updateM8BonVent(data: M8BonVent) {
        repo8BonVent.upsert(data)
    }

    fun addNewM14VentPeriode(generatedDefaultM14: M14VentPeriode) {
        repo14VentPeriode.upsert(generatedDefaultM14)
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
