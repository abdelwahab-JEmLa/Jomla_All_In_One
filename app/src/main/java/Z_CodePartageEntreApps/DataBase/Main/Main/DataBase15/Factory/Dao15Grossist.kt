package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory

import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao15Grossist {
    @Query("DELETE FROM M15Grossist")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM M15Grossist")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M15Grossist ")
    suspend fun getAll(): MutableList<M15Grossist>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M15Grossist>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M15Grossist): Long

    @Query("SELECT * FROM M15Grossist")
    fun getAllFlow(): Flow<List<M15Grossist>>

    @Update
    suspend fun update(tarification: M15Grossist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M15Grossist): Long

    @Query("SELECT COUNT(*) FROM M15Grossist")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M15Grossist)
}
