package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.H.Dao

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface D_EtateMessageVocaleDao {
    @Query("SELECT * FROM D_EtateMessageVocale ")
    suspend fun getAll(): MutableList<D_EtateMessageVocale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: D_EtateMessageVocale)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<D_EtateMessageVocale>)

    @Query("DELETE FROM D_EtateMessageVocale")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<D_EtateMessageVocale>)

    @Query("SELECT * FROM D_EtateMessageVocale")
    fun getAllFlow(): Flow<List<D_EtateMessageVocale>>

    @Update
    suspend fun updateData(data: D_EtateMessageVocale)

    @Delete
    suspend fun deleteData(data: D_EtateMessageVocale)

    @Query("SELECT COUNT(*) FROM D_EtateMessageVocale")
    suspend fun getCount(): Int

    @Upsert
    suspend fun upsert(data: D_EtateMessageVocale)

    @Upsert
    suspend fun upsertAllDatas(datas: List<D_EtateMessageVocale>)
}
