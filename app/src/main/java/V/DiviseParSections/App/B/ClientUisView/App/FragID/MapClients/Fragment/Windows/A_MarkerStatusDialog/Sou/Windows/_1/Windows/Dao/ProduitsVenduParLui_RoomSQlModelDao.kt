package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.ProduitsVenduParLui
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitsVenduParLui_RoomSQlModelDao {
    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getAllAsFlow(): Flow<List<ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel): Long

    @Query("SELECT * FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun getAll(): MutableList<ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel>)

    @Delete
    suspend fun delete(item: ProduitsVenduParLui.ProduitsVenduParLuiRoomSQlModel)

    @Query("DELETE FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM VendeursActiveDonsCettePeriodeRoomSQlModel")
    fun getCount(): Int
}
