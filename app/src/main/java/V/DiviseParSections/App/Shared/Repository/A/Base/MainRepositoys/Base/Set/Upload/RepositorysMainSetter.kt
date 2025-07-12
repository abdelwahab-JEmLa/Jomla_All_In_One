package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.toggleEtateDeliveryNonTrouveVentOu
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.updateListRelativeVentCouleurPrixVent
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.upsertVentCouleurOperation
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import com.google.firebase.database.DatabaseReference

class RepositorysMainSetter(
    private val getter: RepositorysMainGetter,
    private val focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    private val produitOperations: ProduitOperations,
    private val clientOperations: ClientOperations,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo11AchatOperation: Repo11AchatOperation,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
) {
    private val get = focusedVarsHandlerFacade.focusedValuesGetter

    fun saveTariff_Et_RelateIt_Au_Vents_Correspond(
        m13TarificationInfos_Pour_Produit: M13TarificationInfos?,
        m10OperationVentCouleurs: List<M10OperationVentCouleur>
    ) {

        m13TarificationInfos_Pour_Produit?.let {
            addOrUpdateGroAliTariff(it)

            val listFocusedM10OpeVentCouleurParPrixDifineur =
                m10OperationVentCouleurs.map { listVent ->
                    listVent.copy(
                        parentM13TarificationDebugInfos = m13TarificationInfos_Pour_Produit.getDebugInfos(),
                        parentM13TarificationKeyID = m13TarificationInfos_Pour_Produit.keyID,
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

    fun updateListRelativeVentCouleurPrixVentFacade(
        m1produitInfos: ArticlesBasesStatsTable?,
        newPrix: Double
    ) {
        if (m1produitInfos != null) {
            updateListRelativeVentCouleurPrixVent(
                repo10OperationVentCouleur,
                filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali= focusedVarsHandlerFacade.focusedValuesGetter.filtered_ListM10Vent_BY_Curr_M14VentPeriod ,
                m1produitInfos.keyID,
                newPrix)
        }
    }

    fun updateListM10OperationVentCouleur(listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>) {
        listFocusedM10OpeVentCouleurParPrixDifineur.forEach {
            repo10OperationVentCouleur.addOrUpdateData(it)
        }
    }



    fun toggleEtateDeliveryNonTrouveVentOuFacade(produitKey: String) =
        toggleEtateDeliveryNonTrouveVentOu(
            repo10OperationVentCouleur=repo10OperationVentCouleur,
            filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali= focusedVarsHandlerFacade.focusedValuesGetter.filtered_ListM10Vent_BY_Curr_M14VentPeriod ,
            produitKey=produitKey)

    fun addAuRepoM9AppComptParFacade(defaultGeneratedCompt: Z_AppCompt) {
        repo9AppCompt.addNew(defaultGeneratedCompt)
    }

    fun addOrUpdateGroAliTariff(latestTariffLocalData: M13TarificationInfos) {
        repo13TarificationInfos.upsert(latestTariffLocalData)
    }


    fun addNewM14VentPeriode(generatedDefaultM14: M14VentPeriode) {
        repo14VentPeriode.upsert(generatedDefaultM14)
    }

    //------------repo8BonVent -------------------------------------------------------------------------------------------------------------------------------------
    fun add_New_Setter(data: M8BonVent) = repo8BonVent.addNew(data)
    fun update_IfExist_Setter(data: M8BonVent) = repo8BonVent.updateIfExist(data)
    //------------repo3 -------------------------------------------------------------------------------------------------------------------------------------
    fun add_New_M10OperationVentCouleur(data: M10OperationVentCouleur) = repo10OperationVentCouleur.add_New(data)
    fun m10OperationVentCouleur_update_IfExist(data: M10OperationVentCouleur) = repo10OperationVentCouleur.update_If_Exist(data)
    //--------------------------------------m10--------------------------------------------------------------------------------------------------------
    fun m10_delete(ventOperationsForProduct: List<M10OperationVentCouleur>) {
        ventOperationsForProduct.map {
            repo10OperationVentCouleur.delete(it)
        }
    }

    //------------repo11 -------------------------------------------------------------------------------------------------------------------------------------
    fun repo11AchatOperation_add_New(data: M11AchatOperation) = repo11AchatOperation.add_New(data)
    fun repo11AchatOperation_deleteMulti(datas:List<M11AchatOperation> ) = repo11AchatOperation.deleteMulti(datas)
    //------------repo11 -------------------------------------------------------------------------------------------------------------------------------------


    fun upsertM8BonVent(data: M8BonVent) {
        repo8BonVent.upsert(data)
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
