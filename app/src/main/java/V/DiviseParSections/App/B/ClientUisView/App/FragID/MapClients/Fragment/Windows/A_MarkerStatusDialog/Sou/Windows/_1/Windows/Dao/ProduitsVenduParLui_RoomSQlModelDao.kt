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
    @Query("SELECT * FROM RoomSQlModel")
    fun getAllAsFlow(): Flow<List<ProduitsVenduParLui.RoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: ProduitsVenduParLui.RoomSQlModel): Long

    @Query("SELECT * FROM RoomSQlModel")
    suspend fun getAll(): MutableList<ProduitsVenduParLui.RoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: ProduitsVenduParLui.RoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProduitsVenduParLui.RoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProduitsVenduParLui.RoomSQlModel>)

    @Delete
    suspend fun delete(item: ProduitsVenduParLui.RoomSQlModel)

    @Query("DELETE FROM RoomSQlModel")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM RoomSQlModel")
    fun getCount(): Int
}
