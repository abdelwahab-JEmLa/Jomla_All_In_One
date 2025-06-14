package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.addOrUpdateData(data: CategoriesTabelle) {
    CoroutineScope(Dispatchers.IO).launch {
        val _repoState = _repoState.value?.modelListFlow ?: emptyList()
        val getMaxIdPlus1 = if (_repoState.isEmpty()) {
            1L
        } else {
            (_repoState.maxOfOrNull { it.id } ?: 0L) + 1L
        }

        val dataWhithId = if (data.id == 0L) data.copy(id = getMaxIdPlus1) else data

        val preparedData = dataWhithId.withProperKeyFireBaseAndTimeTamp()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)
        repoRef.child(preparedData.keyFireBase).setValue(preparedData)
    }
}

