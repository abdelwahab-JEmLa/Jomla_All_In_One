package Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.ViewModel.Extension

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import kotlinx.coroutines.CoroutineScope

class ViewModelExtension_App1_F1(
    val viewModel: ViewModelInitApp,
    val produitsMainDataBase: MutableList<ProduitModel>,
    val viewModelScope: CoroutineScope,
) {
    val produitsAChoisireLeurClient = viewModel
        ._paramatersAppsViewModelModel.produitsAChoisireLeurClient
}


