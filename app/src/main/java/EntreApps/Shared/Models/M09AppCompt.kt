package EntreApps.Shared.Models

import EntreApps.Shared.Models.Home.ActiveCentralValues
import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

enum class Do() {
    StandartInit_Sans_RienFair,
    DeleteInsertAll_Active_Key(),
    DeleteAll_To_Let_Ancien_Repositorys_GetAll(),
    DeleteInsertAll_Ref_All_Datas();
}

@Entity
data class M09AppCompt(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var appDesignedPourWorkingGrossisst3Ali: Boolean = true,

    var its_mode_affiche_que_produits_au_depot: Boolean = true,

    var mode_edite_dispo: Boolean = false,

    var credit_fait: Double = 0.0,

    var nom: String = "",
    var autres_Noms_SepareParComma: String = "",

    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,

    var period_Qui_Doit_Etre_Au_Entre: String = "",

    var separeted_by_commas_keys_clients_a_cible_groupe_n1: String = ",",
    var keys_clients_a_cible_groupe_n2: String = ",",
    var keys_clients_a_cible_groupe_n3: String = ",",

    var image_detail_produit_s_affiche: Boolean = true,

    var presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId: String = "",
    var hideAppScreen: Boolean = false,

    val travailleChezGrossisst3Ali: Boolean = false,

    val affiche_toujoure_tariffs_tournet: Boolean = false,

    val its_Admin: Boolean = false,
    var c_Ouvert_Pour_Au_Command_Add_Period: Boolean = true,

    val text_Message_Warning: String = "",

    var ne_affiche_que_fragment: String = "",

    var itsProductionModePourCeCompt: Boolean = false,
    var ceComptVendeurInsertBonsAchatAuPeriodID: Long = 0L,
    var ceComptVendeurStartAffichePeriod: Long = 0L,

    var migreSonDataBaseAuStart: Boolean = false,
    var cConnectAuDevelopingDataBaseAuRelodApp: Boolean = false,

    var mainInitDataBaseProgressEtate: Float = 0f,


    val filter_marqueClient_Name: String = "no Filter",

    val next_start: Do = Do.StandartInit_Sans_RienFair,
    val force_next_start_: Do = Do.DeleteInsertAll_Active_Key,

    val activeDialogSearchM1Produit: Boolean = false,
    val active_ProduitKeyID_Au_DroopDown_PresenterEcran: String = "",
    val active_CouleurKeyID_Extended_Image: String = "",

    val affiche_Dialog_Fast_Affiche_Panie_App4: Boolean = false,

    val active_DropDown_FabButton: Boolean = false,

    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",
    var ouvertProduitOnVentNom: String = "",

    var current_OnVent_M14VentPeriode_KeyID: String = "",
    var current_OnVent_M14VentPeriode_DebugInfos: String = "",

    var onVentM8BonVentKey: String = "",
    var onVentM8BonVentDebugInfos: String = "",

    var onVentM1ProduitInfosKeyID: String = "",
    var onVentM1ProduitInfosDebugName: String = "",

    var onVentM3CouleurProduitInfosKeyID: String = "null",
    val onVentM3CouleurProduitDebugInfos: String = "null",

    var dialogAboveAll_OutlinedSearchListProduits: Boolean = false,

    var dialogChoisireQuantityM1ProduitInfosKeyID: String = "null",
    var dialogChoisireQuantityM1ProduitInfosDebugName: String = "null",

    var activeFocuce_TariffPrixDifineur_M1ProduitKeyID: String = "null",
    var activeFocuceTariffPrixDifineurM1ProduitDebugInfos: String = "null",

    var startTextSearchM1Produit: String = "",

    // ---- mode de clic sur un marqueur carte ----
    var click_On_Marque: ActiveCentralValues.Click_On_Marque = ActiveCentralValues.Click_On_Marque.Standart,

    var KeyByParent: String = "",
    var vid: Long = 1,

    var limite_couleurs_ou_leur_last_achate_est_moin_que_jour: Int = 1000,
) {
    fun get_DebugInfos(): String = buildString {
        append("(M9=")
        append(nom)
        append("[")
        append(keyID.takeLast(3).uppercase())
        append("])")
    }

    fun M09AppCompt.addStringAuNomsMutableTags(str: String): List<String> {
        val currentTags = if (autres_Noms_SepareParComma.isNotEmpty())
            autres_Noms_SepareParComma.split(",").map { it.trim() }
        else emptyList()
        return if (currentTags.contains(str)) currentTags else currentTags + str
    }

    fun getList_autres_Noms_SepareParComma(): List<String> =
        if (autres_Noms_SepareParComma.isNotEmpty())
            autres_Noms_SepareParComma.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        else emptyList()

    fun to_Map(): Map<String, Any?> = mapOf(
        "keyID" to keyID,
        "creationTimestamp" to creationTimestamp,
        "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
        "appDesignedPourWorkingGrossisst3Ali" to appDesignedPourWorkingGrossisst3Ali,
        "its_mode_affiche_que_produits_au_depot" to its_mode_affiche_que_produits_au_depot,
        "mode_edite_dispo" to mode_edite_dispo,
        "credit_fait" to credit_fait,
        "nom" to nom,
        "autres_Noms_SepareParComma" to autres_Noms_SepareParComma,
        "deviceModelNom" to deviceModelNom,
        "deviceModelId" to deviceModelId,
        "period_Qui_Doit_Etre_Au_Entre" to period_Qui_Doit_Etre_Au_Entre,
        "separeted_by_commas_keys_clients_a_cible_groupe_n1" to separeted_by_commas_keys_clients_a_cible_groupe_n1,
        "keys_clients_a_cible_groupe_n2" to keys_clients_a_cible_groupe_n2,
        "keys_clients_a_cible_groupe_n3" to keys_clients_a_cible_groupe_n3,
        "image_detail_produit_s_affiche" to image_detail_produit_s_affiche,
        "presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId" to presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId,
        "hideAppScreen" to hideAppScreen,
        "travailleChezGrossisst3Ali" to travailleChezGrossisst3Ali,
        "affiche_toujoure_tariffs_tournet" to affiche_toujoure_tariffs_tournet,
        "its_Admin" to its_Admin,
        "c_Ouvert_Pour_Au_Command_Add_Period" to c_Ouvert_Pour_Au_Command_Add_Period,
        "text_Message_Warning" to text_Message_Warning,
        "ne_affiche_que_fragment" to ne_affiche_que_fragment,
        "itsProductionModePourCeCompt" to itsProductionModePourCeCompt,
        "ceComptVendeurInsertBonsAchatAuPeriodID" to ceComptVendeurInsertBonsAchatAuPeriodID,
        "ceComptVendeurStartAffichePeriod" to ceComptVendeurStartAffichePeriod,
        "migreSonDataBaseAuStart" to migreSonDataBaseAuStart,
        "cConnectAuDevelopingDataBaseAuRelodApp" to cConnectAuDevelopingDataBaseAuRelodApp,
        "mainInitDataBaseProgressEtate" to mainInitDataBaseProgressEtate,
        "filter_marqueClient_Name" to filter_marqueClient_Name,
        "next_start" to next_start.name,
        "force_next_start_" to force_next_start_.name,
        "activeDialogSearchM1Produit" to activeDialogSearchM1Produit,
        "active_ProduitKeyID_Au_DroopDown_PresenterEcran" to active_ProduitKeyID_Au_DroopDown_PresenterEcran,
        "active_CouleurKeyID_Extended_Image" to active_CouleurKeyID_Extended_Image,
        "affiche_Dialog_Fast_Affiche_Panie_App4" to affiche_Dialog_Fast_Affiche_Panie_App4,
        "active_DropDown_FabButton" to active_DropDown_FabButton,
        "couleurAchateOperationIdOuvertPourCeCompt" to couleurAchateOperationIdOuvertPourCeCompt,
        "couleurAchateOperationKeyOuvertPourCeCompt" to couleurAchateOperationKeyOuvertPourCeCompt,
        "ouvertProduitOnVentNom" to ouvertProduitOnVentNom,
        "current_OnVent_M14VentPeriode_KeyID" to current_OnVent_M14VentPeriode_KeyID,
        "current_OnVent_M14VentPeriode_DebugInfos" to current_OnVent_M14VentPeriode_DebugInfos,
        "onVentM8BonVentKey" to onVentM8BonVentKey,
        "onVentM8BonVentDebugInfos" to onVentM8BonVentDebugInfos,
        "onVentM1ProduitInfosKeyID" to onVentM1ProduitInfosKeyID,
        "onVentM1ProduitInfosDebugName" to onVentM1ProduitInfosDebugName,
        "onVentM3CouleurProduitInfosKeyID" to onVentM3CouleurProduitInfosKeyID,
        "onVentM3CouleurProduitDebugInfos" to onVentM3CouleurProduitDebugInfos,
        "dialogAboveAll_OutlinedSearchListProduits" to dialogAboveAll_OutlinedSearchListProduits,
        "dialogChoisireQuantityM1ProduitInfosKeyID" to dialogChoisireQuantityM1ProduitInfosKeyID,
        "dialogChoisireQuantityM1ProduitInfosDebugName" to dialogChoisireQuantityM1ProduitInfosDebugName,
        "activeFocuce_TariffPrixDifineur_M1ProduitKeyID" to activeFocuce_TariffPrixDifineur_M1ProduitKeyID,
        "activeFocuceTariffPrixDifineurM1ProduitDebugInfos" to activeFocuceTariffPrixDifineurM1ProduitDebugInfos,
        "startTextSearchM1Produit" to startTextSearchM1Produit,
        "click_On_Marque" to click_On_Marque.name,
        "KeyByParent" to KeyByParent,
        "vid" to vid,
        "limite_couleurs_ou_leur_last_achate_est_moin_que_jour" to limite_couleurs_ou_leur_last_achate_est_moin_que_jour,
    )

    companion object {
        const val keyModel = "ID9"
        const val keyModelValID7VentParent = "ID7"

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        fun get_Default() = M09AppCompt()

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )

        fun generePushKey() = M00CentralParametresOfAllApps.genereUnPushKeyFireBase(ref)
    }
}
