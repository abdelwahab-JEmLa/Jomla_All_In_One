package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun A_ProduitInfosRepository.addOrUpdateData(data: ArticlesBasesStatsTable) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedData = data.withProperKeyFireBaseAndTimeTamp()

        dao.upsertData(preparedData)

        ref.child(preparedData.keyFireBase).setValue(preparedData)

        // Fixed: Refresh the repository state with all current data from the database
        val allData = dao.getAll()
        updateRepoState(allData)
    }
}

fun A_ProduitInfosRepository.addOrUpdateDatasList(datas: List<ArticlesBasesStatsTable>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            ref.child(data.keyFireBase).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
