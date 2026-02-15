package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory

import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
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
interface M16CategorieProduitDao {

    @Delete
    fun delete(data: M16CategorieProduit)

    @Update
    suspend fun update(data: M16CategorieProduit)

    @Upsert
    suspend fun upsert(data: M16CategorieProduit)

    @Query("SELECT * FROM M16CategorieProduit ORDER BY position")
    suspend fun getAll(): MutableList<M16CategorieProduit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: M16CategorieProduit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<M16CategorieProduit>)

    @Query("DELETE FROM M16CategorieProduit")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<M16CategorieProduit>)

    @Transaction
    suspend fun transaction(block: suspend M16CategorieProduitDao.() -> Unit) {
        block()
    }

    @Query("SELECT * FROM M16CategorieProduit")
    fun getAllFlow(): Flow<List<M16CategorieProduit>>


    @Query("SELECT * FROM M16CategorieProduit WHERE id = :id")
    suspend fun getDataById(id: Long): M16CategorieProduit?

    @Insert
    suspend fun insertData(data: M16CategorieProduit): Long


    @Update
    suspend fun updateData(data: M16CategorieProduit)

    @Delete
    suspend fun deleteData(data: M16CategorieProduit)

    @Query("DELETE FROM M16CategorieProduit WHERE id = :id")
    suspend fun deleteDataById(id: Long)

    @Query("SELECT COUNT(*) FROM M16CategorieProduit")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: M16CategorieProduit)

    @Upsert
    suspend fun upsertAllDatas(datas: List<M16CategorieProduit>)


    @Query("DELETE FROM M16CategorieProduit")
    suspend fun clearTableForRestart()


    @Query("DELETE FROM sqlite_sequence WHERE name = 'CategoriesTabelle'")
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
    @Query("SELECT COUNT(*) FROM M16CategorieProduit")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
