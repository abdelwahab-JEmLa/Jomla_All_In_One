package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MVentPeriodeDao {
    @Query("SELECT * FROM MVentPeriode")
    fun getAllFlow(): Flow<List<MVentPeriode>>

    @Update
    suspend fun update(data: MVentPeriode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: MVentPeriode)

    @Delete
    suspend fun delete(article: MVentPeriode)

    @Query("SELECT * FROM MVentPeriode ")
    suspend fun getAll(): MutableList<MVentPeriode>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<MVentPeriode>)

    @Query("DELETE FROM MVentPeriode")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM MVentPeriode")
    suspend fun getCount(): Int

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM MVentPeriode")
    suspend fun isTableEmpty(): Boolean

}
