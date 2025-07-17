package V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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

    // Fixed: Convert to StateFlow to properly observe state changes in Compose
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
    @PrimaryKey var keyID: String = RepositorysMainGetter.getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var creationTimestamps: Long = 0,

    //Infos De Base
    var etate: Etate = Etate.EN_COURT_ENREGESTREMENT,
    var relativeAuDataBase: TypeDeSonRelativeModel = TypeDeSonRelativeModel.C3_BonAchate,

    val parentMessageVID: Long = 0,
    val nomDeSonOriginaleFichie: String = "null.3gp",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M9AppCompt_KeyID: String = "null",
    val parent_M9AppCompt_DebugInfos: String = "null",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------
    val parent_M8BonVent_KeyID: String = "null",
    val parent_M8BonVent_DebugInfos: String = "null",

    //---------------------------------ForgingKeys.M9AppCompt----------------------------------------------------------------------------------------------------------------------------------

    //Etates Mutable
) {
    fun getDebugInfos(): String {
        return buildString {
            append("KeyID: ${keyID.takeLast(4).uppercase()}\n")
            append(" To ")
            append(creeStrDate_Et_Time_Depuit_CreationTT(creationTimestamps))
            append(" To ")
            append(etate)
        }
    }

    enum class TypeDeSonRelativeModel() {
        NONE,
        C3_BonAchate,
    }

    enum class Etate(val nomArabe: String? = null) {
        EN_COURT_ENREGESTREMENT,
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

        fun removeRef(preparedData: M17MessageVocale) { ArticlesBasesStatsTable.ref.child(preparedData.keyID).removeValue() }

    }
}
