package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.ACentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Sec8FWinID1ViewModel(
    val context: Context,

    val a_CentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
    val wifiTransferDatas: WifiTransferDatas,
    val headViewModel: HeadViewModel,
) : ViewModel() {
    val appComptComposeRepositoryProtoJuin17 = a_CentralDatasHandlerProtoJuin9.appComptComposeRepositoryProtoJuin17
    data class UiState(
        val f: Int = 0,
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun sendOrderAuPresentoireDevice(catalogueBsonID:String): Unit {
        wifiTransferDatas.sendOrderToClientDisplayerT(
            WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct
            ,catalogueBsonID
        )
    }
}
