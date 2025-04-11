package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.DataBase

import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _2_1_ProduitsDataBaseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _2_1_ProduitsDataBase): Long

    @Query("SELECT * FROM _2_1_ProduitsDataBase")
    suspend fun getAll(): MutableList<_2_1_ProduitsDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _2_1_ProduitsDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_2_1_ProduitsDataBase>)

    @Delete
    suspend fun delete(item: _2_1_ProduitsDataBase)

    @Query("DELETE FROM _2_1_ProduitsDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _2_1_ProduitsDataBase")
    fun getCount(): Int
}
