package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _1_3_TransactionCommercial): Long

    @Query("SELECT * FROM _1_3_TransactionCommercial")
    suspend fun getAll(): MutableList<_1_3_TransactionCommercial>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_3_TransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_3_TransactionCommercial>)

    @Delete
    suspend fun delete(item: _1_3_TransactionCommercial)

    @Query("DELETE FROM _1_3_TransactionCommercial")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_3_TransactionCommercial")
    fun getCount(): Int
}
