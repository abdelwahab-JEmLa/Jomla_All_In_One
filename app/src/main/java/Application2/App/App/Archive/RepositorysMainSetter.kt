package Application2.App.App.Archive

import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
import EntreApps.Shared.Models.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import EntreApps.Shared.Models.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import com.google.firebase.database.DatabaseReference

class RepositorysMainSetter_app2(
    private val repoM1Produit: RepoM1Produit,
    private val repo2Client: Repo2Client,
    private val repo03CouleurProduitInfos: Repo03CouleurProduitInfos,
    val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    val repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo11AchatOperation: Repo11AchatOperation,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    val repo14VentPeriode: Repo14VentPeriode,
    private val repoM16CategorieProduit: RepoM16CategorieProduit,
) {
//--------------------By.Repo.Position-----------------------------------------------------------------------------------------------------------------------------
    //--------------------Repo9----------------------------------------------------------------------------------------------------------------------------
    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
    produit: M01Produit,
    currentAppCompt: M09AppCompt
    ) {
        val updatedAppCompt = currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produit.keyID,
        )
        repo9AppCompt.upsert(updatedAppCompt)
    }


    fun updateListM10OperationVentCouleur(listFocusedM10OpeVentCouleurParPrixDifineur: List<M10OperationVentCouleur>) {
        listFocusedM10OpeVentCouleurParPrixDifineur.forEach {
            repo10OperationVentCouleur.addOrUpdateData(it)
        }
    }

    fun upsert_M10OperationVentCouleur(data: M10OperationVentCouleur) {
        repo10OperationVentCouleur
            .update_If_Exist(data)
    }

//------------------------------------------------------------------------------------------------------------------------------------------------

    fun saveTariff_Et_RelateIt_Au_Vents_Correspond(
        m13TarificationInfos_Pour_Produit: M13TarificationInfos?,
        m10OperationVentCouleurs: List<M10OperationVentCouleur>,
    ) {
        m13TarificationInfos_Pour_Produit?.let {
            upsert_M13TarificationInfos(it)

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




    fun addAuRepoM9AppComptParFacade(defaultGeneratedCompt: M09AppCompt) {
        repo9AppCompt.addNew(defaultGeneratedCompt)
    }


    fun addNewM14VentPeriode(generatedDefaultM14: M14VentPeriode) {
        repo14VentPeriode.upsert(generatedDefaultM14)
    }

    //------------repo1 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M1Produit(data: M01Produit) = repoM1Produit.upsert(data)
    fun update2_M1Produit(data: M01Produit) = repoM1Produit.update(data)

    //------------repo2 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M2Client(data: M2Client) = repo2Client.upsert(data)
    fun delete_M2Client(data: M2Client) = repo2Client.delete_M2Client(data)

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
    fun refresh_Datas_M8BonVent() = repo8BonVent.refresh_Datas()

    fun delete_M8BonVent(data: M8BonVent) {
        repo8BonVent.delete(data)
    }

    fun update_M9AppCompt(data: M09AppCompt) = repo9AppCompt.upsert(data)

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

    //------------repo_M13TarificationInfos -------------------------------------------------------------------------------------------------------------------------------------
    fun add_M13TarificationInfos(data: M13TarificationInfos) = repo13TarificationInfos.upsert(data)


    fun upsert_M13TarificationInfos(data: M13TarificationInfos) =
        repo13TarificationInfos.upsert(data)

    fun update_M13TarificationInfos(data: M13TarificationInfos) =
        repo13TarificationInfos.upsert(data)

    //------------repo8BonVent -------------------------------------------------------------------------------------------------------------------------------------
    fun update_M14VentPeriode(data: M14VentPeriode) = repo14VentPeriode.update_If_Exist(data)
    fun upsert_M14VentPeriode(data: M14VentPeriode) = repo14VentPeriode.upsert(data)

    fun delete(data: M14VentPeriode) = repo14VentPeriode.delete(data)


    //------------R16 -------------------------------------------------------------------------------------------------------------------------------------
    fun upsert_M16CategorieProduit(data: M16CategorieProduit) =
        repoM16CategorieProduit.addOrUpdateData(data)

    fun addOrUpdateDatas_M16CategorieProduit(datas: List<M16CategorieProduit>) =
        repoM16CategorieProduit.addOrUpdateDatas(datas, true)


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
