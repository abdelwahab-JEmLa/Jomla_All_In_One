package Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase.Extension.DataBase

import Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase._2_2_CouleursDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _2_2_CouleursDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAndReturnVids(items: List<_2_2_CouleursDataBase>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _2_2_CouleursDataBase): Long

    @Query("SELECT * FROM _2_2_CouleursDataBase")
    suspend fun getAll(): MutableList<_2_2_CouleursDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _2_2_CouleursDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_2_2_CouleursDataBase>)

    @Delete
    suspend fun delete(item: _2_2_CouleursDataBase)

    @Query("DELETE FROM _2_2_CouleursDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _2_2_CouleursDataBase")
    fun getCount(): Int
}
