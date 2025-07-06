package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao13TarificationInfos {
    @Query("SELECT EXISTS(SELECT 1 FROM M13TarificationInfos WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M13TarificationInfos): Long

    @Query("SELECT * FROM M13TarificationInfos")
    fun getAllFlow(): Flow<List<M13TarificationInfos>>

    @Query("SELECT * FROM M13TarificationInfos WHERE id = :id")
    suspend fun getTarificationById(id: Long): M13TarificationInfos?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllReturnIDs(tarifications: List<M13TarificationInfos>): List<Long>

    @Update
    suspend fun update(tarification: M13TarificationInfos)

    @Query("DELETE FROM M13TarificationInfos")
    suspend fun deleteAll()

    // For single update_showDetailsExpanded - use IGNORE to let auto-increment work properly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M13TarificationInfos): Long

    @Update
    suspend fun updateAll(tarifications: List<M13TarificationInfos>)

    @Query("SELECT COUNT(*) FROM M13TarificationInfos")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M13TarificationInfos)
}
