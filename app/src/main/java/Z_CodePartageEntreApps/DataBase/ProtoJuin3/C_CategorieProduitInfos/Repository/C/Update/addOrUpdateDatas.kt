package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.addOrUpdateDatas(
    datas: List<CategoriesTabelle>,
    avecFireBase: Boolean = false
) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withDernierTimeTampsSynchronisationAvecFireBase() }

        preparedDatas.find { it.id ==149L }
            ?.let { CategoriesTabelle.logCategory(it,"C_CategorieProduitInfosRepository") }

        dao.upsertAllDatas(preparedDatas)

        avecFireBase.batchFireBaseUpdate(preparedDatas)

        updateRepoState(preparedDatas)
    }
}

