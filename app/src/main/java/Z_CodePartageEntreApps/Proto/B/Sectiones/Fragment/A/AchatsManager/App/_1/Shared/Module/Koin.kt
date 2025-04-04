package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Module

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_1.DeviseurProduitsCommedeAuGrossists.Package.App.ViewModelFragment_APP2_FragID_1
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val composeModules = module {
    viewModel { ViewModelFragment_APP2_FragID_1(get(),get(),get(),get()) }
    viewModel {ViewModelFragment_APP2_ID_2(get(),get(),get(),get()) }
}

// Load the module when the composable is first used
fun loadComposAPP2Modules() {
    loadKoinModules(composeModules)
}
