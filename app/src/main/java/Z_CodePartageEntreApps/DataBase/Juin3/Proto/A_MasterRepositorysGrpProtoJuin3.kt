package Z_CodePartageEntreApps.DataBase.Juin3.Proto

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class MasterRepositorysModel(
    val repoStateA_ProduitInfos: A_ProduitInfosRepository.RepoState?,
    val b_ClientInfosProtoJuin3Repository: DataBaseFactoryFClient.RepoState?,
    val repoStateC_CategorieProduitInfos: C_CategorieProduitInfosRepository.RepoState?,
    val d_EtateMessageVocaleRepository: D_EtateMessageVocaleRepository.RepoState?,

    val progress: Float = 0f
)

class A_MasterRepositorysGrpProtoJuin3(
    val repoA_ProduitInfos: A_ProduitInfosRepository,
    val b_ClientInfosProtoJuin3Repository: DataBaseFactoryFClient,
    val repoC_CategorieProduitInfos: C_CategorieProduitInfosRepository,
    val d_EtateMessageVocaleRepository: D_EtateMessageVocaleRepository,
    val e_GroupedDataBasesRepositoryProtoAvant3Juin: GroupeRepositorysProtoAvJuin3,
) {
    private val _model = MutableStateFlow<MasterRepositorysModel?>(null)
    val model: StateFlow<MasterRepositorysModel?> = _model.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            combine(
                repoA_ProduitInfos.repoState,
                b_ClientInfosProtoJuin3Repository.repoState,
                repoC_CategorieProduitInfos.repoState,
                d_EtateMessageVocaleRepository.repoState,
                e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model.c3TransactionCommercialRepository.progressRepo,
            ) { repoA_ProduitInfos,
                b_ClientInfosProtoJuin3Repository,
                repoC_CategorieProduitInfos,
                d_EtateMessageVocaleRepository ,
                c3_BonAchate_Repository,
                ->
                val progressA = repoA_ProduitInfos?.mainProgressRepo ?: 0f
                val progressC = repoC_CategorieProduitInfos?.mainProgressRepo ?: 0f
                val progressD = d_EtateMessageVocaleRepository?.mainProgressRepo ?: 0f
                val combinedProgress = (
                        progressA
                                + progressC
                                + progressD
                                + c3_BonAchate_Repository
                                + (b_ClientInfosProtoJuin3Repository?.mainProgressRepo ?: 0f)
                        ) / 2f

                MasterRepositorysModel(
                    repoStateA_ProduitInfos = repoA_ProduitInfos,
                    b_ClientInfosProtoJuin3Repository = b_ClientInfosProtoJuin3Repository,
                    repoStateC_CategorieProduitInfos = repoC_CategorieProduitInfos,
                    d_EtateMessageVocaleRepository = d_EtateMessageVocaleRepository,
                    progress = combinedProgress
                )
            }.collect { masterModel ->
                _model.value = masterModel
            }
        }
    }
}
