package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory

import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao11AchatOperation {
    @Query("DELETE FROM M11AchatOperation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM M11AchatOperation")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M11AchatOperation ")
    suspend fun getAll(): MutableList<M11AchatOperation>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M11AchatOperation>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M11AchatOperation): Long

    @Query("SELECT * FROM M11AchatOperation")
    fun getAllFlow(): Flow<List<M11AchatOperation>>

    @Update
    suspend fun update(tarification: M11AchatOperation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M11AchatOperation): Long

    @Query("SELECT COUNT(*) FROM M11AchatOperation")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M11AchatOperation)
}
