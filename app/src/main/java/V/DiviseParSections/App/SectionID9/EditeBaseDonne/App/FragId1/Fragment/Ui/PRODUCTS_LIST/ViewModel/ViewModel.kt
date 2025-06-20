package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId

class Sec9FragId1ViewId2ViewModel(
    a_CentralCompoRepositoryProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    data class UiState(
        var showDetailsExpandedPourTout: Boolean = true,
        var bSonIdDesProduitsOuLeurDetailsEstFerme: Set<BsonObjectId> = emptySet()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun toggleProductDetailsVisibility(productBsonId: String) {
        val bsonObjectId = BsonObjectId(productBsonId)
        val currentSet = _uiState.value.bSonIdDesProduitsOuLeurDetailsEstFerme
        _uiState.value = _uiState.value.copy(
            bSonIdDesProduitsOuLeurDetailsEstFerme = if (currentSet.contains(bsonObjectId)) currentSet - bsonObjectId else currentSet + bsonObjectId
        )
    }

    fun isProductDetailsExpanded(productBsonId: String) =
        !_uiState.value.bSonIdDesProduitsOuLeurDetailsEstFerme.contains(BsonObjectId(productBsonId))

    fun update_showDetailsExpanded() {
        _uiState.value =
            _uiState.value.copy(showDetailsExpandedPourTout = !uiState.value.showDetailsExpandedPourTout)
    }
}
