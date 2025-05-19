package V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.Module.SQl

import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomOperationsHandler(private val database: AppDatabase) {
    suspend fun insertAll(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
            database.b_ClientInfosDao().insertAll(data.b_ClientInfosList)
            database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            database.a_ProduitInfosDao().deleteAll()
            database.b_ClientInfosDao().deleteAll()
            database.c_TypeTarificationInfosDao().deleteAll()
            database.dTarificationInfosDao().deleteAll()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateData(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            if (data.a_ProduitInfos.isNotEmpty()) {
                database.a_ProduitInfosDao().deleteAll()
                database.a_ProduitInfosDao().insertAll(data.a_ProduitInfos)
            }

            if (data.b_ClientInfosList.isNotEmpty()) {
                database.b_ClientInfosDao().deleteAll()
                database.b_ClientInfosDao().insertAll(data.b_ClientInfosList)
            }

            if (data.c_TypeTarificationInfos.isNotEmpty()) {
                database.c_TypeTarificationInfosDao().deleteAll()
                database.c_TypeTarificationInfosDao().insertAll(data.c_TypeTarificationInfos)
            }

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
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications = database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            
            DataBasesInfosSql(
                a_ProduitInfos = produits.toMutableList(),
                b_ClientInfosList = clients.toMutableList(),
                c_TypeTarificationInfos = typeTarifications.toMutableList(),
                d_TarificationInfos = tarifications.toMutableList()
            )
        } catch (e: Exception) {
            DataBasesInfosSql()
        }
    }
    
    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        try {
            val produits = database.a_ProduitInfosDao().getAllProduitsSync()
            val clients = database.b_ClientInfosDao().getAllClientsSync()
            val typeTarifications = database.c_TypeTarificationInfosDao().getAllTypeTarificationsSync()
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            
            produits.isEmpty() && clients.isEmpty() && typeTarifications.isEmpty() && tarifications.isEmpty()
        } catch (e: Exception) {
            true // Assume empty if there's an error
        }
    }
}
