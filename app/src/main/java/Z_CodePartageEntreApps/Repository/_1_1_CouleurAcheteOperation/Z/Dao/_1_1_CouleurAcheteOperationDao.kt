package Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation.Z.Dao

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface _1_1_CouleurAcheteOperationDao {
    @Query("SELECT * FROM _1_1_CouleurAcheteOperation")
    suspend fun getAll(): MutableList<_1_1_CouleurAcheteOperation>

    @Query("SELECT * FROM _1_1_CouleurAcheteOperation")
    fun getAllFlow(): Flow<List<_1_1_CouleurAcheteOperation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_1_CouleurAcheteOperation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_1_CouleurAcheteOperation>)

    @Delete
    suspend fun delete(item: _1_1_CouleurAcheteOperation)

    @Query("DELETE FROM _1_1_CouleurAcheteOperation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_1_CouleurAcheteOperation")
    fun getCount(): Int
}
