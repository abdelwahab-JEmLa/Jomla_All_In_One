package Z_MasterOfApps.Kotlin.ViewModel

import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.W.Test.UiState
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent.DataBaseFactoryMVentPeriode
import Z_MasterOfApps.Kotlin.ViewModel.Partage.Functions.FunctionsPartageEntreFragment
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_1.id4_DeplaceProduitsVerGrossist.ViewModel.Frag_4A1_ExtVM
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension.Frag2_A1_ExtVM
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_4.id2_TravaillieurListProduitAchercheChezLeGrossist.ViewModel.Extension.ExteVMFragmentId_2
import Z_MasterOfApps.Z.Android.Base.App.App._1.GerantAfficheurGrossistCommend.App.NH_5.id3_AfficheurDesProduitsPourLeColecteur.ViewModel.ExtensionVMApp1FragmentId_3
import Z_MasterOfApps.Z.Android.Main.Screen.Startup.ViewModel.Startup_Extension
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.ParamatersAppsModel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val B_ClientInfosProtoJuin3List: List<HClientInfos> = emptyList(),
    val mainLoadingProgress: Float = 0f,
)


class ViewModelInitApp(
    aCentral: ACentralFacade,
    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val produitModelRepository: A_ProduitRepository,
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val _1_4_PeriodeVent_Repository: DataBaseFactoryMVentPeriode,
    val repo_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
) : ViewModel() {
    val getter = aCentral.getRepositorys
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var _paramatersAppsViewModelModel by mutableStateOf(ParamatersAppsModel())
    var _modelAppsFather by mutableStateOf(_ModelAppsFather())

    var savedGridScrollPosition by mutableStateOf(0)

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
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _uiState.value = _uiState.value.copy(
                        b_ClientInfosProtoJuin3List = model.b_ClientInfosProtoJuin3Repository?.modelListFlow ?: emptyList(),
                        // FIXED: use lowercase 'b' to match the property name in MasterRepositorysModel
                        mainLoadingProgress = model.progress
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                isLoading = true

                // Observe progress changes
                produitModelRepository.progressRepo.collect { progress ->
                    loadingProgress = progress
                    if (progress >= 1.0f && isLoading) {
                        // Data is fully loaded, proceed with other initialization
                   //     loadData(viewModel)
                   //     FromAncienDataBase.setupRealtimeListeners(viewModel)
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModelInitApp", "Init failed", e)
                isLoading = false
            }
        }
    }
}
