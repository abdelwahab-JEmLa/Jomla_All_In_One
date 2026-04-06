package V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init.initializeDataReturn
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init.triggerUpdateFbParTimestampsListener
import Z_CodePartageEntreApps.Modules.DatesHandler.Companion.creeStrDate_Et_Time_Depuit_CreationTT
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class Repo17MessageVocale(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "Repo17MessageVocale"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    val datasValue: StateFlow<List<M17MessageVocale>> =
        repoState.map { state ->
            state?.modelListFlow ?: emptyList()
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Main),
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    data class RepoState(
        val modelListFlow: List<M17MessageVocale>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.M17MessageVocaleDao()
    val repoRef = M17MessageVocale.ref
    var isListenerRegistered = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)
        }
    }

    suspend fun updateRepoState(data: List<M17MessageVocale>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }


        if (!isListenerRegistered) {
            triggerUpdateFbParTimestampsListener()
        }
    }
}

@Entity
data class M17MessageVocale(
    @PrimaryKey var keyID: String = generePushKey(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var creationTimestamps: Long = 0,

    //Infos De Base
    var etate: Etate = Etate.EN_COURT_ENREGESTREMENT,
    var relativeAuDataBase: TypeDeSonRelativeModel = TypeDeSonRelativeModel.C3_BonAchate,

    var ceMessage_Est_Important_Au_Ecoute: Boolean = false,

    var its_Text_Message: Boolean = false,
    var text_Inputted: String = "",

    val parentMessageVID: Long = 0,
    val nomDeSonOriginaleFichie: String = "null.3gp",

    val its_Video_Message:  Boolean = false,
    val nom_Fichie_Video: String = "",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "",
    val parent_M9AppCompt_DebugInfos: String = "",
    //---------------------------------ForgingKeys.Repeated_Datas.M9AppCompt.----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_Nom: String = "",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M8BonVent_KeyID: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------

    //Etates Mutable
) {
    fun getDebugInfos(): String {
        return buildString {
            append("Mes_17")
            append("[")
            append("{${keyID.takeLast(4).uppercase()}}\n")
            append(" To ")
            append(creeStrDate_Et_Time_Depuit_CreationTT(creationTimestamps).second)
            append(" To ")
            append(etate)
            append("]")
        }
    }

    enum class TypeDeSonRelativeModel() {
        NONE,
        C3_BonAchate,
    }

    enum class Etate(val nomArabe: String? = null) {
        EN_COURT_ENREGESTREMENT,
        Premier_Test_Envoi,
        ENVOYER,
        VUE,
        ECOUTE,
    }

    companion object {
        val ref = RepositorysMainGetter.centralRef.child("Datas17MessageVocale")
        fun get_default(
        ): M17MessageVocale {
            return M17MessageVocale(
            )
        }

        fun generePushKey() = ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun createTestInstance(): List<M17MessageVocale> { return emptyList() }

        fun removeRef(preparedData: M17MessageVocale) { M01Produit.ref.child(preparedData.keyID).removeValue() }

    }
}
