package Z_CodePartageEntreApps.DataBase.Juin3.Proto.Z_App.Base.Extension.DataBase

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_5_VendeurDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: Z_AppCompt): Long

    @Query("SELECT * FROM Z_AppCompt")
    suspend fun getAll(): MutableList<Z_AppCompt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Z_AppCompt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Z_AppCompt>)

    @Delete
    suspend fun delete(item: Z_AppCompt)

    @Query("DELETE FROM Z_AppCompt")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM Z_AppCompt")
    fun getCount(): Int
}
