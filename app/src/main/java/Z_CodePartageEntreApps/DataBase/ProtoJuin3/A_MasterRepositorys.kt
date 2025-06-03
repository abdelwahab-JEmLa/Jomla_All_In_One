package Z_CodePartageEntreApps.DataBase.ProtoJuin3

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class MasterRepositorysModel(
    val repoStateA_ProduitInfos: A_ProduitInfosRepository.RepoState?,
    val repoStateC_CategorieProduitInfos: C_CategorieProduitInfosRepository.RepoState?,
    val progress: Float = 0f
)

class A_MasterRepositorys(
    val repoA_ProduitInfos: A_ProduitInfosRepository,
    val repoC_CategorieProduitInfos: C_CategorieProduitInfosRepository,
) {
    private val _model = MutableStateFlow<MasterRepositorysModel?>(null)
    val model: StateFlow<MasterRepositorysModel?> = _model.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            combine(
                repoA_ProduitInfos.repoState,
                repoC_CategorieProduitInfos.repoState
            ) { repoA_ProduitInfos, repoC_CategorieProduitInfos ->
                val progressA = repoA_ProduitInfos?.mainProgressRepo ?: 0f
                val progressC = repoC_CategorieProduitInfos?.mainProgressRepo ?: 0f
                val combinedProgress = (progressA + progressC) / 2f

                MasterRepositorysModel(
                    repoStateA_ProduitInfos = repoA_ProduitInfos,
                    repoStateC_CategorieProduitInfos = repoC_CategorieProduitInfos,
                    progress = combinedProgress
                )
            }.collect { masterModel ->
                _model.value = masterModel
            }
        }
    }
}
