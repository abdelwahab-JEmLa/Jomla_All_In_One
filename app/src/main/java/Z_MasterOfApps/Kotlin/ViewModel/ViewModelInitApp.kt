package Z_MasterOfApps.Kotlin.ViewModel

import Z_CodePartageEntreApps.Model.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase.Companion.updateClientsDataBase
import Z_CodePartageEntreApps.Model.I_CategoriesRepository
import Z_CodePartageEntreApps.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.Init.A_FirebaseListeners.FromAncienDataBase
import Z_MasterOfApps.Kotlin.ViewModel.Init.B_Load.loadData
import Z_MasterOfApps.Kotlin.ViewModel.Partage.Functions.FunctionsPartageEntreFragment
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.ViewModel.Frag_4A1_ExtVM
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension.Frag2_A1_ExtVM
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.ExteVMFragmentId_2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.ViewModel.ExtensionVMApp1FragmentId_3
import Z_MasterOfApps.Z.Android.Main.C_EcranDeDepart.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
class ViewModelInitApp(
    val produitModelRepository: A_ProduitModelRepository ,
    val i_CategoriesRepository: I_CategoriesRepository
) : ViewModel() {
    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())


    val produitsMainDataBaseFromRepositeryPrototype get() = produitModelRepository.modelDatas

    val modelAppsFather: _ModelAppsFather get() = _modelAppsFather
    val produitsMainDataBase = _modelAppsFather.produitsMainDataBase
    val viewModel = this@ViewModelInitApp

    val clientDataBaseSnapList = _modelAppsFather.clientDataBase

    var isLoading by mutableStateOf(false)
    var loadingProgress by mutableFloatStateOf(0f)

    val functionsPartageEntreFragment = FunctionsPartageEntreFragment(this@ViewModelInitApp)
    val extentionStartup = Startup_Extension(this@ViewModelInitApp)

    val frag1_A1_ExtVM = Frag2_A1_ExtVM(
        viewModel = viewModel,
        produitsMainDataBase = produitsMainDataBase,
    )
    val frag2_A1_ExtVM = ExteVMFragmentId_2(
        viewModelInitApp = viewModel,
        produitsMainDataBase = produitsMainDataBase,
        viewModelScope = viewModel.viewModelScope,
    )

    val frag_3A1_ExtVM = ExtensionVMApp1FragmentId_3(viewModel)

    val frag_4A1_ExtVM = Frag_4A1_ExtVM(viewModel)


    init {
        viewModelScope.launch {
            try {
                isLoading = true

                // Observe progress changes
                produitModelRepository.progressRepo.collect { progress ->
                    loadingProgress = progress
                    if (progress >= 1.0f && isLoading) {
                        // Data is fully loaded, proceed with other initialization
                        loadData(viewModel)
                        FromAncienDataBase.setupRealtimeListeners(viewModel)
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }

    fun updateStatueClientParID(
        clientId : Long,
        statueVente: Z_CodePartageEntreApps.Model.B_ClientsDataBase.GpsLocation.DernierEtatAAffiche
    ) {
        clientDataBaseSnapList.toMutableList().forEach { client ->
            if (client.id == clientId) {
                // Now works because gpsLocation is part of the data class
                val updatedClient = client.copy(
                    gpsLocation = client.gpsLocation.copy(
                        actuelleEtat = statueVente
                    )
                )
                updatedClient.updateClientsDataBase(viewModel)
            }
        }
    }


}
