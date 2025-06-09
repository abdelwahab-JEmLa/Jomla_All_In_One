package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.E._3_ClientsDataBase.Extension.DataBase

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.E._3_ClientsDataBase._3_ClientsDataBaseProtoE
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _3_ClientsDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAndReturnVids(items: List<_3_ClientsDataBaseProtoE>): List<Long>

    @Query("SELECT * FROM _3_ClientsDataBaseProtoE")
    suspend fun getAll(): MutableList<_3_ClientsDataBaseProtoE>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _3_ClientsDataBaseProtoE)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_3_ClientsDataBaseProtoE>)

    @Delete
    suspend fun delete(item: _3_ClientsDataBaseProtoE)

    @Query("DELETE FROM _3_ClientsDataBaseProtoE")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _3_ClientsDataBaseProtoE")
    fun getCount(): Int
}
