package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository.Extension

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.C.Repository.B_ClientDataBaseProtoC
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface B_ClientDataBaseDao{
    @Query("SELECT * FROM B_ClientDataBaseProtoC ")
    suspend fun getAll(): MutableList<B_ClientDataBaseProtoC>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: B_ClientDataBaseProtoC)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<B_ClientDataBaseProtoC>)

    @Delete
    suspend fun delete(item: B_ClientDataBaseProtoC)

    @Query("DELETE FROM B_ClientDataBaseProtoC")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM B_ClientDataBaseProtoC")
    fun getCount(): Int

}
