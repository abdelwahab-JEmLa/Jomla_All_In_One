package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
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
    @Query("DELETE FROM M09AppCompt WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: M09AppCompt)

    @Query("SELECT * FROM M09AppCompt")
    fun getAll(): List<M09AppCompt>

    @Update
    suspend fun update(data: M09AppCompt)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: M09AppCompt): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<M09AppCompt>)

    @Query("SELECT COUNT(*) FROM M09AppCompt")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M09AppCompt")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M09AppCompt")
    fun getAllFlow(): Flow<List<M09AppCompt>>

    @Query("SELECT * FROM M09AppCompt WHERE keyID = :keyId")
    fun getByKey_Flow(keyId: String): Flow<List<M09AppCompt>>

    @Query("SELECT * FROM M09AppCompt WHERE keyID = :keyId")
    suspend fun getBy_M00_Lence_Key(keyId: String = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId): M09AppCompt

    @Query("SELECT * FROM M09AppCompt WHERE keyID = :keyId")
    fun getBy_M00_Lence_Key_Flow(keyId: String = M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId): Flow<M09AppCompt>

    @Query("SELECT * FROM M09AppCompt WHERE keyID = :keyId LIMIT 1")
    fun getFlow_ByKeyID(keyId: String): Flow<M09AppCompt?>

    @Query("DELETE FROM M09AppCompt")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: M09AppCompt)
}
