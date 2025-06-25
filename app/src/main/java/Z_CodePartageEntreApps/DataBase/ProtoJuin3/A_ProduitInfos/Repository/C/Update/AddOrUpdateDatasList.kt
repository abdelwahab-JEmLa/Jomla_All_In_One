package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun A_ProduitInfosRepository.addOrUpdateDatasList(datas: List<ArticlesBasesStatsTable>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        batchFireBaseUpdateA_ProduitInfos(preparedDatas)

        updateRepoState(preparedDatas)
    }
}
