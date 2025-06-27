package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GmodelTransactionCommercial
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: GmodelTransactionCommercial): Long

    @Query("SELECT * FROM GmodelTransactionCommercial")
    suspend fun getAll(): MutableList<GmodelTransactionCommercial>

    @Upsert
    suspend fun upsert(data: GmodelTransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GmodelTransactionCommercial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GmodelTransactionCommercial>)

    @Delete
    suspend fun delete(item: GmodelTransactionCommercial)

    @Query("DELETE FROM GmodelTransactionCommercial")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM GmodelTransactionCommercial")
    fun getCount(): Int
}
