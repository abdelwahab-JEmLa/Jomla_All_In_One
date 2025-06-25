package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.A2_Passive.C3_TransactionCommercial
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: C3_TransactionCommercial): Long

    @Query("SELECT * FROM C3_TransactionCommercial")
    suspend fun getAll(): MutableList<C3_TransactionCommercial>

    @Upsert
    suspend fun upsert(data: C3_TransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: C3_TransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<C3_TransactionCommercial>)

    @Delete
    suspend fun delete(item: C3_TransactionCommercial)

    @Query("DELETE FROM C3_TransactionCommercial")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM C3_TransactionCommercial")
    fun getCount(): Int
}
