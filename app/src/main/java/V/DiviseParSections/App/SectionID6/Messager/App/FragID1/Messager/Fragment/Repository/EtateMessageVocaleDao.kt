package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Repository

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.EtateMessageVocale
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EtateMessageVocaleDao {

    @Query("SELECT * FROM EtateMessageVocale")
    fun getAllFlow(): Flow<List<EtateMessageVocale>>

    @Query("SELECT * FROM EtateMessageVocale")
    suspend fun getAll(): MutableList<EtateMessageVocale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: EtateMessageVocale)

    @Update
    suspend fun update(item: EtateMessageVocale)
}
