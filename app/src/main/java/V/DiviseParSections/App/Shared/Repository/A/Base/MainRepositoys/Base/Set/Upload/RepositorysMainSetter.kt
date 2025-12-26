package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ClientOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.ProduitOperations
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.toggleEtateDeliveryNonTrouveVentOu
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.updateListRelativeVentCouleurPrixVent
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Functions.upsertVentCouleurOperation
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.Repo15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.nmRepo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateData
import com.google.firebase.database.DatabaseReference

class RepositorysMainSetter(
    private val getter: RepositorysMainGetter,
    private val focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    private val produitOperations: ProduitOperations,
    private val clientOperations: ClientOperations,
    private val repoM1Produit: RepoM1Produit,
    private val repo2Client: Repo2Client,
    private val repo03CouleurProduitInfos: Repo03CouleurProduitInfos,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo11AchatOperation: Repo11AchatOperation,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    val repo14VentPeriode: Repo14VentPeriode,
    private val repo15Grossist: Repo15Grossist,
    private val repoM16CategorieProduit: RepoM16CategorieProduit,
    private val repo17MessageVocale: Repo17MessageVocale,
    private val repo19Etudiant: Repo19Etudiant,
    private val repo20ObsarvationEtudion: Repo20ObsarvationEtudion,
) {
    private val get = focusedVarsHandlerFacade.focusedValuesGetter
//--------------------By.Repo.Position-----------------------------------------------------------------------------------------------------------------------------
    //--------------------Repo9----------------------------------------------------------------------------------------------------------------------------
    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
        produit: ArticlesBasesStatsTable,
        currentAppCompt: Z_AppCompt
    ) {
        val updatedAppCompt = currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produit.keyID,
        )
        repo9AppCompt.upsert(updatedAppCompt)
    }

    //--------------------Repo10OperationVentCouleur----------------------------------------------------------------------------------------------------------------------------
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
                filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali = focusedVarsHandlerFacade.focusedValuesGetter.filtered_ListM10Vent_BY_Curr_M14VentPeriod,
                m1produitInfos.keyID,
                newPrix
            )
        }
    }

    fun updateListM10OperationVentCouleur(listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>) {
        listFocusedM10OpeVentCouleurParPrixDifineur.forEach {
            repo10OperationVentCouleur.addOrUpdateData(it)
        }
    }

    fun upsert_M10OperationVentCouleur(data: M10OperationVentCouleur) {
        repo10OperationVentCouleur
            .addOrUpdateData(data)
    }

