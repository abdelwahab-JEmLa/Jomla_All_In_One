package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import com.example.Packages.Views._2LocationGpsClients.App.MainApp.ViewModelExtensionMapsHandler
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    private val mapsHandler = ViewModelExtensionMapsHandler(
        viewModel=this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        modelAppsFather = _modelAppsFather,
    )

    // Delegate method for adding markers
    fun onClickAddMarkerButton(
        mapView: MapView,
        onMarkerSelected: (Marker) -> Unit,
        showMarkerDetails: Boolean,
        markers: MutableList<Marker>
    ) {
        mapsHandler.onClickAddMarkerButton(
            mapView = mapView,
            onMarkerSelected = onMarkerSelected,
            showMarkerDetails = showMarkerDetails,
            markers = markers,
        )
    }

    fun clearAllData(context: Context) {
        viewModelScope.launch {
            mapsHandler.clearAllData(mapViewVM)
            mapViewVM = initializeMapView(context)
        }
    }


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

                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }
}
