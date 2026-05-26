package EntreApps.Shared.Models

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.io.File

enum class Compts(val keyId: String) {
    AbdelwahabTravailleChezGros_KeyId("-OV9dYujH9cA3yEx8AY2"),
    Telephone_de_presentation("-OTmoNn0cljrRuhVR2sp"),
}

@Entity
data class M00CentralParametresOfAllApps(
    @PrimaryKey
    val keyId: String = "M18CentralParametresOfAllApps",
    //---------------------------------Developing.Tools---------------------------------------------------------------------------------------------------------------------------------
    val devStartUpScree: String = Screen.EditDatabaseWithCreateNewArticles.route,

    val desactive_Animation_Pour_LayoutInspector: Boolean = false,

    val listens_on_data_change_resources_consolation: Boolean = false,
    val no_loadKoin_CrachComposReglement: Boolean = false,
    val load_All_modules: Boolean = false,
//────────────────────────────Compts──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val abdelwahabCompt_KeyId: String = "-OV9dYujH9cA3yEx8AYT",
    val abdelwahabCompt_KeyId_DPL: String = "-OV9edQZecDczbx-ndPl",

    val younes_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s5",
    val jamale_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s6",
    val walid_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s7",
    val abdelmomen_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s4",
    val amine_madrasa_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s8",
    val kissm_intikali_madrasa_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s9",

//────────────────────────────au_Lence_Set_Compt_Ac_KeyId──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val au_Lence_Set_Compt_Ac_KeyId: String = Compts.Telephone_de_presentation.keyId,
    //  AbdelwahabTravailleChezGros_KeyId
    //  Telephone_de_presentation

//──────────────────────────────Dimine Rapid────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val au_Lence_Diminue_DatasFB: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!    //Ca M11AchatOperation.remove_ref() et  cleanupp Vents Operation et cleanupp Bon Vents
    val au_Lence_remove_Datas_OperationVents: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!
    val au_Lence_Dimininue_Datas_M8BonVents: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!

//───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val time_tamp_all_tariffs: Boolean = false,     //Fait Gaffe updateTariffsWithZeroTimestamps!!!!!!!!!!

//───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val itsDevMode: Boolean = false,

    val chose_ref_test_For_Datas_Car_C_DevMode: Boolean = true,

    val force_next_start_DeleteInsertAll: Boolean = false,
    val its_AppType: AppType = if (au_Lence_Set_Compt_Ac_KeyId == Compts.Telephone_de_presentation.keyId) {
        AppType.JomLaElectroLivreurGrossist_PresenterScreen
    } else {
        if (itsDevMode) {
            AppType.JomLaElectroLivreurGrossist_VendeurHost
        } else {
            AppType.JomLaElectroLivreurGrossist_VendeurHost

           // AppType.AllInOne
           // AppType.JomLaElectroLivreurGrossist_VendeurHost
        }
    },

    //---------------------------------App Settings----------------------------------------------------------------------------------------------------------------------------------
    val activeWindowsSearchProduit: Boolean = false,
    var enablePerformAutoClickImageDisplayer: Boolean = false,
    val isControleFabVisible: Boolean = false,
    //---------------------------------Notification Settings----------------------------------------------------------------------------------------------------------------------------------
    val enableNotifications: Boolean = true,
    val enableSoundNotifications: Boolean = true,
    val enableVibrationNotifications: Boolean = true,
    val notificationVolume: Float = 0.8f, // 0.0 to 1.0
) {
    companion object {

        val centralRef = Firebase.database.getReference(
            "00_DataPrototype-04-02" + "/_1_developingRef" + "/C_InfosSqlDataBases"
        )              //anccien Proto

        val central_All_References_Production = Firebase.database
            .getReference("00_DataBase_06_06")

        val central_MainDataBases_RefProduction = central_All_References_Production
            .child("A_Main_DataBases")

        val central_Developing_Test = central_All_References_Production
            .child("Developing_Test")

        val centralRef_Non_Active_Datas_PourLightApp = central_All_References_Production
            .child("Z_NonActiveDatas")


        val central_Local_storageLink = buildString {
            append("/storage/emulated/0/Abdelwahab_jeMla.com")
        }

        val central_Local_Csv= File(central_Local_storageLink, "CSV_Export")

//─────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────

        fun genereUnPushKeyFireBase(ref: DatabaseReference): String {
            return ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")
        }


        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        inline fun Long?.ifNotNullOrZero(block: () -> Unit) {
            if (this != null && this != 0L) block()
        }

        inline fun String?.ifNotNullOrEmpty(block: () -> Unit) {
            if (!this.isNullOrEmpty()) block()
        }


        inline fun Boolean.ifTrue(block: () -> Unit) {
            if (this) block()
        }

        inline fun Boolean.ifFalse(block: () -> Unit) {
            if (!this) block()
        }

        fun String?.empty_If_Null(value: String = ""): String {
            return this ?: value
        }

        val ref = RepositorysMainGetter.Companion.centralRef
            .child("Datas18CentralParametresOfAllApps")

        fun get_Default(): M00CentralParametresOfAllApps {
            return M00CentralParametresOfAllApps()
        }

        fun get_utilisateur(currentComptKeyId: String): Utilisateur {
            val params = M00CentralParametresOfAllApps()
            return when (currentComptKeyId) {
                params.amine_madrasa_Compt_KeyId -> Utilisateur.Amine_Madrassa
                params.abdelmomen_Compt_KeyId -> Utilisateur.Abdelmoumen
                params.walid_Compt_KeyId -> Utilisateur.Walid
                else -> Utilisateur.Admin
            }
        }

        val images_central_Local_storageLink =
            M3CouleurProduitInfos.images_central_Local_storageLink

        /**
         * Check if edit/modification features should be visible
         * Only Admin can edit, regular users cannot
         */
        fun canEdit(utilisateur: Utilisateur): Boolean {
            return utilisateur == Utilisateur.Admin
        }
    }
}

