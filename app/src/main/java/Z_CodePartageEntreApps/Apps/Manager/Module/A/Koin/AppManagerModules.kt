package Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin

import Z_CodePartageEntreApps.Proto.B.Par.App.B.DataBaseManager.App.A.CategoryReorderAndSelection.Package.ViewModel.ViewModel_A4FragID1
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appManagerModules = module {

//    viewModel { ViewModelW4(get()) }

    viewModel { ViewModel_A4FragID1(get(), get()) }
}
