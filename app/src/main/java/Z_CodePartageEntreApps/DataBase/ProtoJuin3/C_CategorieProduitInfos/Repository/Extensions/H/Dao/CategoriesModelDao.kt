package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.H.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.A2_Passive.CategoriesTabelle
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
interface CategoriesModelDao {
    @Upsert
    suspend fun upsert(data: CategoriesTabelle)

    @Query("SELECT * FROM CategoriesTabelle ORDER BY position")
    suspend fun getAll(): MutableList<CategoriesTabelle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoriesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoriesTabelle>)

    @Query("DELETE FROM CategoriesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<CategoriesTabelle>)

    @Transaction
    suspend fun transaction(block: suspend CategoriesModelDao.() -> Unit) {
        block()
    }

    @Query("SELECT * FROM CategoriesTabelle")
    fun getAllFlow(): Flow<List<CategoriesTabelle>>


    @Query("SELECT * FROM CategoriesTabelle WHERE id = :id")
    suspend fun getDataById(id: Long): CategoriesTabelle?

    @Insert
    suspend fun insertData(data: CategoriesTabelle): Long


    @Update
    suspend fun updateData(data: CategoriesTabelle)

    @Delete
    suspend fun deleteData(data: CategoriesTabelle)

    @Query("DELETE FROM CategoriesTabelle WHERE id = :id")
    suspend fun deleteDataById(id: Long)

    @Query("SELECT COUNT(*) FROM CategoriesTabelle")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsertData(data: CategoriesTabelle)

    @Upsert
    suspend fun upsertAllDatas(datas: List<CategoriesTabelle>)


    @Query("DELETE FROM CategoriesTabelle")
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
    @Query("SELECT COUNT(*) FROM CategoriesTabelle")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

}
