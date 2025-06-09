package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.F

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface B_ClientInfosDao {
    @Query("SELECT * FROM B_ClientInfos")
    fun getAllClients(): Flow<List<B_ClientInfos>>

    @Query("SELECT * FROM B_ClientInfos")
    suspend fun getAllClientsSync(): List<B_ClientInfos>

    @Query("SELECT * FROM B_ClientInfos WHERE id = :id")
    suspend fun getClientById(id: Long): B_ClientInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: B_ClientInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<B_ClientInfos>)

    @Update
    suspend fun update(client: B_ClientInfos)

    @Query("DELETE FROM B_ClientInfos")
    suspend fun deleteAll()
}
