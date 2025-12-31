package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Init.initializeDataReturn
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Init.triggerUpdateFbParTimestampsListener
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Test.setupLocalDbUpdateTracker
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
        val modelListFlow: List<M2Client>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.DaoM2Client()
    val repoRef = M2Client.ref
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

    suspend fun updateRepoState(data: List<M2Client>) {
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
        dataAvecTigerUpdate: M2Client,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdate(listOf(dataAvecTigerUpdate))
        }
    }
    fun delete(dataToDelete: M2Client) {
        factoryScope.launch {
            // Delete from local Room database
            dao.delete(dataToDelete)

            // Delete from Firebase
            repoRef.child(dataToDelete.keyID).removeValue().await()

            // Update repo state
            val updatedData = dao.getAll()
            updateRepoState(updatedData)
        }
    }

    fun batchFireBaseUpdate(datas: List<M2Client>): Unit {
        CoroutineScope(Dispatchers.IO).launch {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.keyID] = data
            }
            repoRef.updateChildren(updates).await()
        }
    }
}
