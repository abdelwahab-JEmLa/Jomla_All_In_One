package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GBonVent
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface _1_3_TransactionCommercialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: GBonVent): Long

    @Query("SELECT * FROM GBonVent")
    suspend fun getAll(): MutableList<GBonVent>

    @Upsert
    suspend fun upsert(data: GBonVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GBonVent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GBonVent>)

    @Delete
    suspend fun delete(item: GBonVent)

    @Query("DELETE FROM GBonVent")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM GBonVent")
    fun getCount(): Int
}
