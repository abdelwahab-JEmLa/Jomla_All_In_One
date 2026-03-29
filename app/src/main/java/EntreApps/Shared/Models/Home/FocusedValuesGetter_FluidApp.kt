package EntreApps.Shared.Models.Home

import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.List_Datas
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class FocusedValues_NewProtoPatterns(
    list_Datas: StateFlow<List_Datas?>,  // Now receives the VM's flow directly — no duplicate state
) {
    private val _activeCentralValues = MutableStateFlow(ActiveCentralValues())


    fun update_activeCentralValues(new: ActiveCentralValues) {
        _activeCentralValues.value = new
    }
    fun update_oneMutableStateLesseRessources(isControleFabVisible: Boolean) {
        _activeCentralValues.value.isControleFabVisible = isControleFabVisible
    }
}


