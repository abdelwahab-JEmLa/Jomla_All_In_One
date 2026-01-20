package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Repo18CentralParametresOfAllApps(appDataBase: AppDatabase) {
    val repoTAG = "Repo18CentralParametresOfAllApps"

    private val repoScope = CoroutineScope(Dispatchers.IO)
    private val refRepo = M18CentralParametresOfAllApps.ref
    private val dao = appDataBase.M18CentralParametresOfAllAppsDao()

    private val _data = mutableStateOf<M18CentralParametresOfAllApps?>(null)
    val dataValue by derivedStateOf { _data.value }

    init {
        extracted()
    }

    private fun extracted() {
        repoScope.launch {
            val localData = dao.getAll()
            _data.value = localData
            setupFirebaseGet()
        }
    }

    private fun setupFirebaseGet() {
        refRepo.get().addOnSuccessListener { snapshot ->
            val data = snapshot.getValue(M18CentralParametresOfAllApps::class.java)

            repoScope.launch {
                if (data != null) {
                    dao.upsert(data)
                    _data.value = data
                    Log.d(repoTAG, "Successfully retrieved and saved data: $data")
                } else {
                    Log.w(repoTAG, "Received null data from Firebase")
                    val defaultData = M18CentralParametresOfAllApps.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Using default data: $defaultData")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(repoTAG, "Failed to retrieve data from Firebase", exception)

            repoScope.launch {
                val localData = dao.getAll()
                if (localData == null) {
                    val defaultData = M18CentralParametresOfAllApps.get_Default()
                    dao.upsert(defaultData)
                    _data.value = defaultData
                    Log.d(repoTAG, "Firebase failed, using default data: $defaultData")
                }
            }
        }
    }

    fun getNotificationSettings(): Boolean {
        return _data.value?.enableNotifications ?: true
    }
}

@Entity
data class M18CentralParametresOfAllApps(
    @PrimaryKey
    val keyId: String = "M18CentralParametresOfAllApps",

    //---------------------------------Developing.Tools---------------------------------------------------------------------------------------------------------------------------------
    val itsDevMode: Boolean = false,
    val devStartUpScree: String = Screen.Compact_Presentoire_App_Produits_FragID4.route,

    val desactive_Animation_Pour_LayoutInspector: Boolean = false,

    val listens_on_data_change_resources_consolation: Boolean = false,
    //---------------------------------Compts----------------------------------------------------------------------------------------------------------------------------------
    val abdelwahabTravailleChezGros_KeyId: String = "-OV9dYujH9cA3yEx8AY2",

    val abdelwahabCompt_KeyId: String = "-OV9dYujH9cA3yEx8AYT",
    val abdelwahabCompt_KeyId_DPL: String = "-OV9edQZecDczbx-ndPl",

    val younes_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s5",
    val jamale_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s6",
    val walid_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s7",
    val abdelmomen_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s4",
    val amine_madrasa_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s8",
    val kissm_intikali_madrasa_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s9",

    val au_Lence_Set_Compt_Ac_KeyId: String =  abdelwahabTravailleChezGros_KeyId,
    //---------------------------------Lence Rapid----------------------------------------------------------------------------------------------------------------------------------

    val au_Lence_Diminue_DatasFB: Boolean =  true,     //Dimine Delete Fait Gaffe!!!!!!!!!!
    val au_Lence_Dimininue_Datas_M8BonVents: Boolean =  false,     //Dimine Delete Fait Gaffe!!!!!!!!!!
    val time_tamp_all_tariffs: Boolean =  false,     //Fait Gaffe updateTariffsWithZeroTimestamps!!!!!!!!!!
    //----------------------------------------------------------------------------------------------------------------------------------------------------

    /*     if (itsDevMode) abdelmomen_Compt_KeyId else {
            if (Build.MODEL == "Redmi Note 8")
                abdelwahabCompt_KeyId
            else
                abdelmomen_Compt_KeyId
        },            */

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
        val ref = RepositorysMainGetter.centralRef
            .child("Datas18CentralParametresOfAllApps")

        fun get_Default(): M18CentralParametresOfAllApps {
            return M18CentralParametresOfAllApps()
        }

        fun get_utilisateur(currentComptKeyId: String): Utilisateur {
            val params = M18CentralParametresOfAllApps()
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

@Dao
interface M18CentralParametresOfAllAppsDao {
    @Query("SELECT COUNT(*) FROM M18CentralParametresOfAllApps")
    suspend fun getCount(): Int

    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M18CentralParametresOfAllApps WHERE keyId = 'M18CentralParametresOfAllApps'")
    suspend fun getAll(): M18CentralParametresOfAllApps?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M18CentralParametresOfAllApps)

    @Delete
    suspend fun delete(data: M18CentralParametresOfAllApps)

    @Query("DELETE FROM M18CentralParametresOfAllApps")
    suspend fun deleteAll()
}
