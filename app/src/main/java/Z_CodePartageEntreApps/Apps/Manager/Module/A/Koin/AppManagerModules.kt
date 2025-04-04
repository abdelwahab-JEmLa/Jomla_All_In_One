package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import W.Fragments.A.PanierFinaleDAchat.APP.ViewModel.ViewModelFragment_APP2_FragID_1
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appManagerModules = module {

//    viewModel { ViewModelW4(get()) }


    viewModel { ViewModel_A4FragID1(get(), get()) }
    viewModel { ViewModelFragment_APP2_FragID_1(get(),get(),get(),get()) }
}
