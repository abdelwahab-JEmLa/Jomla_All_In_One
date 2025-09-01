package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.io.File

data class ActiveCentralValues(
    val roleDefinieParSourceACetteFragment: RoleDefinieParSourceACetteFragment? = null,
    val active_OpnerDialog_M17MessageVocale: M17MessageVocale? = null,

    val handled_M10OperationVent_Pour_Link: M10OperationVentCouleur? = null,
    val affiche_Panier_au_Search_Dialog: Boolean = false,

    //-----------------Delete.Safe-------------------------------------------------------------------------------------------------------------
    val affiche_DeleteButtons: Boolean = false,

    //-----------------Produit-------------------------------------------------------------------------------------------------------------
    val active_Catalogue_Pour_NewAddedProduit: CataloguesCaegorie? = null,

    //-----------------Peride-------------------------------------------------------------------------------------------------------------------------
    val held_Period_Pour_copie_Leur_Vents: M14VentPeriode? = null,

    //-----------------Bon8-----------------------------------------------------------------------------------------------------------------------------------------
    val click_On_Marque: Click_On_Marque = Click_On_Marque.Standart,
    val actuelle_Ciblage_MaxPosition: Int = 1,
    val gps_follow_mode_active: Boolean? = false,
    val visibleClientsNow: MapClientsViewModel.VisibleClientsNow? =
        if(M18CentralParametresOfAllApps().au_Lence_Set_Compt_Ac_KeyId==M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId)
        MapClientsViewModel.VisibleClientsNow.AFFICHE_COMMANDE_LIVRAI_Filter else MapClientsViewModel.VisibleClientsNow.showAll,

    //-----------------Repo11AchatOperation-------------------------------------------------------------------------------------------------------------------------
    val active_M14VentPeriode_AuFilterAchats: M14VentPeriode? = null,
    val active_M15Grossist_AuFilterAchats: M15Grossist? = null,
    val active_M2Client_AuFilterAchats: M2Client? = null,
    val active_M1Produit_AuFilterAchats: ArticlesBasesStatsTable? = null,

    val show_Dialog_filter_AChats_Par_Client_Acheteur: Boolean? = false,
    val vent_Au_Dialog_filter_AChats_Par_Client_Acheteur: M14VentPeriode?= null,

    val dialog_achats_ventPeriod: M14VentPeriode?= null,
    //-----------------Grossist-------------------------------------------------------------------------------------------------------------------------
    val image_Flotant: File? = null,
    //-----------------Fragmet.EditeBaseDonne.Fabs-------------------------------------------------------------------------------------------------------------------------
    val active_ModeEditesProduit: ModeEditesProduit? = null,
    //-----------------Fabs.Affichage-------------------------------------------------------------------------------------------------------------------------
    val affiche_Floating_Button_TogleFilterMarquers: Boolean = false,
    val affiche_Floating_Button_Cible_Client: Boolean = false,

    val affiche_Floating_Button_gps_follow_mode_active: Boolean = true,
    val affiche_Floating_Button_AddCLient: Boolean = false,

    val affiche_Floating_Button_SelecteCategorieEtAddNewProduit: Boolean = false,
    val affiche_Floating_Button_FABsModeEditesProduit: Boolean = false,
    //-----------------Fabs.Affichage-------------------------------------------------------------------------------------------------------------------------
    var pourcentage_AffichageDuCatalogue_Conficerie: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_Cosmitiques: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_tebnage: Double = 0.0,

) {
    companion object {
        fun get_Default(): ActiveCentralValues {
            return ActiveCentralValues()
        }
    }
    //-----------------Fragmet.EditeBaseDonne.Fabs-------------------------------------------------------------------------------------------------------------------------

    enum class ModeEditesProduit
        (
        val couleur: Color = Color(0xFF000000),
        )
    {
        Standart,
        PrixHanled(Color(0xFFFF5722),),
    }
    //-------------------------------------------------------------------------------------------------------------------------------------

    enum class Click_On_Marque
        (
        val couleur: Color = Color(0xFF000000),

        )
    {
        Standart,
        ADD_Au_Ciblage_Clients(Color(0xFFFF5722),),
        Affiche_OnCommand_VentPeriod_Transaction(Color(0xFF9C27B0),),;
    }

    sealed class RoleDefinieParSourceACetteFragment() {
        data object AfficheSearchAllProduits : RoleDefinieParSourceACetteFragment()
        data class SearchProduit(val produit: ArticlesBasesStatsTable) :
            RoleDefinieParSourceACetteFragment()
    }
}

