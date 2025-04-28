package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

data class MessageurUiState(
    val noSqlMessageVocaleList: List<NoSqlMessageVocale> = emptyList(),
    val messageVocaleList: List<MessageVocale> = emptyList(),
    val etateMessageVocaleList: List<EtateMessageVocale> = emptyList(),
)

class ViewModelMessageur(
    val appDatabase: AppDatabase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageurUiState())
    val uiState: StateFlow<MessageurUiState> = _uiState.asStateFlow()


    init {
        viewModelScope.launch(Dispatchers.IO) {  // Use IO dispatcher for database operations
            false.addTest()
            setupDataCollection()
        }
    }

    private suspend fun Boolean.addTest() {
        // Check count in a coroutine with the right dispatcher
        val messageCount = appDatabase.messageVocaleDao().getCount()

        if (messageCount == 0 && this) {
            val messageVocale = MessageVocale.createTestInstance()
            appDatabase.messageVocaleDao().upsertEtReturnSonNewVid(messageVocale)

            appDatabase.etateMessageVocaleDao().insert(
                EtateMessageVocale.createTestInstance(
                    parentMessageVID = messageVocale.vid,
                    parentMessageKeyID = messageVocale.keyID
                )
            )
        }
    }


    private fun setupDataCollection() {
        viewModelScope.launch {
            // Collect MessageVocale entities
            appDatabase.messageVocaleDao().getAllFlow().collectLatest { messagesList ->
                // Fixed: Using the collected list directly instead of the Flow
                _uiState.value = _uiState.value.copy(
                    messageVocaleList = messagesList
                )

                appDatabase.etateMessageVocaleDao().getAllFlow().collectLatest { etatesList ->
                    // Fixed: Using the collected list directly instead of the Flow
                    _uiState.value = _uiState.value.copy(
                        etateMessageVocaleList = etatesList
                    )

                    // Process data for NoSqlMessageVocale
                    val etatesKeysByParent = mutableMapOf<Long, MutableList<String>>()

                    etatesList.forEach { etate ->
                        if (!etatesKeysByParent.containsKey(etate.parentMessageVID)) {
                            etatesKeysByParent[etate.parentMessageVID] = mutableListOf()
                        }
                        etatesKeysByParent[etate.parentMessageVID]?.add(etate.fireBaseKeyID)
                    }

                    val noSqlMessages = messagesList.map { message ->
                        NoSqlMessageVocale(
                            keyIDMessageVocale = message.keyID,
                            keyIDsChildListEtateMessageVocale = etatesKeysByParent[message.vid] ?: listOf()
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        noSqlMessageVocaleList = noSqlMessages
                    )
                }
            }
        }
    }
}

@Entity
data class EtateMessageVocale(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,

    //Forging Keys
    val parentMessageVID: Long = 0,
    val parentMessageKeyID: String =
        "SecteurDeClients.vid(SecteurDeClients.nom)",

    //Infos De Base
    var nom: Nom = Nom.EN_COURT_ENREGESTREMENT,
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
        EN_COURT_ENREGESTREMENT,
        ENVOYER,
        VUE,
        ECOUTE,
    }

    // Test instance function with random value implementation
    companion object {
        fun createTestInstance(parentMessageVID: Long, parentMessageKeyID: String): EtateMessageVocale {
            // Generate a random number between 1 and 9
            val randomNumber = Random.nextInt(1, 10) // Generates 1-9

            return EtateMessageVocale(
                vid = System.currentTimeMillis() + randomNumber,
                parentMessageVID = parentMessageVID,
                parentMessageKeyID = parentMessageKeyID,
                nom = when (randomNumber % 3) {
                    0 -> Nom.EN_COURT_ENREGESTREMENT
                    1 -> Nom.VUE
                    else -> Nom.ECOUTE
                },
                timestamps = DatesHandler().getCurrentTimestamps()
            )
        }
    }
}

@Entity
data class MessageVocale(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,
    val keyID: String = "",

    //Infos De Base

    var currentTimeStr: String = DatesHandler().getDateAndTimString().time,
    val vocaleKeyID: String = "",

    //Etates Mutable

) {


    // Test instance function with random value implementation
    companion object {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("2_MessageVocale")


        fun createTestInstance(): MessageVocale {
            // Generate a random number between 1 and 3
            val randomNumber = Random.nextInt(1, 4) // Generates 1, 2, or 3

            return MessageVocale(
                vid = System.currentTimeMillis(),
                vocaleKeyID = "test_${randomNumber}_${System.currentTimeMillis()}",
            )
        }
    }
}

data class NoSqlMessageVocale(
    val keyIDMessageVocale: String = "MessageVocale.fireBaseKeyID",
    val keyIDsChildListEtateMessageVocale: List<String> = listOf(),
)
