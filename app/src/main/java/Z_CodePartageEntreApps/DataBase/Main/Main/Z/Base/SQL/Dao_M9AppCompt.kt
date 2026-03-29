package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Z_AppCompt
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao_M9AppCompt {
    @Query("DELETE FROM Z_AppCompt WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: Z_AppCompt)

    @Query("SELECT * FROM Z_AppCompt")
    fun getAll(): List<Z_AppCompt>

    @Update
    suspend fun update(data: Z_AppCompt)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: Z_AppCompt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Z_AppCompt>)

    @Query("SELECT COUNT(*) FROM Z_AppCompt")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM Z_AppCompt")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM Z_AppCompt")
    fun getAllFlow(): Flow<List<Z_AppCompt>>

    @Query("SELECT * FROM Z_AppCompt WHERE keyID = :keyId")
    fun getByKey_Flow(keyId: String): Flow<List<Z_AppCompt>>

    @Query("SELECT * FROM Z_AppCompt WHERE keyID = :keyId")
    fun getBy_M00_Lence_Key_Flow(keyId: String = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId): Flow<List<Z_AppCompt>>

    @Query("SELECT * FROM Z_AppCompt WHERE keyID = :keyId LIMIT 1")
    fun getFlow_ByKeyID(keyId: String): Flow<Z_AppCompt?>

    @Query("DELETE FROM Z_AppCompt")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: Z_AppCompt)
}
