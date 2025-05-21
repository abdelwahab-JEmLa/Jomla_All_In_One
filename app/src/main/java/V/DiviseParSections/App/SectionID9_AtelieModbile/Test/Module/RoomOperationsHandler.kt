package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomOperationsHandler(
    private val database: AppDatabase,
    private val onProgressUpdate: (Float) -> Unit = { }
) {
    suspend fun upsertAllAndReturnListIdToData(
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
            val ids = database.dTarificationInfosDao().upsertAllAndReturnIDs(data)

            onProgressUpdate(0.7f)
            val resultMap = mutableMapOf<Long, D_TarificationInfos>()
            ids.forEachIndexed { index, id ->
                if (index < data.size) {
                    val updatedTariff = data[index].copy(id = id)
                    resultMap[id] = updatedTariff
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

    suspend fun insertAll(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            if (data.d_TarificationInfos.isEmpty()) {
                onProgressUpdate(1f)
                return@withContext true
            }

            onProgressUpdate(0.6f)
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)

            onProgressUpdate(1f)
            true
        } catch (e: Exception) {
            onProgressUpdate(0f)
            false
        }
    }

    suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.3f)
            database.dTarificationInfosDao().deleteAll()
            onProgressUpdate(1f)
            true
        } catch (e: Exception) {
            onProgressUpdate(0f)
            false
        }
    }

    suspend fun updateData(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (data.d_TarificationInfos.isNotEmpty()) {
                onProgressUpdate(0.3f)
                database.dTarificationInfosDao().deleteAll()

                onProgressUpdate(0.7f)
                database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            }

            onProgressUpdate(1f)
            true
        } catch (e: Exception) {
            onProgressUpdate(0f)
            false
        }
    }

    suspend fun getAllData(): DataBasesInfosSql = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.3f)
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

            onProgressUpdate(1f)
            DataBasesInfosSql(
                d_TarificationInfos = tarifications.toMutableList()
            )
        } catch (e: Exception) {
            onProgressUpdate(0f)
            DataBasesInfosSql()
        }
    }
}
