package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01.Extension.DataBase

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._03_ProduitsVenduParLuiRoomSQlModel
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface _03_ProduitsVenduParLui_RoomSQlModelDao {
    @Query("SELECT * FROM _03_ProduitsVenduParLuiRoomSQlModel")
    fun getAllAsFlow(): Flow<List<_03_ProduitsVenduParLuiRoomSQlModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _03_ProduitsVenduParLuiRoomSQlModel): Long

    @Query("SELECT * FROM _03_ProduitsVenduParLuiRoomSQlModel")
    suspend fun getAll(): MutableList<_03_ProduitsVenduParLuiRoomSQlModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(produitAcheteOperation: _03_ProduitsVenduParLuiRoomSQlModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _03_ProduitsVenduParLuiRoomSQlModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_03_ProduitsVenduParLuiRoomSQlModel>)

    @Delete
    suspend fun delete(item: _03_ProduitsVenduParLuiRoomSQlModel)

    @Query("DELETE FROM _03_ProduitsVenduParLuiRoomSQlModel")
    suspend fun deleteAll()

    // In _03_ProduitsVenduParLui_RoomSQlModelDao.kt
    @Query("SELECT COUNT(*) FROM _03_ProduitsVenduParLuiRoomSQlModel")
    suspend fun getCount(): Int
}
