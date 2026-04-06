package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.E1SecteurDeClients.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface E1SecteurDeClientsDao {
    @Query("SELECT * FROM E1SecteurDeClients")
    fun getAllFlow(): Flow<List<E1SecteurDeClients>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: E1SecteurDeClients): Long

    @Query("SELECT * FROM E1SecteurDeClients")
    suspend fun getAll(): MutableList<E1SecteurDeClients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: E1SecteurDeClients)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<E1SecteurDeClients>)

    @Delete
    suspend fun delete(item: E1SecteurDeClients)

    @Query("DELETE FROM E1SecteurDeClients")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM E1SecteurDeClients")
    fun getCount(): Int
}
