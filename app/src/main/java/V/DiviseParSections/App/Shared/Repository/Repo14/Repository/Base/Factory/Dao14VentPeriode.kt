package V.DiviseParSections.App.Shared.Repository.Repo14.Repository.Base.Factory

import V.DiviseParSections.App.Shared.Repository.Repo14.Repository.M14VentPeriode
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao14VentPeriode {
    @Query("DELETE FROM M14VentPeriode")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM M14VentPeriode")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M14VentPeriode ")
    suspend fun getAll(): MutableList<M14VentPeriode>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M14VentPeriode>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tarificationInfos: M14VentPeriode): Long

    @Query("SELECT * FROM M14VentPeriode")
    fun getAllFlow(): Flow<List<M14VentPeriode>>

    @Update
    suspend fun update(tarification: M14VentPeriode)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: M14VentPeriode): Long

    @Query("SELECT COUNT(*) FROM M14VentPeriode")
    suspend fun getCount(): Int

    @Delete
    fun delete(data: M14VentPeriode)
}
