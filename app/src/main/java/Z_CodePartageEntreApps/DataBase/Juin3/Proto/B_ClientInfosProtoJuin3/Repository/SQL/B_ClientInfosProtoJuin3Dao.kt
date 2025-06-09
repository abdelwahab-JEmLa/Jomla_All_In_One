package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface B_ClientInfosProtoJuin3Dao {
    @Query("SELECT * FROM B_ClientInfosProtoJuin3 ")
    suspend fun getAll(): MutableList<B_ClientInfosProtoJuin3>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: B_ClientInfosProtoJuin3)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<B_ClientInfosProtoJuin3>)

    @Query("DELETE FROM B_ClientInfosProtoJuin3")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<B_ClientInfosProtoJuin3>)

    @Query("SELECT * FROM B_ClientInfosProtoJuin3")
    fun getAllFlow(): Flow<List<B_ClientInfosProtoJuin3>>

    @Update
    suspend fun updateData(data: B_ClientInfosProtoJuin3)

    @Delete
    suspend fun deleteData(data: B_ClientInfosProtoJuin3)

    @Query("SELECT COUNT(*) FROM B_ClientInfosProtoJuin3")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsert(data: B_ClientInfosProtoJuin3)

    @Upsert
    suspend fun upsertAllDatas(datas: List<B_ClientInfosProtoJuin3>)

    @Delete
    suspend fun delete(item: B_ClientInfosProtoJuin3)


}
