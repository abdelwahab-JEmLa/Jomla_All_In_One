package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.H.Dao

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface M17MessageVocaleDao {
    @Query("DELETE FROM M17MessageVocale WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Query("SELECT * FROM M17MessageVocale ")
    suspend fun getAll(): MutableList<M17MessageVocale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: M17MessageVocale)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<M17MessageVocale>)

    @Query("DELETE FROM M17MessageVocale")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<M17MessageVocale>)

    @Query("SELECT * FROM M17MessageVocale")
    fun getAllFlow(): Flow<List<M17MessageVocale>>

    @Update
    suspend fun updateData(data: M17MessageVocale)

    @Delete
    suspend fun deleteData(data: M17MessageVocale)

    @Query("SELECT COUNT(*) FROM M17MessageVocale")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsert(data: M17MessageVocale)

    @Upsert
    suspend fun upsertAllDatas(datas: List<M17MessageVocale>)
}
