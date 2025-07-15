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
    @Query("DELETE FROM M13TarificationInfos")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM M13TarificationInfos")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M13TarificationInfos ")
    suspend fun getAll(): MutableList<M13TarificationInfos>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M13TarificationInfos>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M13TarificationInfos): Long

    @Query("SELECT * FROM M13TarificationInfos")
    fun getAllFlow(): Flow<List<M13TarificationInfos>>


    @Update
    suspend fun update(tarification: M13TarificationInfos)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M13TarificationInfos): Long

    @Query("SELECT COUNT(*) FROM M13TarificationInfos")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M13TarificationInfos)
}
