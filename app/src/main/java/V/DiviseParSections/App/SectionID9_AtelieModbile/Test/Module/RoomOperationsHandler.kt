package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomOperationsHandler(private val database: AppDatabase) {
    suspend fun upsertAllAndReturnListIdToData(
        data: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val ids = database.dTarificationInfosDao().upsertAllAndReturnIDs(data)

            val resultMap = mutableMapOf<Long, D_TarificationInfos>()
            ids.forEachIndexed { index, id ->
                if (index < data.size) {
                    val updatedTarif = data[index].copy(id = id)
                    resultMap[id] = updatedTarif
                }
            }

            onAddSuccess(resultMap)
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in upsertAllAndReturnListIdToData: ${e.message}")
            false
        }
    }

    suspend fun insertAll(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in insertAll: ${e.message}")
            false
        }
    }

    suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().deleteAll()
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in deleteAll: ${e.message}")
            false
        }
    }

    suspend fun updateData(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            if (data.d_TarificationInfos.isNotEmpty()) {
                database.dTarificationInfosDao().deleteAll()
                database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            }
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in updateData: ${e.message}")
            false
        }
    }

    suspend fun getAllData(): DataBasesInfosSql = withContext(Dispatchers.IO) {
        try {
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()

            DataBasesInfosSql(
                d_TarificationInfos = tarifications.toMutableList()
            )
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in getAllData: ${e.message}")
            DataBasesInfosSql()
        }
    }

    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        try {
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            tarifications.isEmpty()
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in isDatabaseEmpty: ${e.message}")
            true // Assume empty if there's an error
        }
    }
}
