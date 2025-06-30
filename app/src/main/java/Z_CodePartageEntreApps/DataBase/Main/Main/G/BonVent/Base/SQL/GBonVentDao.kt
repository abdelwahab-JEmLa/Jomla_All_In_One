package Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.SQL

import V.DiviseParSections.App.Shared.Repository.GBonVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GBonVentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<GBonVent>)

    @Delete
    suspend fun delete(data: GBonVent)

    @Update
    suspend fun update(data: GBonVent)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: GBonVent): Long

    @Query("SELECT COUNT(*) FROM GBonVent")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM GBonVent")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM GBonVent ")
    suspend fun getAll(): MutableList<GBonVent>

    @Query("SELECT * FROM GBonVent")
    fun getAllFlow(): Flow<List<GBonVent>>

    @Query("DELETE FROM GBonVent")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: GBonVent)

}
