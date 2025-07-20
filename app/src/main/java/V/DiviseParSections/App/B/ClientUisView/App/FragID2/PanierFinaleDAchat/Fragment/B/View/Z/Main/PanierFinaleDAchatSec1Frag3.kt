package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ifFalse
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.DetailsBonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.MainList_Frag_Panie
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun PanierFinaleDAchatSec1Frag3(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinViewModel(),
    its_From_SearchPrd: Boolean = false
) {
    Column {
        its_From_SearchPrd.ifFalse {
            DetailsBonVent(viewModel = viewModel)
        }

        MainList_Frag_Panie(
            modifier = modifier,
            viewModel = viewModel,
            its_From_SearchPrd = its_From_SearchPrd
        )
    }
}
