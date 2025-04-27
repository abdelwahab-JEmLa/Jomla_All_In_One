package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SecteurDeClientsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: SecteurDeClients): Long

    @Query("SELECT * FROM SecteurDeClients")
    suspend fun getAll(): MutableList<SecteurDeClients>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SecteurDeClients)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SecteurDeClients>)

    @Delete
    suspend fun delete(item: SecteurDeClients)

    @Query("DELETE FROM SecteurDeClients")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM SecteurDeClients")
    fun getCount(): Int
}

@Dao
interface PolygonGeoLimiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: PolygonGeoLimite): Long

    @Query("SELECT * FROM PolygonGeoLimite")
    suspend fun getAll(): MutableList<PolygonGeoLimite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PolygonGeoLimite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PolygonGeoLimite>)

    @Delete
    suspend fun delete(item: PolygonGeoLimite)

    @Query("DELETE FROM PolygonGeoLimite")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM PolygonGeoLimite")
    fun getCount(): Int
}
