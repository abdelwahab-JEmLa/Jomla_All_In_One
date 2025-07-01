package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.H.Dao

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
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
interface ArticlesBasesStatsModelDao {
    @Update
    suspend fun update(data: ArticlesBasesStatsTable)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: ArticlesBasesStatsTable)

    @Delete
    suspend fun delete(article: ArticlesBasesStatsTable)

    @Query("SELECT * FROM ArticlesBasesStatsTable")
    suspend fun getAll(): MutableList<ArticlesBasesStatsTable>

    @Transaction
    suspend fun transaction(block: suspend ArticlesBasesStatsModelDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<ArticlesBasesStatsTable>)

    @Query("DELETE FROM ArticlesBasesStatsTable")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(articlesBasesStatTabelles: List<ArticlesBasesStatsTable>)
    @Query("SELECT * FROM ArticlesBasesStatsTable")
    fun getAllFlow(): Flow<List<ArticlesBasesStatsTable>>


    @Insert
    suspend fun insertData(data: ArticlesBasesStatsTable): Long


    @Update
    suspend fun updateData(data: ArticlesBasesStatsTable)

    @Delete
    suspend fun deleteData(data: ArticlesBasesStatsTable)


    @Query("SELECT COUNT(*) FROM ArticlesBasesStatsTable")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: ArticlesBasesStatsTable)

    @Upsert
    suspend fun upsertAllDatas(datas: List<ArticlesBasesStatsTable>)


    @Query("DELETE FROM ArticlesBasesStatsTable")
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
    @Query("SELECT COUNT(*) FROM ArticlesBasesStatsTable")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
