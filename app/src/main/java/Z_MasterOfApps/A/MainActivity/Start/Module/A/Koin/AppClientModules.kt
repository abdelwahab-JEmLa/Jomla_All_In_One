package Z_MasterOfApps.A.MainActivity.Start.Module.A.Koin

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.ConnectionManager
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
    viewModel { ViewModel_MapClients_App2FragID1(
        get(),
        get(),
        get(),
    ) }
}
