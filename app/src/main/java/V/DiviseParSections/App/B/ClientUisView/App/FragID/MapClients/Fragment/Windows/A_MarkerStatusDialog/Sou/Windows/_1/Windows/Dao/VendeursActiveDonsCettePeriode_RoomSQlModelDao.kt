package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.VendeursActiveDonsCettePeriode
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VendeursActiveDonsCettePeriode_RoomSQlModelDao {
    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getAllAsFlow(): Flow<List<VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getAll(): MutableList<VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel>)

    @Delete
    suspend fun delete(item: VendeursActiveDonsCettePeriode.VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Query("DELETE FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getCount(): Int
}
