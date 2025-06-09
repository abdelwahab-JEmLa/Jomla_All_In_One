package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBaseProtoJuin3
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface B_ClientDataBaseDao{
    @Query("SELECT * FROM B_ClientDataBaseProtoJuin3 ")
    suspend fun getAll(): MutableList<B_ClientDataBaseProtoJuin3>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: B_ClientDataBaseProtoJuin3)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<B_ClientDataBaseProtoJuin3>)

    @Delete
    suspend fun delete(item: B_ClientDataBaseProtoJuin3)

    @Query("DELETE FROM B_ClientDataBaseProtoJuin3")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM B_ClientDataBaseProtoJuin3")
    fun getCount(): Int

}
