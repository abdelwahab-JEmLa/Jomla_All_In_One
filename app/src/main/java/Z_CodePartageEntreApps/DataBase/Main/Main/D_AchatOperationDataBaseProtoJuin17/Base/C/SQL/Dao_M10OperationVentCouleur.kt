package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao_M10OperationVentCouleur {
    @Delete
    suspend fun delete(data: M10OperationVentCouleur)

    @Update
    suspend fun update(data: M10OperationVentCouleur)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: M10OperationVentCouleur): Long

    @Query("SELECT COUNT(*) FROM M10OperationVentCouleur")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M10OperationVentCouleur")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M10OperationVentCouleur")
    suspend fun getAll(): MutableList<M10OperationVentCouleur>

    @Query("SELECT * FROM M10OperationVentCouleur WHERE parent_M8BonVent_KeyId = :bonVentKey")
    fun getFlow_ListM10OperationVentCouleur_Of_Active_M8Bon_Key(
        bonVentKey: String
    ): Flow<List<M10OperationVentCouleur>>

    @Query("SELECT * FROM M10OperationVentCouleur")
    fun getAllFlow(): Flow<List<M10OperationVentCouleur>>

    @Upsert
    suspend fun upsert(data: M10OperationVentCouleur)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<M10OperationVentCouleur>)

    @Query("DELETE FROM M10OperationVentCouleur")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<M10OperationVentCouleur>)

    @Insert
    suspend fun insertData(data: M10OperationVentCouleur): Long

    @Update
    suspend fun updateData(data: M10OperationVentCouleur)

    @Delete
    suspend fun deleteData(data: M10OperationVentCouleur)

    @Upsert
    suspend fun upsertData(data: M10OperationVentCouleur)

    @Upsert
    suspend fun upsertAllDatas(datas: List<M10OperationVentCouleur>)
}
