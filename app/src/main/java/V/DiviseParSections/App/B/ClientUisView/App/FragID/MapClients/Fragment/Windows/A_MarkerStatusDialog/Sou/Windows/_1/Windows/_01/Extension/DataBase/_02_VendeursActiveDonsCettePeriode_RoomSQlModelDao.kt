package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01.Extension.DataBase
   /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._02_VendeursActiveDonsCettePeriodeRoomSQlModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface _02_VendeursActiveDonsCettePeriode_RoomSQlModelDao {
    @Query("SELECT * FROM _02_VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getAllAsFlow(): Flow<List<_02_VendeursActiveDonsCettePeriodeRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecReturnNewVid(item: _02_VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Query("SELECT * FROM _02_VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getAll(): MutableList<_02_VendeursActiveDonsCettePeriodeRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(produitAcheteOperation: _02_VendeursActiveDonsCettePeriodeRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEtReturnSonNewVid(item: _02_VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_02_VendeursActiveDonsCettePeriodeRoomSQlModel>)

    @Delete
    suspend fun delete(item: _02_VendeursActiveDonsCettePeriodeRoomSQlModel)

    @Query("DELETE FROM _02_VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _02_VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getCount(): Int
}
                                                        */
