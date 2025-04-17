package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.VendeursActiveDonsCettePeriode
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VendeursActiveDonsCettePeriode_RoomSQlModelDao {
    //<--
    //TODO(1): ajout un collecteur  fun
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: VendeursActiveDonsCettePeriode.RoomSQlModel): Long

    @Query("SELECT * FROM RoomSQlModel")
    suspend fun getAll(): MutableList<VendeursActiveDonsCettePeriode.RoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: VendeursActiveDonsCettePeriode.RoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VendeursActiveDonsCettePeriode.RoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VendeursActiveDonsCettePeriode.RoomSQlModel>)

    @Delete
    suspend fun delete(item: VendeursActiveDonsCettePeriode.RoomSQlModel)

    @Query("DELETE FROM RoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM RoomSQlModel")
    fun getCount(): Int
}
