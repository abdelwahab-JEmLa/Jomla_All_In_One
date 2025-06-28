package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.GrossistAchat.Fragment.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.B.Init.initializeDataReturn
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.B.Init.triggerUpdateFbParTimestampsListener
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test.setupLocalDbUpdateTracker
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class B_ClientInfosProtoJuin3Repository(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "B_ClientInfosProtoJuin3Repository"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    data class RepoState(
        val modelListFlow: List<B_ClientInfosProtoJuin3>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.B_ClientInfosProtoJuin3Dao()
    val repoRef = B_ClientInfosProtoJuin3.ref
    var isListenerRegistered = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Initialiser les données
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)

            // 2. Configurer le suivi des mises à jour de la BD locale
            setupLocalDbUpdateTracker()
        }
    }

    suspend fun updateRepoState(data: List<B_ClientInfosProtoJuin3>) {
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