//------------------------------------------------------------------------------------------------------------------------------------------------

    fun saveTariff_Et_RelateIt_Au_Vents_Correspond(
        m13TarificationInfos_Pour_Produit: M13TarificationInfos?,
        m10OperationVentCouleurs: List<M10OperationVentCouleur>,
        aCentralFacade: ACentralFacade
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
    fun deleteAddMultiDatas(list_M1Produit: List<ArticlesBasesStatsTable>) =
        produitOperations.deleteAddMultiDatas(list_M1Produit)


    fun toggleEtateDeliveryNonTrouveVentOuFacade(produitKey: String) =
        toggleEtateDeliveryNonTrouveVentOu(
            repo10OperationVentCouleur = repo10OperationVentCouleur,
            filtered_ListM10Vent_BY_Curr_M14VentPeriod_AND_travailleChezGrossisst3Ali = focusedVarsHandlerFacade.focusedValuesGetter.filtered_ListM10Vent_BY_Curr_M14VentPeriod,
            produitKey = produitKey
        )

    fun addAuRepoM9AppComptParFacade(defaultGeneratedCompt: Z_AppCompt) {
        repo9AppCompt.addNew(defaultGeneratedCompt)
    }

    fun addOrUpdateGroAliTariff(latestTariffLocalData: M13TarificationInfos) {
        repo13TarificationInfos.upsert(latestTariffLocalData)
    }


    fun addNewM14VentPeriode(generatedDefaultM14: M14VentPeriode) {
        repo14VentPeriode.upsert(generatedDefaultM14)
    }

    //------------repo1 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M1Produit(data: ArticlesBasesStatsTable) = repoM1Produit.upsert(data)
    fun update2_M1Produit(data: ArticlesBasesStatsTable) = repoM1Produit.update(data)

    //------------repo2 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M2Client(data: M2Client) = repo2Client.upsert(data)

    //------------repo2 -------------------------------------------------------------------------------------------------------------------------------------
    fun addOrUpdateData_M3CouleurProduitInfos(data: M3CouleurProduitInfos) = repo03CouleurProduitInfos.addOrUpdateData(data)

    //------------repo10 -------------------------------------------------------------------------------------------------------------------------------------
    fun add_New_M10OperationVentCouleur(data: M10OperationVentCouleur) =
        repo10OperationVentCouleur.add_New(data)

    fun m10OperationVentCouleur_update_IfExist(data: M10OperationVentCouleur) =
        repo10OperationVentCouleur.update_If_Exist(data)

    //------------repo8BonVent -------------------------------------------------------------------------------------------------------------------------------------
    fun addNew_M8BonVent(data: M8BonVent) = repo8BonVent.addNew(data)
    fun update_M8BonVent(data: M8BonVent?) = data?.let { repo8BonVent.upsert(it) }

    fun delete_M8BonVent(data: M8BonVent) {
        repo8BonVent.delete(data)
    }

    fun update_M9AppCompt(data: Z_AppCompt) = repo9AppCompt.upsert(data)

    //--------------------------------------m10--------------------------------------------------------------------------------------------------------
    fun update_M10OperationVentCouleur(data: M10OperationVentCouleur) =
        repo10OperationVentCouleur.update_If_Exist(data)

    fun delete_ListM10OperationVentCouleur(datas: List<M10OperationVentCouleur>) {
        datas.map {
            repo10OperationVentCouleur.delete(it)
        }
    }

    //------------repo11 -------------------------------------------------------------------------------------------------------------------------------------
    fun repo11AchatOperation_add_New(data: M11AchatOperation) = repo11AchatOperation.add_New(data)
    fun repo11AchatOperation_deleteMulti(datas: List<M11AchatOperation>) =
        repo11AchatOperation.deleteMulti(datas)

    fun repo11AchatOperation_update_If_Exist(data: M11AchatOperation) =
        repo11AchatOperation.update_If_Exist(data)

    //------------repo8BonVent -------------------------------------------------------------------------------------------------------------------------------------
    fun add_M13TarificationInfos(data: M13TarificationInfos) = repo13TarificationInfos.upsert(data)

    fun upsert_M13TarificationInfos(data: M13TarificationInfos) =
        repo13TarificationInfos.upsert(data)

    //------------repo8BonVent -------------------------------------------------------------------------------------------------------------------------------------
    fun update_M14VentPeriode(data: M14VentPeriode) = repo14VentPeriode.update_If_Exist(data)
    fun upsert_M14VentPeriode(data: M14VentPeriode) = repo14VentPeriode.upsert(data)

    fun delete(data: M14VentPeriode) = repo14VentPeriode.delete(data)

    //------------Funcs.Repo15Grossist -------------------------------------------------------------------------------------------------------------------------------------
    fun repo15Grossist_add_New(data: M15Grossist) = repo15Grossist.add_New(data)
    fun repo15Grossist_update_If_Exist(data: M15Grossist) = repo15Grossist.update_If_Exist(data)
    fun repo15Grossist_deleteMulti(datas: List<M15Grossist>? = null) =
        repo15Grossist.deleteMulti(datas)

    //------------R16 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M16CategorieProduit(data: CategoriesTabelle) =
        repoM16CategorieProduit.addOrUpdateData(data)

    fun addOrUpdateDatas_M16CategorieProduit(datas: List<CategoriesTabelle>) =
        repoM16CategorieProduit.addOrUpdateDatas(datas, true)

    //------------R17 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M17MessageVocale(data: M17MessageVocale) = repo17MessageVocale.addOrUpdateData(data)

    //------------R19 -------------------------------------------------------------------------------------------------------------------------------------
    fun add_M19Etudiant(data: M19Etudiant) = repo19Etudiant.add(data)
    fun upsert_M19Etudiant(data: M19Etudiant) = repo19Etudiant.upsert(data)
    //------------R20 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M20ObsarvationEtudion(data: M20ObsarvationEtudion) = repo20ObsarvationEtudion.upsert(data)
    //-------------------------------------------------------------------------------------------------------------------------------------------------

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
