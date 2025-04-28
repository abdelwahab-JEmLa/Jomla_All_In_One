package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.NoSqlMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

// UI State class that contains all UI-related state
data class MessageurUiState(
    val listNoSqlMessageVocale: Flow<List<NoSqlMessageVocale>> = emptyFlow(),
    val listMessageVocale: Flow<List<MessageVocale>> = emptyFlow(),
    val listEtateMessageVocale: Flow<List<EtateMessageVocale>> = emptyFlow(),
)

class ViewModelMessageur(
    val appDatabase: AppDatabase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageurUiState())
    val uiState: StateFlow<MessageurUiState> = _uiState.asStateFlow()

    val storageRef = FirebaseStorage.getInstance().reference

    init {
        if (true) {
            viewModelScope.launch {
                // Add suspend function call within coroutine
                val messageVocale = MessageVocale.createTestInstance()
                appDatabase.messageVocaleDao().insert(messageVocale)

                // Fix the EtateMessageVocale creation by passing required parameters
                appDatabase.etateMessageVocaleDao().insert(
                    EtateMessageVocale.createTestInstance(
                        parentMessageVID = messageVocale.vid,
                        parentMessageKeyID = messageVocale.fireBaseKeyID
                    )
                )
            }
        }
        // Collect messages and their states from the database
        viewModelScope.launch {
            // Collect MessageVocale entities
            appDatabase.messageVocaleDao().getAllFlow().collectLatest { messagesList ->
                // FIXED: collect to _uiState.listMessageVocale
                _uiState.value = _uiState.value.copy(listMessageVocale = appDatabase.messageVocaleDao().getAllFlow())

                appDatabase.etateMessageVocaleDao().getAllFlow().collectLatest { etatesList ->
                    // FIXED: collect to _uiState.listEtateMessageVocale
                    _uiState.value = _uiState.value.copy(listEtateMessageVocale = appDatabase.etateMessageVocaleDao().getAllFlow())

                    // Group EtateMessageVocale by parent message using forEach
                    val etatesKeysByParent = mutableMapOf<Long, MutableList<String>>()

                    etatesList.forEach { etate ->
                        if (!etatesKeysByParent.containsKey(etate.parentMessageVID)) {
                            etatesKeysByParent[etate.parentMessageVID] = mutableListOf()
                        }
                        etatesKeysByParent[etate.parentMessageVID]?.add(etate.fireBaseKeyID)
                    }

                    // FIXED: Rules for adding NoSqlMessageVocale with associated states
                    val noSqlMessages = messagesList.map { message ->
                        NoSqlMessageVocale(
                            keyIDMessageVocale = message.fireBaseKeyID,
                            keyIDsChildListEtateMessageVocale = etatesKeysByParent[message.vid] ?: listOf()
                        )
                    }

                    // Update the UI state with the NoSqlMessageVocale list
                    _uiState.value = _uiState.value.copy(
                        listNoSqlMessageVocale = MutableStateFlow(noSqlMessages)
                    )
                }
            }
        }
    }


}

