package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BProto_ClientsDataBaselDao{
    @Query("SELECT * FROM BProto_ClientsDataBase ")
    suspend fun getAll(): MutableList<BProto_ClientsDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BProto_ClientsDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BProto_ClientsDataBase>)

    @Delete
    suspend fun delete(item: BProto_ClientsDataBase)

    @Query("DELETE FROM BProto_ClientsDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM BProto_ClientsDataBase")
    fun getCount(): Int

}
