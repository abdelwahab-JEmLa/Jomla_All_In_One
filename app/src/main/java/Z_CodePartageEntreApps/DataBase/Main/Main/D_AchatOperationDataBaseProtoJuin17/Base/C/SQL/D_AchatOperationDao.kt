package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
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
    suspend fun update(data: D_AchatOperation)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: D_AchatOperation): Long

    @Query("SELECT COUNT(*) FROM D_AchatOperation")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM D_AchatOperation")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM D_AchatOperation ")
    suspend fun getAll(): MutableList<D_AchatOperation>

    @Query("SELECT * FROM D_AchatOperation")
    fun getAllFlow(): Flow<List<D_AchatOperation>>

    @Upsert
    suspend fun upsert(data: D_AchatOperation)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<D_AchatOperation>)

    @Query("DELETE FROM D_AchatOperation")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<D_AchatOperation>)

    @Insert
    suspend fun insertData(data: D_AchatOperation): Long

    @Update
    suspend fun updateData(data: D_AchatOperation)

    @Delete
    suspend fun deleteData(data: D_AchatOperation)

    @Upsert
    suspend fun upsertData(data: D_AchatOperation)

    @Upsert
    suspend fun upsertAllDatas(datas: List<D_AchatOperation>)

}
