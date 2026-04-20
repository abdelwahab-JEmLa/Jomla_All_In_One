package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao_M2Client {
    @Query("SELECT COUNT(*) FROM M2Client")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("DELETE FROM M2Client WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Query("SELECT * FROM M2Client ")
    suspend fun getAll(): MutableList<M2Client>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: M2Client)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<M2Client>)

    @Query("DELETE FROM M2Client")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<M2Client>)

    @Query("SELECT * FROM M2Client")
    fun getAllFlow(): Flow<List<M2Client>>

    @Update
    suspend fun updateData(data: M2Client)

    @Delete
    suspend fun deleteData(data: M2Client)

    @Query("SELECT COUNT(*) FROM M2Client")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsert(data: M2Client)

    @Upsert
    suspend fun upsertAllDatas(datas: List<M2Client>)

    @Delete
    suspend fun delete(item: M2Client)


}
