package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface D_TarificationInfosDao {
    @Query("SELECT * FROM D_TarificationInfos")
    fun getAllTarifications(): Flow<List<D_TarificationInfos>>

    @Query("SELECT * FROM D_TarificationInfos WHERE id = :id")
    suspend fun getTarificationById(id: Long): D_TarificationInfos?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllReturnIDs(tarifications: List<D_TarificationInfos>) : List<Long>

    @Update
    suspend fun update(tarification: D_TarificationInfos)

    @Query("DELETE FROM D_TarificationInfos")
    suspend fun deleteAll()

    // For single insert - use IGNORE to let auto-increment work properly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tarification: D_TarificationInfos): Long

    @Update
    suspend fun updateAll(tarifications: List<D_TarificationInfos>)

    @Query("SELECT COUNT(*) FROM D_TarificationInfos")
    suspend fun getCount(): Int
}
