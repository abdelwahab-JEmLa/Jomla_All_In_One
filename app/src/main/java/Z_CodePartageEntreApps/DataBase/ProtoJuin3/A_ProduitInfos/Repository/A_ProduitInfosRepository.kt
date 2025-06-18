package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
        val modelListFlow: List<ArticlesBasesStatsTable>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.ArticlesBasesStatsModelDao()
    val ref = ArticlesBasesStatsTable.ref

    init {
        //ArticlesBasesStatsTable.securedRemoveFireBaseDB()

        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)
        }


    }

    suspend fun updateRepoState(data: List<ArticlesBasesStatsTable>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState
        }
    }
}
