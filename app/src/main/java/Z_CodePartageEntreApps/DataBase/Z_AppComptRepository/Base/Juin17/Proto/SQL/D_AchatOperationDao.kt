package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.SQL

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.Z_AppCompt
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface Z_AppComptDao {
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

    @Query("DELETE FROM Z_AppCompt")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: Z_AppCompt)

}
