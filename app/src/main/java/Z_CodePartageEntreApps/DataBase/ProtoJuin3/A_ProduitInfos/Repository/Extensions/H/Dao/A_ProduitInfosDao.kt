package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.H.Dao

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface A_ProduitInfosProtoJuin3Dao {
    @Query("SELECT * FROM A_ProduitInfosProtoJuin3")
    fun getAllFlow(): Flow<List<A_ProduitInfosProtoJuin3>>

    @Query("SELECT * FROM A_ProduitInfosProtoJuin3")
    suspend fun getAll(): List<A_ProduitInfosProtoJuin3>

    @Query("SELECT * FROM A_ProduitInfosProtoJuin3 WHERE id = :id")
    suspend fun getDataById(id: Long): A_ProduitInfosProtoJuin3?

    @Insert
    suspend fun insertData(data: A_ProduitInfosProtoJuin3): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datas: List<A_ProduitInfosProtoJuin3>)

    @Update
    suspend fun updateData(data: A_ProduitInfosProtoJuin3)

    @Delete
    suspend fun deleteData(data: A_ProduitInfosProtoJuin3)

    @Query("DELETE FROM A_ProduitInfosProtoJuin3 WHERE id = :id")
    suspend fun deleteDataById(id: Long)

    @Query("SELECT COUNT(*) FROM A_ProduitInfosProtoJuin3")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: A_ProduitInfosProtoJuin3)

    @Upsert
    suspend fun upsertAllDatas(datas: List<A_ProduitInfosProtoJuin3>)

    @Query("DELETE FROM A_ProduitInfosProtoJuin3")
    suspend fun deleteAll()

    @Query("DELETE FROM A_ProduitInfosProtoJuin3")
    suspend fun clearTableForRestart()


    @Query("DELETE FROM sqlite_sequence WHERE name = 'A_ProduitInfosProtoJuin3'")
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
    @Query("SELECT COUNT(*) FROM A_ProduitInfosProtoJuin3")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
