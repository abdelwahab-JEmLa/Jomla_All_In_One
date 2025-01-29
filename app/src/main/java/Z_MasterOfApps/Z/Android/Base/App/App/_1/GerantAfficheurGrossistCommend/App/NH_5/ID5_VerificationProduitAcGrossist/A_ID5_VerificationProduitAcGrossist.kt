package Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_2.id1_GerantDefinirePosition.Modules.ClientEditePositionDialog
import Z_MasterOfApps.Z.Android.Packages._1.GerantAfficheurGrossistCommend.App.NH_5.ID5_VerificationProduitAcGrossist.ViewModel.Extension.ViewModelExtension_App1_F5
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

private const val TAG = "A_id1_GerantDefinirePosition"

@Composable
internal fun A_ID5_VerificationProduitAcGrossist(
    viewModelInitApp: ViewModelInitApp = viewModel(),
    modifier: Modifier = Modifier,
) {
    val extensionVM =
        ViewModelExtension_App1_F5(viewModelInitApp, viewModelInitApp.produitsMainDataBase)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {

                if (extensionVM.produitsVerifie.size > 0) {
                    C_MainList_F5(
                        extensionVM = extensionVM,
                        viewModel = viewModelInitApp,
                        paddingValues = paddingValues
                    )
                }

                if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
                    B_MainScreenFilterFAB_F5(
                        extensionVM = extensionVM,
                        viewModelProduits = viewModelInitApp,
                    )
                }
            }

            ClientEditePositionDialog(
                viewModelProduits = viewModelInitApp,
            )
        }
    }
}

