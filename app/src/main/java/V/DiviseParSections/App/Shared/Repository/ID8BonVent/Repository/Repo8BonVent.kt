package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.MainRepositorysSetterFacade.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.DataBaseCreationFactoryGBonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
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
    val dataBaseCreationFactory: DataBaseCreationFactoryGBonVent,
    val zAppComptRepositoryComposable: Repo9AppCompt,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<M8BonVent>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            dataBaseCreationFactory.dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun upsert(data: M8BonVent) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.keyByParent == dataUpdate.keyByParent }

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
        val dataUpdate = data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())

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
}

@Entity
data class M8BonVent(
    @PrimaryKey var keyByParent: String = "null",

    var keyID: String = generePushKey(),

    var fireBasePushKey: String = generePushKey(),

    var creationTimestamps: Long = 0,
    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),
    var debugInfos: String = "",

    //---------------------------------Forging Keys----------------------------------------------------------------------------------------------------------------------------------

    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parentM7VentPeriodKeyId: String = "",
    var parentM7VentPeriodDebugInfos: String = "",

    var parentPeriodeVentOldID: Long = 0L,

    var parentPeriodeVentStartTimestampStr: String = "",

    //Section Infos ForgingKeys
    var parentM2ClientInfosKey: String = "Non Defini",
    var parentM2ClientInfosDebugName: String = "",

    var parentHClientKeyByParent: String = "",
    var parentHClientOldID: Long = 0L,
    var nomClientConcerned: String = "Non Defini",

    var parentKeyId9AppComptInfos: String = "b1",
    var parentDebugNameId9AppComptInfos: String = "",

    //Autres Infos ForgingKeys
    //PeriodeVen

    //Autres Infos ForgingKeys

    // Section InfosDeBase
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,


    // Section Centralization Valeurs Pour Injection add TOu modules

    var cLeDataOuvertDuParentList: Boolean? = null,
    var cActive: Boolean = false,

    //A Supp
    var vid: Long = 0L,
    var parentZAppComptNom: String = "",
    // Section keyFireBase et Update Version Id
    var keyFireBase: String = "",
    val parentID2ClientKeyByParent: String = "",
    val parentID7VentPeriodeKeyByParent: String = "",
    val parentID8C2TypeTransactionKeyByParent: String = "",
) {

    fun getCreationTimeString(): String {
        return try {
            val date = Date(creationTimestamps)
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            "00:00:00"
        }
    }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        CreeMaisNonDefinie(android.R.color.white, "غير محدد"),

        ON_MODE_COMMEND_ACTUELLEMENT(
            android.R.color.holo_green_light, "في طور تنفيذ المطلوب"
        ),
        A_COMMANDE_CONFIRME(
            android.R.color.holo_purple, "تم تاكيد الطلبية"
        ),
        PourVoirPanie(
            android.R.color.holo_red_light, "للنظر"
        ),
        COMMANDE_LIVRAI(android.R.color.holo_blue_dark, "تم أيصال منتجاته"),

        AVEC_MARCHANDISE(R.color.couleur1, "عندو سلعة"), ACHETEUR_NON_DISPO(
            R.color.c2, "الشاري غائب"
        ),
        FERME(android.R.color.darker_gray, "مغلق"),

        A_EVITE(android.R.color.black, "اقترح ان يتجنب لمدة اسبوعين"),

        RAPPORT_AU_ENREGESTREMENT_VOCALE(android.R.color.black, "التقرير قي التسجيل الصوتي "),

        ON_MODE_VOIRE_PANIE_ARTICLES(android.R.color.holo_blue_dark, "في معاينة السلة"),

        Cible(
            android.R.color.holo_red_light, "Cible"
        ),
        CIBLE_PRIORITE_2(android.R.color.holo_orange_dark, "CIBLE_PRIORITE_2"), CIBLE_PRIORITE_3(
            android.R.color.holo_green_light, "CIBLE_PRIORITE_3"
        ),
        CIBLE_POUR_2(android.R.color.holo_blue_dark, "CIBLE_POUR_2"),
        ;

        companion object {
            const val keyModel = "ID8C2"
            fun getKey(etate: EtateActuellementEst) =
                "--$keyModel-${etate.name}"
        }
    }

    fun isSameEntity(other: M8BonVent) =
        keyID == other.keyID && parentKeyId9AppComptInfos == other.parentKeyId9AppComptInfos && parentM7VentPeriodKeyId == other.parentM7VentPeriodKeyId

    override fun equals(other: Any?) =
        this === other || (other is M8BonVent && isSameEntity(other))

    override fun hashCode() = Objects.hash(
        keyID, parentZAppComptNom, parentM7VentPeriodKeyId
    )

    companion object {
        const val keyModel = "ID8"

        fun getKeyByParent(
            ventPeriodKeyByParent: String = null.toString(),
            clientKeyByParent: String = null.toString(),
            etateKeyByParent: String = null.toString(),
        ) = ("ID8---ID7-$ventPeriodKeyByParent--ID2-$clientKeyByParent--ID8C2-$etateKeyByParent")
            .withOutFireBaseInvalidCharacters()

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Datas08BonVent"
        )

        fun generePushKey() = genereUnPushKeyFireBase(ref)
    }
}
