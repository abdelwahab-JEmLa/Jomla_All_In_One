package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface B_ClientDataBaseDao{
    @Query("SELECT * FROM B_ClientInfos ")
    suspend fun getAll(): MutableList<B_ClientDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: B_ClientDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<B_ClientDataBase>)

    @Delete
    suspend fun delete(item: B_ClientDataBase)

    @Query("DELETE FROM B_ClientInfos")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM B_ClientInfos")
    fun getCount(): Int

}
