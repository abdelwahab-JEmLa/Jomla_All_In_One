package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.VendeursActiveDonsCettePeriodeRoomSQlModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VendeursActiveDonsCettePeriode_RoomSQlModelDao {
    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getAllAsFlow(): Flow<List<VendeursActiveDonsCettePeriodeRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getAll(): MutableList<VendeursActiveDonsCettePeriodeRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VendeursActiveDonsCettePeriodeRoomSQlModel>)

    @Delete
    suspend fun delete(item: VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Query("DELETE FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getCount(): Int
}
