package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomOperationsHandler(private val database: AppDatabase) {
    suspend fun insertAll(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().deleteAll()
            true
        } catch (e: Exception) {
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
            DataBasesInfosSql()
        }
    }
    
    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        try {
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            
            tarifications.isEmpty()
        } catch (e: Exception) {
            true // Assume empty if there's an error
        }
    }
}
