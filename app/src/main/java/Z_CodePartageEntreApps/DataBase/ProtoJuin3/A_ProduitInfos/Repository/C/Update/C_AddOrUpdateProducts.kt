package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun A_ProduitInfosRepository.addOrUpdateData(data: ArticlesBasesStatsTable) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedData = data.withProperKeyFireBaseAndTimeTamp()

        dao.upsertData(preparedData)

        batchFireBaseUpdateA_ProduitInfos(listOf(preparedData))

        // Fixed: Refresh the repository state with all current data from the database
        val allData = dao.getAll()
        updateRepoState(allData)
    }
}

