package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.HClientInfos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface B_ClientInfosProtoJuin3Dao {
    @Query("SELECT * FROM HClientInfos ")
    suspend fun getAll(): MutableList<HClientInfos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: HClientInfos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<HClientInfos>)

    @Query("DELETE FROM HClientInfos")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<HClientInfos>)

    @Query("SELECT * FROM HClientInfos")
    fun getAllFlow(): Flow<List<HClientInfos>>

    @Update
    suspend fun updateData(data: HClientInfos)

    @Delete
    suspend fun deleteData(data: HClientInfos)

    @Query("SELECT COUNT(*) FROM HClientInfos")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsert(data: HClientInfos)

    @Upsert
    suspend fun upsertAllDatas(datas: List<HClientInfos>)

    @Delete
    suspend fun delete(item: HClientInfos)


}
