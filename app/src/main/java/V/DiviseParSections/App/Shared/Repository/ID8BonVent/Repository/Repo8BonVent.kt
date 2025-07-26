package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clientjetpack.R
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@Stable
class Repo8BonVent(
    private val context: Context,
    val dataBaseCreationFactory: DataBaseInitFactory_8BonVent,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M8BonVent>>(emptyList())
    val datasValue by derivedStateOf { _datas.value.sortedBy { it.creationTimestamps } }

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun upsert(data: M8BonVent) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyID == dataUpdate.keyID }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    if (existingIndex >= 0) {
                        this[existingIndex] = dataUpdate
                    } else {
                        add(dataUpdate)
                    }
                }
            }
        }
        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    fun add(data: M8BonVent) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: M8BonVent) {
        dataBaseCreationFactory.set(dataUpdate)
    }

    fun delete(data: M8BonVent) {
        composScope.launch {
            try {
                _datas.value = datasValue.filter { it.keyID != data.keyID }
                dataBaseCreationFactory.delete(data)
            } catch (e: Exception) {
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------
    fun addNew(data: M8BonVent) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = _datas.value.toMutableList().apply {
                    add(dataUpdate)
                }
            }
        }

        dataBaseCreationFactory.set(dataUpdate)
    }

    fun updateIfExist(data: M8BonVent) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ancien.keyID == data.keyID
        }

        if (existingIndex < 0) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Item not found, cannot update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return
        }

        val updatedItem = data.copy(
            keyID = datasValue[existingIndex].keyID,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = datasValue.toMutableList().apply {
                    this[existingIndex] = updatedItem
                }
            }
        }

        dataBaseCreationFactory.set(updatedItem)
    }
}

