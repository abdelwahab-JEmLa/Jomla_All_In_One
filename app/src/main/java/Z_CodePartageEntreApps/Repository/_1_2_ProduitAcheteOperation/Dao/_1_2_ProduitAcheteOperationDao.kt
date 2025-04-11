package Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao

import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_2_ProduitAcheteOperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _1_2_ProduitAcheteOperation): Long

    @Query("SELECT * FROM _1_2_ProduitAcheteOperation")
    suspend fun getAll(): MutableList<_1_2_ProduitAcheteOperation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: _1_2_ProduitAcheteOperation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_2_ProduitAcheteOperation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_2_ProduitAcheteOperation>)

    @Delete
    suspend fun delete(item: _1_2_ProduitAcheteOperation)

    @Query("DELETE FROM _1_2_ProduitAcheteOperation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_2_ProduitAcheteOperation")
    fun getCount(): Int
}
