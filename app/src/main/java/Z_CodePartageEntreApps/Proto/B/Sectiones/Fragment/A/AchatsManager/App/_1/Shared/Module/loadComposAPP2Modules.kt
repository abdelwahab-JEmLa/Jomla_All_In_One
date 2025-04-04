package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App._1.Shared.Module

import W.Fragments.A.PanierFinaleDAchat.APP.composeModules
import org.koin.core.context.loadKoinModules

// Load the module when the composable is first used
fun loadComposAPP2Modules() {
    loadKoinModules(composeModules)
}
