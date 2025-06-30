package Z_CodePartageEntreApps.Windows.B.Windows.ViewModel

import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation_Repository
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent.DataBaseFactoryMVentPeriode
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class InintializeViewModel_StartUpScreen {
    private val TAG = "InintializeViewModel"

    suspend fun waitForDataInitialization(
        _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
        _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
        _1_4_PeriodeVent_Repository: DataBaseFactoryMVentPeriode,
        uiStateFlow: MutableStateFlow<UiState_StartUpScreen>,
        viewModelScope: CoroutineScope,
        checkInitializationComplete: () -> Unit,
    ) {
        try {
            // Launch coroutines for all repositories in parallel
            val job1 = viewModelScope.launch {
                _1_1_CouleurAcheteOperation_Repository.progressRepo.collect { _ ->
                    updateDataLoadingState(
                        _1_1_CouleurAcheteOperation_Repository,
                        _1_2_ProduitAcheteOperation_Repository,
                        _1_4_PeriodeVent_Repository,
                        uiStateFlow
                    )
                    uiStateFlow.value = uiStateFlow.value.copy(
                        _1_1_CouleurAcheteOperationList = _1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                    )
                    checkInitializationComplete()
                }
            }

            val job2 = viewModelScope.launch {
                _1_2_ProduitAcheteOperation_Repository.progressRepo.collect { progress ->
                    updateDataLoadingState(
                        _1_1_CouleurAcheteOperation_Repository,
                        _1_2_ProduitAcheteOperation_Repository,
                        _1_4_PeriodeVent_Repository,
                        uiStateFlow
                    )
                    uiStateFlow.value = uiStateFlow.value.copy(
                        _1_2_ProduitAcheteOperationList = _1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    )
                    checkInitializationComplete()
                }
            }


            val job4 = viewModelScope.launch {
                _1_4_PeriodeVent_Repository.progressRepo.collect { progress ->
                    updateDataLoadingState(
                        _1_1_CouleurAcheteOperation_Repository,
                        _1_2_ProduitAcheteOperation_Repository,
                        _1_4_PeriodeVent_Repository,
                        uiStateFlow
                    )
                    uiStateFlow.value = uiStateFlow.value.copy(
                        _1_4_PeriodeVentList = _1_4_PeriodeVent_Repository.modelDatasSnapList
                    )
                    checkInitializationComplete()
                }
            }

            // Wait for all to complete
            job1.join()
            job2.join()
            job4.join()
        } catch (e: Exception) {
            uiStateFlow.value = uiStateFlow.value.copy(
                isDataLoading = false
            )
            handleError("Error initializing data", e, uiStateFlow)
            uiStateFlow.value = uiStateFlow.value.copy(
                isInitialized = true
            )
        }
    }

    private fun updateDataLoadingState(
        _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
        _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
        _1_4_PeriodeVent_Repository: DataBaseFactoryMVentPeriode,
        uiStateFlow: MutableStateFlow<UiState_StartUpScreen>
    ) {
        val progress1 = _1_1_CouleurAcheteOperation_Repository.progressRepo.value
        val progress2 = _1_2_ProduitAcheteOperation_Repository.progressRepo.value
        val progress4 = _1_4_PeriodeVent_Repository.progressRepo.value

        uiStateFlow.value = uiStateFlow.value.copy(
            isDataLoading = progress1 < 1.0f || progress2 < 1.0f  || progress4 < 1.0f
        )
    }

    fun checkInitializationComplete(
        _1_1_CouleurAcheteOperation_Repository: _1_1_CouleurAcheteOperation_Repository,
        _1_2_ProduitAcheteOperation_Repository: _1_2_ProduitAcheteOperation_Repository,
        _1_4_PeriodeVent_Repository: DataBaseFactoryMVentPeriode,
        uiStateFlow: MutableStateFlow<UiState_StartUpScreen>,
    ) {
        val progress1 = _1_1_CouleurAcheteOperation_Repository.progressRepo.value
        val progress2 = _1_2_ProduitAcheteOperation_Repository.progressRepo.value
        val progress4 = _1_4_PeriodeVent_Repository.progressRepo.value

        if (progress1 >= 1.0f && progress2 >= 1.0f  && progress4 >= 1.0f) {
            if (!uiStateFlow.value.isInitialized) {
                uiStateFlow.value = uiStateFlow.value.copy(
                    isInitialized = true
                )
            }
        }
    }

     fun handleError(message: String, exception: Exception, uiStateFlow: MutableStateFlow<UiState_StartUpScreen>) {
        Log.e(TAG, "$message: ${exception.message}")
        uiStateFlow.value = uiStateFlow.value.copy(
            errorMessage = "$message: ${exception.message}"
        )
    }
}
