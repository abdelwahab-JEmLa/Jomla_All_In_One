package Z_CodePartageEntreApps.Proto.Par.Type.Repository

import Z_CodePartageEntreApps.Proto.Par.Type.Models.C_TypeTarificationInfos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

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

