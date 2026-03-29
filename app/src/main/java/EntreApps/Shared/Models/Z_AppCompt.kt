package EntreApps.Shared.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
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
    DeleteInsertAll_Ref_All_Datas();   // deletes all local data then re-fetches ALL ref data without M3 active-key filtering
}

@Entity
data class Z_AppCompt(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var appDesignedPourWorkingGrossisst3Ali: Boolean = true,

    var its_mode_affiche_que_produits_au_depot: Boolean = true,

    var mode_edite_dispo: Boolean = false,
    var credit_fait: Double = 0.0,

    // Section InfosDeBase

    var nom: String = "",
    var autres_Noms_SepareParComma: String = "",

    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = Build.ID,

    var period_Qui_Doit_Etre_Au_Entre: String = "",

    var separeted_by_commas_keys_clients_a_cible_groupe_n1: String = ",",
    var keys_clients_a_cible_groupe_n2: String = ",",
    var keys_clients_a_cible_groupe_n3: String = ",",


    // Section Options Personnel
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


    // Section Centralization Valeurs Pour Injection add_New TOu modules

    // Section Paramaters App telephone


    var mainInitDataBaseProgressEtate: Float = 0f,
    //---------------------------------Centrale_Focuces_Values.----------------------------------------------------------------------------------------------------------------------------------
    val filter_marqueClient_Name: String = "no Filter",

    val next_start: Do = Do.StandartInit_Sans_RienFair,

    val activeDialogSearchM1Produit: Boolean = false,
    val active_ProduitKeyID_Au_DroopDown_PresenterEcran: String = "",
    val active_CouleurKeyID_Extended_Image: String = "",

    val affiche_Dialog_Fast_Affiche_Panie_App4: Boolean = false,

    val affiche_ProduitDataBaseEdites_ComposableViews: Boolean = true,
    //------------------------------------------------------------------------------------------------------------------------------------------------------------

    var couleurAchateOperationIdOuvertPourCeCompt: String = "",
    var couleurAchateOperationKeyOuvertPourCeCompt: String = "",
    var ouvertProduitOnVentNom: String = "",

    //---------------------------------Parent.M14VentPeriode----------------------------------------------------------------------------------------------------------------------------------
    var current_OnVent_M14VentPeriode_KeyID: String = "",
    var current_OnVent_M14VentPeriode_DebugInfos: String = "",
    //---------------------------------------------------Vent Createur--------------------------------------------------------------
    var onVentM8BonVentKey: String = "",
    var onVentM8BonVentDebugInfos: String = "",
    //---------------------------------Parent.M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var onVentM1ProduitInfosKeyID: String = "",
    var onVentM1ProduitInfosDebugName: String = "",
    //---------------------------------Parent.M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var onVentM3CouleurProduitInfosKeyID: String = "null",
    val onVentM3CouleurProduitDebugInfos: String = "null",
    //------------------------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------DialogOpner.DialogAboveAll.OutlinedSearchListProduits----------------------------------------------------------------------------------------------------------------------------------
    var dialogAboveAll_OutlinedSearchListProduits: Boolean = false,

    //---------------------------------dialogChoisireQuantityM1Produit----------------------------------------------------------------------------------------------------------------------------------
    var dialogChoisireQuantityM1ProduitInfosKeyID: String = "null",
    var dialogChoisireQuantityM1ProduitInfosDebugName: String = "null",

    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var activeFocuce_TariffPrixDifineur_M1ProduitKeyID: String = "null",
    var activeFocuceTariffPrixDifineurM1ProduitDebugInfos: String = "null",
    //---------------------------------M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
    var startTextSearchM1Produit: String = "",
    //------------------------------------------------------------------------------------------------------------------------------------------------

    //------------------------------------A SUPP ------------------------------------------------------------------------------------------------------------
    var KeyByParent: String = "",
    var vid: Long = 1,
) {

    fun get_DebugInfos(): String {
        return buildString {
            append("(M9=")
            append(nom)
            append("[")
            append(keyID.takeLast(3).uppercase())
            append("])")
        }
    }

    fun Z_AppCompt.addStringAuNomsMutableTags(str: String): List<String> {
        val currentTags = if (autres_Noms_SepareParComma.isNotEmpty()) {
            autres_Noms_SepareParComma.split(",").map { it.trim() }
        } else {
            emptyList()
        }

        return if (currentTags.contains(str)) {
            currentTags
        } else {
            currentTags + str
        }
    }

    fun getList_autres_Noms_SepareParComma(): List<String> {
        return if (autres_Noms_SepareParComma.isNotEmpty()) {
            autres_Noms_SepareParComma.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            emptyList()
        }
    }

    companion object {
        const val keyModel = "ID9"
        const val keyModelValID7VentParent = "ID7"

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        fun get_Default() = Z_AppCompt()


        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )

        fun generePushKey() = RepositorysMainSetter.Companion.genereUnPushKeyFireBase(ref)
    }
}
