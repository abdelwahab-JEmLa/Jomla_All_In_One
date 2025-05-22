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

    // Fixed: Proper batch upsert that handles new items correctly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnoreConflicts(items: List<D_TarificationInfos>): List<Long>

    // This is the corrected method for upsert with proper ID handling
    suspend fun upsertAllAndReturnIDs(items: List<D_TarificationInfos>): List<Long> {
        val result = mutableListOf<Long>()

        for (item in items) {
            val id = if (item.id == 0L) {
                // New item - let auto-increment handle it
                insert(item.copy(id = 0L))
            } else {
                // Existing item - check if it exists and update or insert
                if (existsById(item.id) > 0) {
                    update(item)
                    item.id
                } else {
                    forceInsert(item)
                }
            }
            result.add(id)
        }

        return result
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarifications: List<D_TarificationInfos>)

    @Update
    suspend fun update(tarification: D_TarificationInfos)

    @Query("DELETE FROM D_TarificationInfos")
    suspend fun deleteAll()

    // For single insert - use IGNORE to let auto-increment work properly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: D_TarificationInfos): Long

    // Alternative insert method that forces a new ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun forceInsert(tarification: D_TarificationInfos): Long

    // Method to check if an item exists
    @Query("SELECT COUNT(*) FROM D_TarificationInfos WHERE id = :id")
    suspend fun existsById(id: Long): Int
}
