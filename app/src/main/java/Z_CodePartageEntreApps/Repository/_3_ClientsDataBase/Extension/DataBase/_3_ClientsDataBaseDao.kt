package Z_CodePartageEntreApps.Repository._3_ClientsDataBase.Extension.DataBase

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views.Models._3_ClientsDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _3_ClientsDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAndReturnVids(items: List<_3_ClientsDataBase>): List<Long>

    @Query("SELECT * FROM _3_ClientsDataBase")
    suspend fun getAll(): MutableList<_3_ClientsDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _3_ClientsDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_3_ClientsDataBase>)

    @Delete
    suspend fun delete(item: _3_ClientsDataBase)

    @Query("DELETE FROM _3_ClientsDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _3_ClientsDataBase")
    fun getCount(): Int
}
