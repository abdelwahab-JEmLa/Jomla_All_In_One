package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.A.DeviseurProduitsCommedeAuGrossists.Package.App.ViewModelFragment_ID_7
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.ViewModel.ViewModelFragment_APP2_ID_2
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appManagerModules = module {

//    viewModel { ViewModelW4(get()) }


    viewModel { ViewModel_A4FragID1(get(), get()) }
    viewModel {ViewModelFragment_ID_7(get(),get(),get(),get()) }
    viewModel {ViewModelFragment_APP2_ID_2(get(),get(),get(),get()) }
}
