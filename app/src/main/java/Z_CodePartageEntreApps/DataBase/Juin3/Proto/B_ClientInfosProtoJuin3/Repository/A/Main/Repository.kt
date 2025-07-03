package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main

import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientInfos
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class dataBaseCreationFactoryMID2ClientRepository(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "dataBaseCreationFactoryMID2ClientRepository"
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    data class RepoState(
        val modelListFlow: List<HClientInfos>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.B_ClientInfosProtoJuin3Dao()
    val repoRef = HClientInfos.ref
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

    suspend fun updateRepoState(data: List<HClientInfos>) {
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
    fun set(
        dataAvecTigerUpdate: HClientInfos,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdate(listOf(dataAvecTigerUpdate))
        }
    }

    fun batchFireBaseUpdate(datas: List<HClientInfos>): Unit {
        CoroutineScope(Dispatchers.IO).launch {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.keyID] = data
            }
            repoRef.updateChildren(updates).await()
        }
    }
}
