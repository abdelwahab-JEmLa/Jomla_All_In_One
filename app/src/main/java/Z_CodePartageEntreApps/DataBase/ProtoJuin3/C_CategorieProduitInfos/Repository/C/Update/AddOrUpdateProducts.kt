package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.addOrUpdateDatas(datas: List<CategoriesTabelle>) {
    CoroutineScope(Dispatchers.IO).launch {
        val preparedDatas = datas.map { it.withProperKeyFireBaseAndTimeTamp() }

        dao.upsertAllDatas(preparedDatas)

        preparedDatas.forEach { data ->
            ref.child(data.keyFireBase).setValue(data)
        }

        updateRepoState(preparedDatas)
    }
}
