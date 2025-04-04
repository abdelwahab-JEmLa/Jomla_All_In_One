package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Module

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val composeModules = module {
    viewModel {ViewModelFragment_APP2_ID_2(get(),get(),get(),get()) }

}

// Load the module when the composable is first used
fun loadComposAPP1ID2Module() {
    loadKoinModules(composeModules)
}
