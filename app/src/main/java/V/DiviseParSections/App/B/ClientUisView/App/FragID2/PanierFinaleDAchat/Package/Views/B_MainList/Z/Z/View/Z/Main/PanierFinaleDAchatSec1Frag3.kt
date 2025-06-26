package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.Z.Main

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.DetailBonVent.View.DetailsBonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.Z.View.ListAchats.View.A.List.MainList
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun PanierFinaleDAchatSec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val fCouleurAchatOperationRepositoryComposable =
        viewModel.uiStateCentralRepositorys.fCouleurAchatOperationRepositoryComposable

    Column {
        DetailsBonVent(viewModel = viewModel)

        MainList(
            modifier = modifier,
            viewModel = viewModel,
            fCouleurAchatOperationRepositoryComposable = fCouleurAchatOperationRepositoryComposable
        )
    }
}


