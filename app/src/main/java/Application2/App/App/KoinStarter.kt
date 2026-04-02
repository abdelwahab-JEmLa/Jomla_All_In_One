package Application2.App.App

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val centralDataBasesModule_app2 = module {
    single {
        RepositorysMainGetter_app2(

        )
    }
}

val classesHandlersModule_app2 = module {
}

val appModule_App2_ac_app1 = module {
    includes(
        classesHandlersModule_app2,
        centralDataBasesModule_app2,
    )
}
