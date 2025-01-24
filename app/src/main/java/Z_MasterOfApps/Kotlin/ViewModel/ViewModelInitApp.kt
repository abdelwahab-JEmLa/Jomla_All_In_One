package Z_MasterOfApps.Kotlin.ViewModel

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.ViewModelExtensionMapsHandler
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    var mapViewVM by mutableStateOf<MapView?>(null)
        private set
   
    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase
    val clientDataBaseSnapList = _modelAppsFather.clientDataBaseSnapList

    var clientsMarkers: SnapshotStateList<Marker> = mutableStateListOf()

    // Updated updateMarkers function
    fun updateMarkers() {
        clientsMarkers.clear() // Clear existing markers
        clientDataBaseSnapList.forEach { client ->
            client.gpsLocation.locationGpsMark?.let { marker ->
                clientsMarkers.add(marker)
            }
        }
    }

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    val mapsHandler = ViewModelExtensionMapsHandler(
        viewModelScope =this@ViewModelInitApp.viewModelScope,
        viewModel=this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        clientDataBaseSnapList=clientDataBaseSnapList,
        modelAppsFather = _modelAppsFather,
    )

    fun initializeMapView(context: Context): MapView {
        return MapView(context).also {
            mapViewVM = it
        }
    }

    init {
        viewModelScope.launch {
            try {
                isLoading = true
                val nombre = 0
                if (nombre == 0) {
                    LoadFromFirebaseProduits.loadFromFirebase(this@ViewModelInitApp)
                } else {
                    CreeDepuitAncienDataBases(
                        _modelAppsFather,
                        this@ViewModelInitApp
                    )
                }
                updateMarkers()

                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }
}
