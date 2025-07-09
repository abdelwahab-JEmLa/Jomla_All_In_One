package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId

class PresenterElectroBoutiqueAbdelwahabSec10Frag1ViewModel(
    val aCentral: ACentralFacade,
) : ViewModel() {
    val getter = aCentral.getRepositorys
    val setter = aCentral.setRepositorys

    data class UiState(val catalogueFilterId: BsonObjectId? = null)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
