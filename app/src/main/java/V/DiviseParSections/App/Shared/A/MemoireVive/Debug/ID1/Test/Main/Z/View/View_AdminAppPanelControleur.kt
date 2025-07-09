package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test.Main.Z.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_AdminAppPanelControleur(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun View_AdminAppPanelControleur(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_AdminAppPanelControleur = koinInject(),
) {

}
