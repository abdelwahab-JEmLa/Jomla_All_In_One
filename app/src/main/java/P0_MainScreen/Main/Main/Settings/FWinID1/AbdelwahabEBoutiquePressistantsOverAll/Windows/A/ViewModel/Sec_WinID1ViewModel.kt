package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A_CentralCompoRepositoryProtoJuin9
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Sec8FWinID1ViewModel(
    val context: Context,

    val a_CentralDatasHandlerProtoJuin9: A_CentralCompoRepositoryProtoJuin9,
    val wifiTransferDatas: WifiTransferDatas,
    val headViewModel: HeadViewModel,
) : ViewModel() {
    val appComptComposeRepositoryProtoJuin17 = a_CentralDatasHandlerProtoJuin9.appComptComposeRepositoryProtoJuin17
    data class UiState(
        val f: Int = 0,
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val connectionManager = WifiTransferDatas(
        context = context,
        a_CentralDatasHandlerProtoJuin9 = a_CentralDatasHandlerProtoJuin9,
    )
    { }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() = connectionManager.startAsHost()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() = connectionManager.startAsClient()
    private fun handlePayload(payload: String) {
        WifiUpdateClientDisplayerStats.fromPayload(payload)?.let { (messageType, content) ->
            when (messageType) {
                WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition ->  {
                    /*   if (content=="0") {
                           showToast("Filtre catalogue reçu: $content")
                       }
                       Log.d(tag, "📩 ClientMainGridScrollPosition received: $content")   */

                }
                   else ->{}

            }
        }
    }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        viewModelScope.launch {
            connectionManager.sendData("$orderName$data")
        }
    }

    fun sendOrderAuPresentoireDevice(catalogueBsonID:String): Unit {

        Log.d("handlePayload", "📩 sendOrderAuPresentoireDevice :" +
                " ${WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix}$catalogueBsonID")

        wifiTransferDatas.sendOrderToClientDisplayerT2(
            WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
            catalogueBsonID
        )

        headViewModel.sendOrderToClientDisplayer(
            WifiUpdateClientDisplayerStats.FilterProduitsParCatalogueBsonID.prefix,
            100
        )
        wifiTransferDatas.sendOrderToClientDisplayerT(
            WifiUpdateClientDisplayerStats.NewArregmentColorsJsonStruct
            ,catalogueBsonID
        )

        sendParHead(999)
    }
    fun sendParHead(data: Int){
        headViewModel.sendOrderToClientDisplayer(
            WifiUpdateClientDisplayerStats.FilterProduitsParCatalogueBsonID.prefix,
            data
        )
    }

}
