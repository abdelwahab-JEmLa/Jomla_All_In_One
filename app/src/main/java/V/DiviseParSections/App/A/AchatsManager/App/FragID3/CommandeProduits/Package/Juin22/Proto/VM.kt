package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ACentralCompoRepositoryProtoJuin9
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.androidx.compose.koinViewModel

class ViewModel_Sec1Frag3(
    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val d_AchatOperationComposeRepositoryPJ17 = a_CentralDatasHandlerProtoJuin9.d_AchatOperationComposeRepositoryPJ17
    data class UiState_Sec1Frag3(
        val v: String="",
        )

    private val _uiState = MutableStateFlow(UiState_Sec1Frag3())
    val uiState: StateFlow<UiState_Sec1Frag3> = _uiState.asStateFlow()
}

@Preview
@Composable
private fun Sec1Frag3Prv() {
    Sec1Frag3()
}
@Composable
fun Sec1Frag3(modifier: Modifier = Modifier,viewModel:ViewModel_Sec1Frag3 = koinViewModel()) {
    val achats = viewModel.d_AchatOperationComposeRepositoryPJ17.datasValue

}

@Composable
fun MainList(modifier: Modifier = Modifier) {
        //<--
        //TODO(1): fait groupe par parentBonVentObjectId
}

@Composable
fun MainItem(modifier: Modifier = Modifier) {

}
