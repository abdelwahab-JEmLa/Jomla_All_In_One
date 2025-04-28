package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Models.NoSqlMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

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


    init {
        viewModelScope.launch {
            // Check count in a coroutine, not on the main thread
            val messageCount = appDatabase.messageVocaleDao().getCount()

            if (messageCount > 0) {
                // Add test data if needed
                val messageVocale = MessageVocale.createTestInstance()
                appDatabase.messageVocaleDao().insert(messageVocale)

                appDatabase.etateMessageVocaleDao().insert(
                    EtateMessageVocale.createTestInstance(
                        parentMessageVID = messageVocale.vid,
                        parentMessageKeyID = messageVocale.fireBaseKeyID
                    )
                )
            }

            // Setup the data collection flow
            setupDataCollection()
        }
    }

    private fun setupDataCollection() {
        viewModelScope.launch {
            // Collect MessageVocale entities
            appDatabase.messageVocaleDao().getAllFlow().collectLatest { messagesList ->
                _uiState.value = _uiState.value.copy(
                    listMessageVocale = appDatabase.messageVocaleDao().getAllFlow()
                )

                appDatabase.etateMessageVocaleDao().getAllFlow().collectLatest { etatesList ->
                    _uiState.value = _uiState.value.copy(
                        listEtateMessageVocale = appDatabase.etateMessageVocaleDao().getAllFlow()
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
                            keyIDMessageVocale = message.fireBaseKeyID,
                            keyIDsChildListEtateMessageVocale = etatesKeysByParent[message.vid] ?: listOf()
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        listNoSqlMessageVocale = MutableStateFlow(noSqlMessages)
                    )
                }
            }
        }
    }


}

