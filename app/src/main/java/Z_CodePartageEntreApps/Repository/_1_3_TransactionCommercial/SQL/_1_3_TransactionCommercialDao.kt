package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GTransactionVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: GTransactionVent): Long

    @Query("SELECT * FROM GTransactionVent")
    suspend fun getAll(): MutableList<GTransactionVent>

    @Upsert
    suspend fun upsert(data: GTransactionVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GTransactionVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GTransactionVent>)

    @Delete
    suspend fun delete(item: GTransactionVent)

    @Query("DELETE FROM GTransactionVent")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM GTransactionVent")
    fun getCount(): Int
}
