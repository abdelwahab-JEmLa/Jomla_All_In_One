package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun A_ProduitInfosRepository.deleteData(data: ArticlesBasesStatsTable) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedData = data.withProperKeyFireBaseAndTimeTamp()

        dao.deleteData(preparedData)

        ArticlesBasesStatsTable.removeRef(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
    }
}