@Stable
class FocusedValuesGetter(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1Produit,
    repo3CouleurProduitInfos: Repo03CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
    private val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
) {

    val currentActiveFocuced_M14VentPeriode by derivedStateOf {
        val periods = repo14VentPeriode.datasValue
        if (periods.isEmpty()) return@derivedStateOf null
        periods.find { it.keyID == currentActive_M9AppCompt?.current_OnVent_M14VentPeriode_KeyID } ?: periods.lastOrNull()
    }

    val filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod by derivedStateOf {
        val currentPeriod = currentActiveFocuced_M14VentPeriode ?: return@derivedStateOf emptyList<M8BonVent>()
        repo8BonVent.datasValue.filter { it.parent_M14VentPeriod_KeyId == currentPeriod.keyID }
    }

    val filtered_ListM10Vent_BY_Curr_M14VentPeriod by derivedStateOf {
        val currentPeriod = currentActiveFocuced_M14VentPeriode ?: return@derivedStateOf emptyList<M10OperationVentCouleur>()
        repo10OperationVentCouleur.datasValue.filter { it.parent_M14VentPeriod_KeyId == currentPeriod.keyID }
    }

    fun getDefaultM8BonVent() = M8BonVent(
        keyID = M8BonVent.generePushKey(),
        parent_M9AppCompt_KeyID = currentActive_M9AppCompt?.keyID ?: "",
        parent_M14VentPeriod_KeyId = currentActiveFocuced_M14VentPeriode?.keyID ?: "null",
    )

    private val _activeCentralValues = mutableStateOf(ActiveCentralValues(
        active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode()
    ))
    val active_Central_Values by derivedStateOf { _activeCentralValues.value }

    private fun getCurrentActiveVentPeriode(): M14VentPeriode? {
        return try {
            val currentAppCompt = repo9AppCompt.datasValue.firstOrNull {
                it.keyID == repo18CentralParametresOfAllApps.dataValue?.au_Lence_Set_Compt_Ac_KeyId
            }
            val periods = repo14VentPeriode.datasValue
            if (periods.isEmpty()) return null
            periods.find { it.keyID == currentAppCompt?.current_OnVent_M14VentPeriode_KeyID } ?: periods.lastOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun update_activeCentralValues(new: ActiveCentralValues) {
        _activeCentralValues.value = new
    }

    fun removePeriodFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode())
        update_activeCentralValues(updatedValues)
    }

    fun addGrossistFilter(grossist: M15Grossist) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = grossist)
        update_activeCentralValues(updatedValues)
    }

    fun removeGrossistFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M15Grossist_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun addClientFilter(client: M2Client) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = client)
        update_activeCentralValues(updatedValues)
    }

    fun removeClientFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M2Client_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun addProductFilter(product: ArticlesBasesStatsTable) {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = product)
        update_activeCentralValues(updatedValues)
    }

    fun removeProductFilter() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(active_M1Produit_AuFilterAchats = null)
        update_activeCentralValues(updatedValues)
    }

    fun clearAllFilters() {
        val currentValues = active_Central_Values
        val updatedValues = currentValues.copy(
            active_M14VentPeriode_AuFilterAchats = getCurrentActiveVentPeriode(),
            active_M15Grossist_AuFilterAchats = null,
            active_M2Client_AuFilterAchats = null,
            active_M1Produit_AuFilterAchats = null
        )
        update_activeCentralValues(updatedValues)
    }

    val currentActive_M9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull {
            it.keyID == repo18CentralParametresOfAllApps.dataValue?.au_Lence_Set_Compt_Ac_KeyId
        }
    }

    //-----------------------------M8BonVent-----------------------------------------------------------------------------------------------------------------------------------------------
    val activeOnVent_M8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find { it.keyID == currentActive_M9AppCompt?.onVentM8BonVentKey }
    }

    val filteredList_M2Client_Ou_Leur_Last_M8BonVent_Etate_IS_Cible by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .sortedBy { it.creationTimestamps }
                .lastOrNull { it.parent_M2Client_KeyID == client.keyID }
                ?.etateActuellementEst == M8BonVent.EtateActuellementEst.Cible
        }
    }

    val currentApp_Est_Admin by derivedStateOf {
        currentActive_M9AppCompt?.its_Admin == true
    }

    val filteredList_M2Client_LastM8BonVentEtate_IS_ON_MODE_COMMEND_ACTUELLEMENT by derivedStateOf {
        repo2Client.datasValue.filter { client ->
            val lastBonVent = filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
                .filter {
                    (it.parent_M2Client_KeyID == client.keyID && it.parent_M9AppCompt_KeyID == (currentActive_M9AppCompt?.keyID ?: ""))
                }
                .maxByOrNull { it.creationTimestamps }

            lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT ||
                    lastBonVent?.etateActuellementEst == M8BonVent.EtateActuellementEst.Rapport_Entre_On_Etate_De_Bloquage
        }
    }

    val activeOnVentM2ClientInfos by derivedStateOf {
        repo2Client.datasValue.find {
            it.keyID == (activeOnVent_M8BonVent?.parent_M2Client_KeyID ?: "")
        }
    }

    val activeOnVent_M2Client by derivedStateOf {
        val targetKey = activeOnVent_M8BonVent?.parent_M2Client_KeyID
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    fun getDefaultM10VentOperation(): M10OperationVentCouleur? {
        return activeOnVent_M8BonVent?.let {
            with(it) {
                M10OperationVentCouleur(
                    parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                    parent_M8BonVent_KeyId = keyID,
                    parent_M8BonVent_DebugInfos = get_DebugInfos(),
                )
            }
        }
    }

    val onVentM10VentOperation by derivedStateOf {
        repo10OperationVentCouleur.datasValue.find { it.keyID == currentActive_M9AppCompt?.onVentM3CouleurProduitInfosKeyID }
    }

    //-----------------------------M10OperationVentCouleur-----------------------------------------------------------------------------------------------------------------------------------------------
    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parent_M8BonVent_KeyId == (currentActive_M9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == currentActive_M9AppCompt?.activeFocuce_TariffPrixDifineur_M1ProduitKeyID }
    }

    fun get_ListFiltered_M10OperationVentCouleurs_By_M1Produit(produit: ArticlesBasesStatsTable) =
        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.filter { ventOperation ->
            ventOperation.parent_M1Produit_KeyId == produit.keyID
        }

    val focused_ListM10OpeVentCouleur_Par_PD_M1Produit by derivedStateOf {
        onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.filter {
            it.parent_M1Produit_KeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    val active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val focused_M13TarificationInfos_Pour_Produit by derivedStateOf {
        repo13TarificationInfos.datasValue.lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    val activeDialogSearchM1Produit by derivedStateOf {
        currentActive_M9AppCompt?.activeDialogSearchM1Produit ?: false
    }

    val its_Developing_Mode = M18CentralParametresOfAllApps.get_Default().itsDevMode

    val m18CentralParametresOfAllApps by derivedStateOf { M18CentralParametresOfAllApps() }

    companion object {
        @SuppressLint("ModifierFactoryUnreferencedReceiver")
        fun Modifier.getSemanticsTagFocucedVars(getter: FocusedValuesGetter): Modifier {
            val map = buildMap {
                with(getter) {
                    put("onVentM2ClientInfos", activeOnVent_M2Client?.let { it.nom } ?: "null")
                    put("onVentM8BonVent", activeOnVent_M8BonVent?.let { "${it.parent_M2Client_DebugInfos}/${it.etateActuellementEst}" } ?: "null")
                    put("focused_M1ProduitInfos_Pour_PrixDifineur", focused_M1ProduitInfos_Pour_PrixDifineur?.let { it.nom + it.keyID } ?: "null")
                    put("onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent", onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.map { "${it.parent_M1Produit_DebugInfos} / ${it.parent_M1Produit_KeyId}" })
                    put("focused_ListM10OpeVentCouleur_Par_PD_M1Produit", focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { it.getDebugInfos() })
                }
            }
            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag_By_datas_A_Affiche_Au_Nom(index + 6, key, value)
            }
        }
    }
}
