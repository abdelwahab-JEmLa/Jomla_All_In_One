package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.H.Dao

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface C_CategorieProduitInfosDao {
    @Query("SELECT * FROM C_CategorieProduitInfos")
    fun getAllFlow(): Flow<List<C_CategorieProduitInfos>>

    @Query("SELECT * FROM C_CategorieProduitInfos")
    suspend fun getAll(): List<C_CategorieProduitInfos>

    @Query("SELECT * FROM C_CategorieProduitInfos WHERE id = :id")
    suspend fun getDataById(id: Long): C_CategorieProduitInfos?

    @Insert
    suspend fun insertData(data: C_CategorieProduitInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datas: List<C_CategorieProduitInfos>)

    @Update
    suspend fun updateData(data: C_CategorieProduitInfos)

    @Delete
    suspend fun deleteData(data: C_CategorieProduitInfos)

    @Query("DELETE FROM C_CategorieProduitInfos WHERE id = :id")
    suspend fun deleteDataById(id: Long)

    @Query("SELECT COUNT(*) FROM C_CategorieProduitInfos")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: C_CategorieProduitInfos)

    @Upsert
    suspend fun upsertAllDatas(datas: List<C_CategorieProduitInfos>)

    @Query("DELETE FROM C_CategorieProduitInfos")
    suspend fun deleteAll()

    @Query("DELETE FROM C_CategorieProduitInfos")
    suspend fun clearTableForRestart()


    @Query("DELETE FROM sqlite_sequence WHERE name = 'C_CategorieProduitInfos'")
    suspend fun resetAutoIncrement()

    /**
     * Complete restart: clears data and resets auto-increment
     * Use with caution - this will permanently delete all data
     */
    suspend fun restartRoom() {
        clearTableForRestart()
        resetAutoIncrement()
    }

    /**
     * Soft restart: just clear data, keep auto-increment sequence
     */
    suspend fun softRestartRoom() {
        clearTableForRestart()
    }

    /**
     * Check if table is completely empty (useful after restart)
     */
    @Query("SELECT COUNT(*) FROM C_CategorieProduitInfos")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
