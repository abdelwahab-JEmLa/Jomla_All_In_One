package com.example.clientjetpack.App2.App.A.Main.App

import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import com.example.clientjetpack.App2.App.A.Main.App.Archive.composRepositorysModule_app2
import com.example.clientjetpack.App2.App.A.Main.App.ViewModel.ViewModel_MainFragment
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.RepositorysMainGetter_app2
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val centralDataBasesModule_app2 = module {
    single {
        RepositorysMainGetter_app2(
            context = androidContext(),
            get(),
        )
    }
}

val appDatabase = module {
    single { AppDatabase.DatabaseModule.getDatabase(get()) }
}

val classesHandlersModule_app2 = module {
}

val viewModelModule_app2 = module {
    single {
        ViewModel_MainFragment(
            context = androidContext(),
            get(),
        )
    }
}

val appModule_App2_ac_app1 = module {
    includes(
        classesHandlersModule_app2,
        composRepositorysModule_app2,
        centralDataBasesModule_app2,
        viewModelModule_app2
    )
}

val appModule_App2 = module {
    includes(
        appDatabase,
        centralDataBasesModule_app2,
        factoryDataBaseProtoAvantJuin3Module,
        centralDataBasesModule,
        composRepositorysModule,
        classesHandlersModule,
        viewModelModule_app2,
        viewModelModule,

        composRepositorysModule_app2,
        //   classesHandlersModule_app2,
    )
}
