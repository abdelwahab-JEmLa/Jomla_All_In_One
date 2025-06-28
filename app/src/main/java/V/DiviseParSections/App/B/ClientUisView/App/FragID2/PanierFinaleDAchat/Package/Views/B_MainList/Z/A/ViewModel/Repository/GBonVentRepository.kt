package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ASetterCentral.Companion.genereUnPushKeyFireBase
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GBonVent.EtateActuellementEst
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
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
class GBonVentRepository(
    val gDataBaseTransactionCommercial: FVentCouleurOperationRepository,
    val ancienRepo: A_MasterRepositorysGrpProtoJuin3,
    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _datas = mutableStateOf<List<GBonVent>>(emptyList())
    val datasState: State<List<GBonVent>> = _datas
    val datasValue by derivedStateOf { _datas.value }

    val onVentData by derivedStateOf { datasValue.find { it.keyID == zAppComptRepositoryComposable.ouvertData?.onVentGBonVentKeyId } }

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: State<Float> = _loadingProgress

    val size: Int by derivedStateOf { _datas.value.size }
    val isEmpty: Boolean by derivedStateOf { _datas.value.isEmpty() }

    init {
        composScope.launch {
            ancienRepo.model.collect { masterModel ->
                masterModel?.let { model ->
                    updateLoadingProgress(model.progress)
                }
            }
        }

        composScope.launch {
            snapshotFlow {
                ancienRepo.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model.c3TransactionCommercialRepository.modelDatasSnapList.toList()
            }.collect { list ->
                updateDatas(list)
            }
        }
    }

    fun getClientLastTransactionParEtate(
        clientId: Long,
        etateActuellementEst: EtateActuellementEst = EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ): GBonVent? {
        return datasValue.filter {
            it.parentHClientOldID == clientId && it.etateActuellementEst == etateActuellementEst
        }.maxByOrNull { it.keyID }
    }

    fun getClientLastTransaction(clientId: Long): GBonVent? {
        return datasValue.filter {
            it.parentHClientOldID == clientId
        }.maxByOrNull { it.keyID }
    }

    fun updateLoadingProgress(progress: Float) {
        _loadingProgress.floatValue = progress
    }

    fun updateDatas(newDatas: List<GBonVent>) {
        _datas.value = newDatas
    }

    fun addOrUpdateData(data: GBonVent) {
        val dataUpdate =
            data.copy(dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis())
        val existingIndex = datasValue.indexOfFirst { it.isSameEntity(dataUpdate) }

        composScope.launch {
            withContext(Dispatchers.Main.immediate) {
                _datas.value = if (existingIndex >= 0) {
                    datasValue.toMutableList().apply {
                        this[existingIndex] = this[existingIndex].copy(
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )
                    }
                } else datasValue + dataUpdate
            }
        }

        ancienRepoUpsertUneDataEtReturnVID(dataUpdate)
    }

    private fun ancienRepoUpsertUneDataEtReturnVID(dataUpdate: GBonVent) {
        ancienRepo.e_GroupedDataBasesRepositoryProtoAvant3Juin.upsertUneDataEtReturnVID(
            dataUpdate
        )
    }
}

@Entity
data class GBonVent(
    @PrimaryKey var keyID: String = "",
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = DatesHandler().getCurrentTimestamps(),

    //Section Forging Keys
    //PeriodeVen
    var parentPeriodeVentKeyID: String = "",
    var parentPeriodeVentStartTimestampStr: String = "",

    //Section Infos ForgingKeys
    var parentHClientKeyID: Long = 0L,
    var nomClientConcerned: String = "Non Defini",
    var parentZAppComptCreateurKeyID: String = "b1",

    //Autres Infos ForgingKeys
    //PeriodeVen
    var parentPeriodeVentOldID: Long = 0L,

    //Autres Infos ForgingKeys
    var parentHClientOldID: Long = 0L,

    // Section InfosDeBase
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.CreeMaisNonDefinie,
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
) {
    val fireBaseKeyID_1_3_TransactionCommercial: String
        get() {
            val parent = "(${parentPeriodeVentOldID})"
            val thisVal = "->(${parentHClientOldID}_($nomClientConcerned))"

            val name = etateActuellementEst.nomArabe

            val autre = "->($name)"

            return "$parent$thisVal$autre"
        }

    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Int, val nomArabe: String) {
        CreeMaisNonDefinie(android.R.color.white, "غير محدد"),

        ON_MODE_COMMEND_ACTUELLEMENT(
            android.R.color.holo_green_light, "تم تنفيذ المطلوب في "
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
    }

    fun isSameEntity(other: GBonVent) =
        keyID == other.keyID && parentZAppComptCreateurKeyID == other.parentZAppComptCreateurKeyID && parentPeriodeVentKeyID == other.parentPeriodeVentKeyID

    override fun equals(other: Any?) =
        this === other || (other is GBonVent && isSameEntity(other))

    override fun hashCode() = Objects.hash(
        keyID, parentZAppComptNom, parentPeriodeVentKeyID
    )

    companion object {
        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/GBonVent"
        )
        fun generePushKey()= genereUnPushKeyFireBase(ref)
    }
}
