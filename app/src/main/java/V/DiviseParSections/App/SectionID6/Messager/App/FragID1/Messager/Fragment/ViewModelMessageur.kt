package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ViewModelMessageur(
    val appDatabase: AppDatabase,
) : ViewModel() {
    private val _MessageVocale = MutableStateFlow(NoSqlMessageVocale())
    val messageVocale: StateFlow<NoSqlMessageVocale> = _MessageVocale.asStateFlow()

    val storageRef = FirebaseStorage.getInstance().reference

    init {
        // Collect messages and their states from the database
        viewModelScope.launch {
            // Collect MessageVocale entities
            appDatabase.messageVocaleDao().getAllFlow().collectLatest { messagesList ->
                // Collect EtateMessageVocale entities
                appDatabase.etateMessageVocaleDao().getAllFlow().collectLatest { etatesList ->
                    // Group EtateMessageVocale by parent message using forEach
                    val etatesKeysByParent = mutableMapOf<Long, MutableList<String>>()

                    etatesList.forEach { etate ->
                        if (!etatesKeysByParent.containsKey(etate.parentMessageVID)) {
                            etatesKeysByParent[etate.parentMessageVID] = mutableListOf()
                        }
                        etatesKeysByParent[etate.parentMessageVID]?.add(etate.fireBaseKeyID)
                    }

                    // Get the first message or create empty one if list is empty
                    val firstMessage = messagesList.firstOrNull() ?: return@collectLatest

                    // Update the StateFlow with collected data
                    _MessageVocale.value = NoSqlMessageVocale(
                        keyIDMessageVocale = firstMessage.fireBaseKeyID,
                        keyIDsChildListEtateMessageVocale = etatesKeysByParent[firstMessage.vid] ?: listOf()
                    )
                }
            }
        }
    }
}

data class NoSqlMessageVocale(
    val keyIDMessageVocale: String = "MessageVocale.fireBaseKeyID",
    val keyIDsChildListEtateMessageVocale: List<String> = listOf(),
)

@Entity
data class MessageVocale(
    @PrimaryKey val vid: Long,

    //Infos De Base
    val vocaleKeyID: String = "",
    val nomClientConcerned: String = ""

    //Etates Mutable

) {
    val fireBaseKeyID: String
        get() {
            val parent = "()"
            val thisVal = "$vid->(${vocaleKeyID}_($nomClientConcerned))"
            return "$parent$thisVal"
        }
}

@Entity
data class EtateMessageVocale(
    @PrimaryKey val vid: Long,

    //Forging Keys
    val parentMessageVID: Long = 0,
    val parentMessageKeyID: String =
        "SecteurDeClients.vid(SecteurDeClients.nom)",

    //Infos De Base
    var nom: Nom = Nom.CREE,
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),

    //Etates Mutable

) {
    val fireBaseKeyID: String
        get() {
            val parent = "($parentMessageVID)->"
            val thisVal = "($vid)->(${nom}_($timestamps))"

            return "$parent$thisVal"
        }

    enum class Nom(val nomArabe: String? = null) {
        CREE,
        VUE,
        ECOUTE,
    }
}

@Dao
interface MessageVocaleDao {
    @Query("SELECT * FROM MessageVocale")
    fun getAllFlow(): Flow<List<MessageVocale>>

    @Query("SELECT * FROM MessageVocale")
    suspend fun getAll(): MutableList<MessageVocale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: MessageVocale)

    @Update
    suspend fun update(item: List<MessageVocale>)
}

@Dao
interface EtateMessageVocaleDao {
    @Query("SELECT * FROM EtateMessageVocale")
    fun getAllFlow(): Flow<List<EtateMessageVocale>>

    @Query("SELECT * FROM EtateMessageVocale")
    suspend fun getAll(): MutableList<EtateMessageVocale>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: EtateMessageVocale)

    @Update
    suspend fun update(item: List<EtateMessageVocale>)
}
