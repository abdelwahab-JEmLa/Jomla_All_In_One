package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface B_ClientInfosDao {
    @Query("SELECT * FROM B_ClientInfos")
    fun getAllClients(): Flow<List<B_ClientInfos>>

    @Query("SELECT * FROM B_ClientInfos")
    suspend fun getAllClientsSync(): List<B_ClientInfos>

    @Query("SELECT * FROM B_ClientInfos WHERE id = :id")
    suspend fun getClientById(id: Long): B_ClientInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: B_ClientInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<B_ClientInfos>)

    @Update
    suspend fun update(client: B_ClientInfos)

    @Query("DELETE FROM B_ClientInfos")
    suspend fun deleteAll()
}

@Dao
interface C_TypeTarificationInfosDao {
    @Query("SELECT * FROM C_TypeTarificationInfos")
    fun getAllTypeTarifications(): Flow<List<C_TypeTarificationInfos>>

    @Query("SELECT * FROM C_TypeTarificationInfos")
    suspend fun getAllTypeTarificationsSync(): List<C_TypeTarificationInfos>

    @Query("SELECT * FROM C_TypeTarificationInfos WHERE id = :id")
    suspend fun getTypeTarificationById(id: Long): C_TypeTarificationInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(typeTarification: C_TypeTarificationInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(typeTarifications: List<C_TypeTarificationInfos>)

    @Update
    suspend fun update(typeTarification: C_TypeTarificationInfos)

    @Query("DELETE FROM C_TypeTarificationInfos")
    suspend fun deleteAll()
}

