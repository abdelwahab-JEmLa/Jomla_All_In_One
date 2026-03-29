package EntreApps.Shared.Models

import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.Components.Utilisateur
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

enum class Compts(val keyId: String){
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
    val au_Lence_Set_Compt_Ac_KeyId: String = Compts.AbdelwahabTravailleChezGros_KeyId.keyId,

//──────────────────────────────Dimine Rapid────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val au_Lence_Diminue_DatasFB: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!
    val au_Lence_Dimininue_Datas_OperationVents: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!
    val au_Lence_Dimininue_Datas_M8BonVents: Boolean = false,     //Dimine Delete Fait Gaffe!!!!!!!!!!
//───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val time_tamp_all_tariffs: Boolean = false,     //Fait Gaffe updateTariffsWithZeroTimestamps!!!!!!!!!!
//───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
    val itsDevMode: Boolean = false,
    val force_next_start_DeleteInsertAll: Boolean = false,

    val its_AppType: AppType = if (au_Lence_Set_Compt_Ac_KeyId == Compts.Telephone_de_presentation.keyId) {
        AppType.JomLaElectroLivreurGrossist_PresenterScreen
    } else {
        if (itsDevMode) {
            AppType.AllInOne
        } else {
            AppType.AllInOne
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
        )
         val central_Local_storageLink = buildString {
            append("/storage/emulated/0/Abdelwahab_jeMla.com")
        }

         val images_central_Local_storageLink = buildString {
            append(central_Local_storageLink)
            append("/IMGs/BaseDonne")
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

        /**
         * Check if edit/modification features should be visible
         * Only Admin can edit, regular users cannot
         */
        fun canEdit(utilisateur: Utilisateur): Boolean {
            return utilisateur == Utilisateur.Admin
        }
    }
}
