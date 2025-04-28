package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Repository

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.MessageVocale
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageVocaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEtReturnSonNewVid(item: MessageVocale): Long

    @Query("SELECT COUNT(*) FROM MessageVocale")
    fun getCount(): Int

    @Query("SELECT MAX(vid) FROM MessageVocale")
    suspend fun getMaxVid(): Long

    @Query("SELECT * FROM MessageVocale")
    fun getAllFlow(): Flow<List<MessageVocale>>

    @Query("SELECT * FROM MessageVocale")
    suspend fun getAll(): MutableList<MessageVocale>

    @Update
    suspend fun update(item: List<MessageVocale>)
}
