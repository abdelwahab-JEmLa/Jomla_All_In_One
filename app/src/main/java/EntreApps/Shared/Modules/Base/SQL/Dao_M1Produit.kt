package EntreApps.Shared.Modules.Base.SQL

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao_M1Produit {
    @Update
    suspend fun update(data: M01Produit)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(category: M01Produit)

    @Delete
    suspend fun delete(article: M01Produit)

    @Query("SELECT * FROM M01Produit")
    suspend fun getAll(): MutableList<M01Produit>

    @Transaction
    suspend fun transaction(block: suspend Dao_M1Produit.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<M01Produit>)

    @Query("DELETE FROM M01Produit")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(articlesBasesStatTabelles: List<M01Produit>)
    @Query("SELECT * FROM M01Produit")
    fun getAllFlow(): Flow<List<M01Produit>>


    @Insert
    suspend fun insertData(data: M01Produit): Long


    @Update
    suspend fun updateData(data: M01Produit)

    @Delete
    suspend fun deleteData(data: M01Produit)


    @Query("SELECT COUNT(*) FROM M01Produit")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: M01Produit)

    @Upsert
    suspend fun upsertAllDatas(datas: List<M01Produit>)


    @Query("DELETE FROM M01Produit")
    suspend fun clearTableForRestart()


    @Query("DELETE FROM sqlite_sequence WHERE name = 'ArticlesBasesStatsTable'")
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
    @Query("SELECT COUNT(*) FROM M01Produit")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
