package Z_CodePartageEntreApps.Repository._1_3_BonAchat

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views.Models._1_3_BonAchat
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_3_BonAchatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _1_3_BonAchat): Long

    @Query("SELECT * FROM _1_3_BonAchat")
    suspend fun getAll(): MutableList<_1_3_BonAchat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_3_BonAchat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_3_BonAchat>)

    @Delete
    suspend fun delete(item: _1_3_BonAchat)

    @Query("DELETE FROM _1_3_BonAchat")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_3_BonAchat")
    fun getCount(): Int
}
