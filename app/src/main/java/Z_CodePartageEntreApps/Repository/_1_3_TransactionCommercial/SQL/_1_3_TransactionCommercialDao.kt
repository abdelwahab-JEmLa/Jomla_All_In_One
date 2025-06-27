package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionCommercial
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: TransactionCommercial): Long

    @Query("SELECT * FROM TransactionCommercial")
    suspend fun getAll(): MutableList<TransactionCommercial>

    @Upsert
    suspend fun upsert(data: TransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TransactionCommercial>)

    @Delete
    suspend fun delete(item: TransactionCommercial)

    @Query("DELETE FROM TransactionCommercial")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM TransactionCommercial")
    fun getCount(): Int
}
