package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_4_PeriodeVentDao {
    @Query("SELECT * FROM _1_4_PeriodeVent")
    suspend fun getAll(): MutableList<_1_4_PeriodeVent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_4_PeriodeVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _1_4_PeriodeVent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_4_PeriodeVent>)

    @Delete
    suspend fun delete(item: _1_4_PeriodeVent)

    @Query("DELETE FROM _1_4_PeriodeVent")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_4_PeriodeVent")
    fun getCount(): Int
}
