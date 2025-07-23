package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.SQL

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GBonVentDao {
    @Query("DELETE FROM M8BonVent WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: M8BonVent)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M8BonVent>)

    @Delete
    suspend fun delete(data: M8BonVent)

    @Update
    suspend fun update(data: M8BonVent)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: M8BonVent): Long

    @Query("SELECT COUNT(*) FROM M8BonVent")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M8BonVent")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M8BonVent ")
    suspend fun getAll(): MutableList<M8BonVent>

    @Query("SELECT * FROM M8BonVent")
    fun getAllFlow(): Flow<List<M8BonVent>>

    @Query("DELETE FROM M8BonVent")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: M8BonVent)

}
