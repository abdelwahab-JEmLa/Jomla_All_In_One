package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
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
    suspend fun update(data: FCouleurVentOperation)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: FCouleurVentOperation): Long

    @Query("SELECT COUNT(*) FROM FCouleurVentOperation")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM FCouleurVentOperation")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM FCouleurVentOperation ")
    suspend fun getAll(): MutableList<FCouleurVentOperation>

    @Query("SELECT * FROM FCouleurVentOperation")
    fun getAllFlow(): Flow<List<FCouleurVentOperation>>

    @Upsert
    suspend fun upsert(data: FCouleurVentOperation)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<FCouleurVentOperation>)

    @Query("DELETE FROM FCouleurVentOperation")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<FCouleurVentOperation>)

    @Insert
    suspend fun insertData(data: FCouleurVentOperation): Long

    @Update
    suspend fun updateData(data: FCouleurVentOperation)

    @Delete
    suspend fun deleteData(data: FCouleurVentOperation)

    @Upsert
    suspend fun upsertData(data: FCouleurVentOperation)

    @Upsert
    suspend fun upsertAllDatas(datas: List<FCouleurVentOperation>)

}
