package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Dao

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.ProduitsVenduParLuiRoomSQlModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitsVenduParLui_RoomSQlModelDao {
    @Query("SELECT * FROM ProduitsVenduParLuiRoomSQlModel")
    fun getAllAsFlow(): Flow<List<ProduitsVenduParLuiRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: ProduitsVenduParLuiRoomSQlModel): Long

    @Query("SELECT * FROM ProduitsVenduParLuiRoomSQlModel")
    suspend fun getAll(): MutableList<ProduitsVenduParLuiRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: ProduitsVenduParLuiRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProduitsVenduParLuiRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProduitsVenduParLuiRoomSQlModel>)

    @Delete
    suspend fun delete(item: ProduitsVenduParLuiRoomSQlModel)

    @Query("DELETE FROM ProduitsVenduParLuiRoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM ProduitsVenduParLuiRoomSQlModel")
    fun getCount(): Int
}
