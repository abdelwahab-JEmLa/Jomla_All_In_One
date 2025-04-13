package Z_CodePartageEntreApps.Proto.B.ParSections.Fragment.A.AchatsManager.App._1.Shared.Module

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.View.composeModules
import org.koin.core.context.loadKoinModules

// Load the module when the composable is first used
fun loadComposAPP2Modules() {
    loadKoinModules(composeModules)
}
