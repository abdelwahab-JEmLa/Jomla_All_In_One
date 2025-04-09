package Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase.Extension.DataBase

import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase._2_2_ClientsDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _2_2_ClientsDataBaseDao {
    @Query("SELECT * FROM _2_2_ClientsDataBase")
    suspend fun getAll(): MutableList<_2_2_ClientsDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _2_2_ClientsDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_2_2_ClientsDataBase>)

    @Delete
    suspend fun delete(item: _2_2_ClientsDataBase)

    @Query("DELETE FROM _2_2_ClientsDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _2_2_ClientsDataBase")
    fun getCount(): Int
}
