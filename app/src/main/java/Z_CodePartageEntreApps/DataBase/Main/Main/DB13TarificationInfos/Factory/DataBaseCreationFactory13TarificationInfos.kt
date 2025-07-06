package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DataBaseCreationFactory13TarificationInfos(
    appDatabase: AppDatabase
) {
    val dao = appDatabase.Dao13TarificationInfos()
    val repoEntityName = "DataBaseCreationFactoryGBonVent"
    val repoTAG = repoEntityName
    val repoRef = M8BonVent.ref
    private val factoryScope = CoroutineScope(Dispatchers.IO)

    fun set(
        dataAvecTigerUpdate: M13TarificationInfos,
    ) {
        factoryScope.launch {
            dao.upsert(dataAvecTigerUpdate)
            batchFireBaseUpdateGBonVent(listOf(dataAvecTigerUpdate))
        }
    }

    private suspend fun batchFireBaseUpdateGBonVent(datas: List<M13TarificationInfos>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.keyID] = data
        }
        repoRef.updateChildren(updates).await()
    }

    fun delete(data: M13TarificationInfos) {

    }
}
