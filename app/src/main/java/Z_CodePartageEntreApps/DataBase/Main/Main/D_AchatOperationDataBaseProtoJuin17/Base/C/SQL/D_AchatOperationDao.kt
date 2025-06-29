package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface D_AchatOperationDao {
    @Update
    suspend fun update(data: FCouleurVentOperationInfos)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: FCouleurVentOperationInfos): Long

    @Query("SELECT COUNT(*) FROM FCouleurVentOperationInfos")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM FCouleurVentOperationInfos")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM FCouleurVentOperationInfos ")
    suspend fun getAll(): MutableList<FCouleurVentOperationInfos>

    @Query("SELECT * FROM FCouleurVentOperationInfos")
    fun getAllFlow(): Flow<List<FCouleurVentOperationInfos>>

    @Upsert
    suspend fun upsert(data: FCouleurVentOperationInfos)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<FCouleurVentOperationInfos>)

    @Query("DELETE FROM FCouleurVentOperationInfos")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<FCouleurVentOperationInfos>)

    @Insert
    suspend fun insertData(data: FCouleurVentOperationInfos): Long

    @Update
    suspend fun updateData(data: FCouleurVentOperationInfos)

    @Delete
    suspend fun deleteData(data: FCouleurVentOperationInfos)

    @Upsert
    suspend fun upsertData(data: FCouleurVentOperationInfos)

    @Upsert
    suspend fun upsertAllDatas(datas: List<FCouleurVentOperationInfos>)

}
