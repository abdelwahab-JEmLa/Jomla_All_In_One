package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.DetailsBonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.MainList
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun PanierFinaleDAchatSec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel()
) {
    val fVentCouleurOperationRepository =
        viewModel.uiStateCentralRepositorys.fVentCouleurOperationRepository


    Column {
        DetailsBonVent(viewModel = viewModel)
        MainList(
            modifier = modifier,
            viewModel = viewModel,
            fVentCouleurOperationRepository = fVentCouleurOperationRepository
        )
    }
}


