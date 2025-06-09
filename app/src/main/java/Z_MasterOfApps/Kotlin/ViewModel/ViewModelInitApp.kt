package Z_MasterOfApps.Kotlin.ViewModel

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.A_ProduitRepository
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.I_CategoriesRepository
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_BonAchate_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVent_Repository
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur_Repository
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ViewModelInitApp(
    val produitModelRepository: A_ProduitRepository,
    val i_CategoriesRepository: I_CategoriesRepository,
    val _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
    val _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
    val C3_BonAchate_Repository: C3_BonAchate_Repository,
    val _1_4_PeriodeVent_Repository: _1_4_PeriodeVent_Repository,
    val _1_5_Vendeur_Repository: _1_5_Vendeur_Repository,
    val repo_0_0_HeadOfRepositorys_SQL_Repository: GroupeRepositorysProtoAvJuin3,
    val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository,
) : ViewModel() {
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


    fun updateStatueClientParID(
        clientId : Long,
        statueVente: B_ClientsDataBase.GpsLocation.DernierEtatAAffiche
    ) {
        clientDataBaseSnapList.toMutableList().forEach { client ->
            if (client.id == clientId) {
                // Now works because gpsLocation is part of the data class
                val updatedClient = client.copy(
                    gpsLocation = client.gpsLocation.copy(
                        actuelleEtat = statueVente
                    )
                )
                updateClientsDataBase(updatedClient)
            }
        }
    }
    fun updateClientsDataBase(data : B_ClientsDataBase) {
        viewModel.viewModelScope.launch {
            try {
                // Create a snapshot of the current state
                val currentState = data.copy()

                // Update local state using clear and addAll
                val clientsList = viewModel._modelAppsFather.clientDataBase
                val updatedList = clientsList.toMutableList()
                val index = updatedList.indexOfFirst { it.id == currentState.id }

                if (index != -1) {
                    updatedList[index] = currentState
                } else {
                    // If client doesn't exist, upsert them
                    updatedList.add(currentState)
                }

                // Replace entire list
                clientsList.clear()
                clientsList.addAll(updatedList)

                // Update Firebase with error handling
                try {
                    B_ClientsDataBase.refClientsDataBase.child(currentState.id.toString())
                        .setValue(currentState)
                        .await()
                } catch (e: Exception) {
                    // Revert local state if Firebase upsertLenceCommandeRepoGroupedProtoAvanJuin3 fails
                    clientsList.clear()
                    clientsList.addAll(
                        if (index != -1) updatedList.toMutableList().apply { this[index] = data }
                        else updatedList.dropLast(1)
                    )
                    throw e
                }

            } catch (e: Exception) {
                Log.e("B_ClientsDataBase", "Failed to upsertLenceCommandeRepoGroupedProtoAvanJuin3 client", e)
            }
        }
    }


}
