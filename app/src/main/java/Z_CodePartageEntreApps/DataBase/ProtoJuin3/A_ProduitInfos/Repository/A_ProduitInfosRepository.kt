package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Modules.AppDatabase
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
        val modelListFlow: List<M01Produit>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.dao_M1Produit()
    val ref = M01Produit.ref

    init {
        //ArticlesBasesStatsTable.securedRemoveFireBaseDB()

        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)
        }


    }

    suspend fun updateRepoState(data: List<M01Produit>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }
    }
}
