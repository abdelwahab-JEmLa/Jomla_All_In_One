package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Models.C3_BonAchate
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: C3_BonAchate): Long

    @Query("SELECT * FROM C3_BonAchate")
    suspend fun getAll(): MutableList<C3_BonAchate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: C3_BonAchate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<C3_BonAchate>)

    @Delete
    suspend fun delete(item: C3_BonAchate)

    @Query("DELETE FROM C3_BonAchate")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM C3_BonAchate")
    fun getCount(): Int
}