enum class Utilisateur(
    val comp: String,
    val ayam_tadriss: String = "dimanch/jeudi",
    val nom_arab: String = "",
    val num_telephone: String = "",
    val hour_earn: Double = 1200.00,
) {
    Admin("", "", "المسؤول"),
    Abdelwahab_Osstad(
        Compts.AbdelwahabTravailleChezGros_KeyId.keyId,
        "dimanch/jeudi",
        "عبدالوهاب حمنيش",
        "+213 553 88 50 37",
        1530.00
    ),
    kissm_intikali_madrasa_Compt_Osstad(
        M00CentralParametresOfAllApps().kissm_intikali_madrasa_Compt_KeyId,
        "dimanch/jeudi",
        "قسم انتقالي"
    ),
    Abdelmoumen(
        M00CentralParametresOfAllApps().abdelmomen_Compt_KeyId,
        "dimanch/jeudi",
        "عبدالمؤمن"
    ),
    Walid(
        M00CentralParametresOfAllApps().walid_Compt_KeyId,
        "dimanch/jeudi",
        "وليد"
    ),
    Amine_Madrassa(
        M00CentralParametresOfAllApps().amine_madrasa_Compt_KeyId,
        "dimanch/jeudi",
        "أمين"
    );

    override fun toString(): String {
        return name
    }

    /**
     * Toggle to next utilisateur in cycle
     */
    fun toggle(): Utilisateur {
        return when (this) {
            Admin -> Abdelwahab_Osstad
            Abdelwahab_Osstad -> Abdelmoumen
            Abdelmoumen -> Walid
            Walid -> Amine_Madrassa
            Amine_Madrassa -> Utilisateur.kissm_intikali_madrasa_Compt_Osstad
            kissm_intikali_madrasa_Compt_Osstad -> Admin
        }
    }

    /**
     * Get display name for UI
     */
    fun getDisplayName(): String {
        return when (this) {
            Admin -> "Admin (Tous)"
            Abdelwahab_Osstad -> "Abdelwahab Oustade"
            Abdelmoumen -> "Abdelmoumen"
            Amine_Madrassa -> "Amine Madrassa"
            Walid -> "Walid"
            kissm_intikali_madrasa_Compt_Osstad -> "kissm_intikali_madrasa_Compt_Osstad"
        }
    }


    companion object {
        /**
         * Get next utilisateur from current, or return Admin if null
         */
        fun toggleFrom(current: Utilisateur?): Utilisateur {
            return current?.toggle() ?: Abdelmoumen
        }

        /**
         * Find utilisateur by comp ID
         */
        fun fromCompId(compId: String): Utilisateur? {
            return values().find { it.comp == compId }
        }

    }
}

enum class AppType {
    AllInOne,
    GrossistRealSeller,
    JomLaElectroLivreurGrossist_PresenterScreen,
    JomLaElectroLivreurGrossist_VendeurHost,
}
