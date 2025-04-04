package Z_MasterOfApps.A.MainActivity.Start.Module.A.Koin

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_.ConnectionManager
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.ViewModel_App2FragID1
import Z_MasterOfApps.Z.Android.A_Section.App.A.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import android.content.Context
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appClientModules = module {
    factory { (viewModel: HeadViewModel, context: Context) ->
        ConnectionManager(
            context = context,
        )
    }


    viewModel { (context: Context) -> HeadViewModel(get(),
        AppDatabase.DatabaseModule.getDatabase(get())
    ) }
    viewModel { Windows__ViewModel(get()) }
    viewModel { ViewModel_App2FragID1(get()) }
}
