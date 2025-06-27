package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: TransactionVent): Long

    @Query("SELECT * FROM TransactionVent")
    suspend fun getAll(): MutableList<TransactionVent>

    @Upsert
    suspend fun upsert(data: TransactionVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TransactionVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TransactionVent>)

    @Delete
    suspend fun delete(item: TransactionVent)

    @Query("DELETE FROM TransactionVent")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM TransactionVent")
    fun getCount(): Int
}
