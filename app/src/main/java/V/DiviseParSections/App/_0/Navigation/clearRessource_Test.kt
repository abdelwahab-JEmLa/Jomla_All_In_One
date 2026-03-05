package V.DiviseParSections.App._0.Navigation

import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.centralDataBasesModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.classesHandlersModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.composRepositorysModule
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.factoryDataBaseProtoAvantJuin3Module
import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.viewModelModule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import org.koin.core.context.GlobalContext

@Composable
fun clearRessource_Test(activate: Boolean) {
    DisposableEffect(Unit,activate) {
        GlobalContext.get().unloadModules(
            listOf(
                viewModelModule,
                centralDataBasesModule,
                composRepositorysModule,
                factoryDataBaseProtoAvantJuin3Module,
                classesHandlersModule,
            )
        )
        onDispose {
            GlobalContext.get().loadModules(
                listOf(
                    centralDataBasesModule,
                    composRepositorysModule,
                    factoryDataBaseProtoAvantJuin3Module,
                    classesHandlersModule,
                    viewModelModule,
                )
            )
        }
    }
}
