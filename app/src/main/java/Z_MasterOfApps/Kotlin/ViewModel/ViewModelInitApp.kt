package Z_MasterOfApps.Kotlin.ViewModel

import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension.ViewModelExtension_App1_F1
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_3.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.ViewModelExtension_App1_F2
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.A_LoadFireBase.LoadFromFirebaseProduits
import Z_MasterOfApps.Z_AppsFather.Kotlin._3.Init.CreeDepuitAncienDataBases
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())
    var mapViewVM by mutableStateOf<MapView?>(null)
        private set
   
    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase

    val clientDataBaseSnapList = _modelAppsFather.clientDataBaseSnapList

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    val extension_App1_F1 = ViewModelExtension_App1_F1(
        viewModel=this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        viewModelScope =this@ViewModelInitApp.viewModelScope,
    )
    val extension_App1_F2 = ViewModelExtension_App1_F2(
        viewModel=this@ViewModelInitApp,
        produitsMainDataBase = produitsMainDataBase,
        viewModelScope =this@ViewModelInitApp.viewModelScope,
    )

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
