package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module.SQl

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface D_TarificationInfosDao
{
    @Query("SELECT * FROM D_TarificationInfos")
    fun getAllTarifications(): Flow<List<D_TarificationInfos>>

    @Query("SELECT * FROM D_TarificationInfos")
    suspend fun getAllTarificationsSync(): List<D_TarificationInfos>

    @Query("SELECT * FROM D_TarificationInfos WHERE id = :id")
    suspend fun getTarificationById(id: Long): D_TarificationInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllAndReturnIDs(items: List<D_TarificationInfos>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarifications: List<D_TarificationInfos>)

    @Update
    suspend fun update(tarification: D_TarificationInfos)

    @Query("DELETE FROM D_TarificationInfos")
    suspend fun deleteAll()

    // First, make sure the insert method returns Long:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarification: D_TarificationInfos): Long
}
