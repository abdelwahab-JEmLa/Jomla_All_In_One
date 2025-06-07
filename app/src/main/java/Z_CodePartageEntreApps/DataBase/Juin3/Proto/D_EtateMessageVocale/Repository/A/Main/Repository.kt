package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.D_EtateMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init.initializeDataReturn
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class D_EtateMessageVocaleRepository(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "D_EtateMessageVocaleRepository"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    data class RepoState(
        val modelListFlow: List<D_EtateMessageVocale>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.D_EtateMessageVocaleDao()
    val ref = D_EtateMessageVocale.caRef

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val initializedData =
                initializeDataReturn()
            updateRepoState(initializedData)
        }
    }

    suspend fun updateRepoState(data: List<D_EtateMessageVocale>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }
    }
}
