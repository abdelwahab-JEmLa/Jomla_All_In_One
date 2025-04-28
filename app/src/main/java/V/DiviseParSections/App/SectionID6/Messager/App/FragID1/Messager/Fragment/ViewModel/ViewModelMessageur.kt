package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.MessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.NoSqlMessageVocale
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
