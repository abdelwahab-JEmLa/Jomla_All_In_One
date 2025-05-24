package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class F_RoomOperationsHandler(
    private val database: AppDatabase,
    private val onProgressUpdate: (Float) -> Unit = { }
) {
    suspend fun checkDataBaseIsEmpty(
        onCheckIsTrue: (F_RoomOperationsHandler) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            val count = database.dTarificationInfosDao().getCount()

            onProgressUpdate(0.8f)

            if (count == 0) {
                onProgressUpdate(1f)
                onCheckIsTrue(this@F_RoomOperationsHandler)
            } else {
                onProgressUpdate(1f)
            }
        } catch (e: Exception) {
            onProgressUpdate(0f)
        }
    }

    suspend fun insertAllAndReturnListIdToData(
        data: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (data.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext true
            }

            onProgressUpdate(0.3f)

            val ids = database.dTarificationInfosDao()
                .insertAllReturnIDs(data)

            onProgressUpdate(0.7f)

            val resultMap = mutableMapOf<Long, D_TarificationInfos>()
            ids.forEachIndexed { index, generatedId ->
                if (index < data.size) {
                    val originalItem = data[index]
                    val itemWithGeneratedId = originalItem.copy(id = generatedId)
                    val itemWithDefaults = itemWithGeneratedId.withProperDefaults()
                    resultMap[generatedId] = itemWithDefaults
                }
            }

            onProgressUpdate(1f)
            onAddSuccess(resultMap)
            true
        } catch (e: Exception) {
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
            false
        }
    }
}
