package com.example.clientjetpack.App2.App.A.Main.Base.Modules

import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.ProductDisplayController
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.toggle_update_expanded_M3CouleurProduitInfos_app2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class UiState(
    val devicesTypeManager: List<DevicesTypeManager> = emptyList(),
    val productDisplayController: ProductDisplayController,
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null,
)

@SuppressLint("StaticFieldLeak")
open class WifiConexiontLuncher(
    val context: Context,
    val focusedValuesGetter_app2: FocusedValuesGetter_app2,
    val repositorysMainGetter_app2: RepositorysMainGetter_app2,
) : ViewModel() {

    private val tag = "HeadViewModel_App2"

    private val firestore by lazy {
        Firebase.firestore.apply {
            Log.d(tag, "Firestore instance accessed from ViewModel")
        }
    }

    val _uiState = MutableStateFlow(UiState(productDisplayController = ProductDisplayController()))
    open val uiState = _uiState.asStateFlow()


    private val connectionManager = WifiTransferDatas_app2(
        context = context,
        repositorysMainGetter = repositorysMainGetter_app2,
        focusedValuesGetter = focusedValuesGetter_app2
    ) { payload -> handleRetoureDataPayload(payload) }

    fun sendOrderToClientDisplayer(orderName: String, data: Any? = null) {
        viewModelScope.launch {
            connectionManager.sendData("$orderName$data")
        }
    }

    private fun handleRetoureDataPayload(payload: String) {
        WifiUpdateClientDisplayerStats_app2.fromPayload(payload)?.let { (messageType, content) ->
            when (messageType) {
                WifiUpdateClientDisplayerStats_app2.ClientMainGridScrollPosition -> updateDisplayController {
                    copy(mainGridScrollPosition = content.toInt())
                }

                WifiUpdateClientDisplayerStats_app2.ClientWindowsDisplayedProductId -> {
                    // Log the received product ID
                    Log.d("ClientWindowsDisplayedProductId", "📱 ClientWindowsDisplayedProductId reçu: $content (Long: ${content.toLong()})")

                    updateDisplayController {
                        copy(clientWindowsDisplayedProductId = content.toLong())
                    }

                    focusedValuesGetter_app2.currentActive_M9AppCompt?.let {
                        Log.d(tag, "✅ Mise à jour de active_ProduitKeyID_Au_DroopDown_PresenterEcran: $content")
                        repositorysMainGetter_app2.repo9AppCompt.update(
                            it.copy(
                                active_ProduitKeyID_Au_DroopDown_PresenterEcran = content
                            )
                        )
                    } ?: Log.e(tag, "❌ currentActive_M9AppCompt est null, impossible de mettre à jour")
                }

                WifiUpdateClientDisplayerStats_app2.DISMISS_PRODUCT_INFO -> updateDisplayController {
                    copy(
                        clientWindowsDisplayedProductId = null, searchWindowsDisplaye = ""
                    )
                }

                WifiUpdateClientDisplayerStats_app2.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran -> {
                    // Trouver la couleur correspondante
                    val couleurInfos = repositorysMainGetter_app2.find_M3CouleurInfos_By_KeyID(content)

                    if (couleurInfos != null) {
                        // Utiliser la fonction toggle pour mettre à jour
                        toggle_update_expanded_M3CouleurProduitInfos_app2(
                            focusedValuesGetter_app2 = focusedValuesGetter_app2,
                            relative_M3CouleurProduitInfos = couleurInfos
                        )
                    } else {
                        Log.e(tag, "❌ Couleur introuvable pour keyID: $content")
                    }
                }

                else -> {}
            }
        } ?: Log.d(tag, "📩 Unhandled message received: $payload")
    }


    private fun observeConnectionState() {
        viewModelScope.launch {
            connectionManager.connectionUiState.collect { connectionState ->

                updateDisplayController {
                    copy(
                        isConnected = connectionState.isConnected,
                        connectionStatus = connectionState.connectionStatus,
                    )
                }

                _uiState.update { it.copy(error = connectionState.error) }
            }
        }
    }

    private fun updateDisplayController(update: ProductDisplayController.() -> ProductDisplayController) {
        _uiState.update { it.copy(productDisplayController = update(it.productDisplayController)) }
    }


    private fun getHostDevices(): List<String> {
        return uiState.value.devicesTypeManager.filter { it.isHost }.map { it.name }
    }

    fun updateTypePhone(type: Boolean = false) {
        updateDisplayController {
            copy(isHostPhone = type)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun initializeConnection() {
        val currentDevice = Build.MODEL.lowercase()
        val isHostDevice = getHostDevices().any { deviceName ->
            currentDevice.contains(deviceName)
        }


        if (isHostDevice) {
            updateTypePhone(true)
        } else {
            updateTypePhone(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsHost() = connectionManager.startAsHost()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun startAsClient() = connectionManager.startAsClient()

    fun disconnect() = connectionManager.disconnect()


    // Ensure the directory exists when initializing the path
    val viewModelImagesPath =
        File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne/").apply {
            if (!exists()) {
                mkdirs()
            }
        }

}

