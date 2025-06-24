package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_CouleurVentOperation
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
    suspend fun update(data: D_CouleurVentOperation)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: D_CouleurVentOperation): Long

    @Query("SELECT COUNT(*) FROM D_CouleurVentOperation")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM D_CouleurVentOperation")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM D_CouleurVentOperation ")
    suspend fun getAll(): MutableList<D_CouleurVentOperation>

    @Query("SELECT * FROM D_CouleurVentOperation")
    fun getAllFlow(): Flow<List<D_CouleurVentOperation>>

    @Upsert
    suspend fun upsert(data: D_CouleurVentOperation)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<D_CouleurVentOperation>)

    @Query("DELETE FROM D_CouleurVentOperation")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<D_CouleurVentOperation>)

    @Insert
    suspend fun insertData(data: D_CouleurVentOperation): Long

    @Update
    suspend fun updateData(data: D_CouleurVentOperation)

    @Delete
    suspend fun deleteData(data: D_CouleurVentOperation)

    @Upsert
    suspend fun upsertData(data: D_CouleurVentOperation)

    @Upsert
    suspend fun upsertAllDatas(datas: List<D_CouleurVentOperation>)

}
