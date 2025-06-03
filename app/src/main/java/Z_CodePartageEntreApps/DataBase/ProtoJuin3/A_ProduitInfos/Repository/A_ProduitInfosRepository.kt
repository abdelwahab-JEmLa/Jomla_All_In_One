package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository

import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.A_ProduitInfos.Repository.initializeDataReturn
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class A_ProduitInfosRepository(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "A_ProduitInfosRepository"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()
    data class RepoState(
        val modelListFlow: List<A_ProduitInfosProtoJuin3>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.A_ProduitInfosProtoJuin3Dao()
    val ref = A_ProduitInfosProtoJuin3.caRef

    init {
        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)
        }
    }

    suspend fun updateRepoState(data: List<A_ProduitInfosProtoJuin3>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }
    }
}