@Entity
data class M8BonVent(
    @PrimaryKey
    var keyID: String = generePushKey(),

    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    //---------------------------------Parent.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    var parent_M9AppCompt_KeyID: String = "null",
    var parent_M9AppCompt_DebugInfos: String = "null",
    //---------------------------------Parent.M14VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    //---------------------------------Parent.M2Client----------------------------------------------------------------------------------------------------------------------------------
    var parent_M2Client_KeyID: String = "null",
    var parent_M2Client_DebugInfos: String = "null",
    var parent_M2Client_OldLongID: Long = 0L,
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------Parent.M17----------------------------------------------------------------------------------------------------------------------------------
    var parent_M17Message_KeyID: String = "null",
    var parent_M17Message_DebugInfos: String = "null",
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // Section InfosDeBase
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,

    //Mutable
    var position_Don_Lis_Cible_Clients_au_VentPeriod: Int = 0,

    // Section Centralization Valeurs Pour Injection add_New TOu modules
    var cLeDataOuvertDuParentList: Boolean? = null,
    var cActive: Boolean = false,

    //A Supp
    val parentID8C2TypeTransactionKeyByParent: String = "",
    var vid: Long = 0L,

    // Section keyFireBase et Update Version Id
) {

    fun get_DebugInfos(): String {
        return buildString {
            append("Bon")
            append("[")
            append("p.cli->")
            append(parent_M2Client_DebugInfos)
            append(") ")
            append("[")
            append(keyID.takeLast(4))
            append("])")
        }
    }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        CreeMaisNonDefinie(android.R.color.white, "غير محدد"),

        ON_MODE_COMMEND_ACTUELLEMENT(
            android.R.color.holo_green_light,
            " تنفيذ المطلوب في تحسين الوضع معه"
        ),

        Rapport_Entre_On_Etate_De_Bloquage(android.R.color.holo_red_light, ":تقرير الدخول معه في حالة انسداد في التجارة بسبب"),
        Bloque_Probleme(R.color.c3, "حدث مشكل معه"),
        Ordre_Gerant(R.color.c4, "توجيه المسير"),

        A_COMMANDE_CONFIRME(
            android.R.color.holo_purple, "تم تاكيد الطلبية"
        ),
        COMMANDE_LIVRAI(android.R.color.holo_blue_dark, "تم أيصال منتجاته"),


        ACHETEUR_NON_DISPO(R.color.c2, "الشاري غائب"),
        AVEC_MARCHANDISE(R.color.c5, "عندو سلعة"),
        FERME(android.R.color.darker_gray, "مغلق"),


        Cible(android.R.color.holo_orange_dark, "معين من المسير"),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(android.R.color.holo_green_light, "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),

        PourVoirPanie(
            android.R.color.holo_red_light, "للنظر"
        ),
        RAPPORT_AU_ENREGESTREMENT_VOCALE(android.R.color.black, "التقرير قي التسجيل الصوتي "),
        ON_MODE_VOIRE_PANIE_ARTICLES(android.R.color.holo_blue_dark, "في معاينة السلة"),
        A_EVITE(android.R.color.holo_green_light, "اقترح ان يتجنب لمدة اسبوعين"),


        ;

        companion object {
            const val keyModel = "ID8C2"
        }
    }

    fun isSameEntity(other: M8BonVent) =
        keyID == other.keyID && parent_M9AppCompt_KeyID == other.parent_M9AppCompt_KeyID && parent_M14VentPeriod_KeyId == other.parent_M14VentPeriod_KeyId

    override fun equals(other: Any?) =
        this === other || (other is M8BonVent && isSameEntity(other))

    override fun hashCode() = Objects.hash(
        keyID, parent_M9AppCompt_DebugInfos, parent_M14VentPeriod_KeyId
    )

    companion object {
        const val keyModel = "ID8"

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("Datas08BonVent")

        fun generePushKey() = genereUnPushKeyFireBase(ref)

        fun get_default(
            parent_M9AppCompt_KeyID: String,
            parent_M9AppCompt_DebugInfos: String,
            parent_M14VentPeriod_DebugInfos: String,
            parent_M14VentPeriod_KeyId: String,
            parent_M2Client_KeyID: String,
            parent_M2Client_DebugInfos: String,
            etateActuellementEst: EtateActuellementEst? = null,
        ): M8BonVent {
            return M8BonVent(
                parent_M9AppCompt_DebugInfos = parent_M9AppCompt_DebugInfos,
                parent_M9AppCompt_KeyID = parent_M9AppCompt_KeyID,
                parent_M14VentPeriod_DebugInfos = parent_M14VentPeriod_DebugInfos,
                parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                parent_M2Client_KeyID = parent_M2Client_KeyID,
                parent_M2Client_DebugInfos = parent_M2Client_DebugInfos,
                etateActuellementEst = etateActuellementEst
                    ?: EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        }

        fun get_default_Par_VentPeriod_Et_Client(
            m14VentPeriode: M14VentPeriode,
            m2Client: M2Client,
            etateActuellementEst: EtateActuellementEst? = null,
        ): M8BonVent {
            return M8BonVent(
                parent_M9AppCompt_DebugInfos = m14VentPeriode.parent_M9AppCompt_KeyID,
                parent_M14VentPeriod_DebugInfos = m14VentPeriode.keyID,
                parent_M2Client_KeyID = m2Client.keyID,
                etateActuellementEst = etateActuellementEst
                    ?: EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        }

        fun find_By_MainValuesKeys_Depuit_List(
            data_List: List<M8BonVent>,
            parent_M14VentPeriod_DebugInfos: String,
            parent_M2Client_KeyID: String,
            relative_Etate: EtateActuellementEst? = null,
        ) = data_List
            .find { data ->
                val match_MainValuesKeys =
                    data.parent_M14VentPeriod_DebugInfos == parent_M14VentPeriod_DebugInfos
                            && data.parent_M2Client_KeyID == parent_M2Client_KeyID
                            && data.etateActuellementEst == relative_Etate

                match_MainValuesKeys
            }
    }
}
