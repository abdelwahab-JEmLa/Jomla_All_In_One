package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Details.UI.B.UI.GBonVentInfosHeader
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandlerP2
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Preview
@Composable
fun DetailsBonVentPrev() {
    DetailsBonVent()
}

val petitePaddine = 4.dp //rename
@Composable
fun DetailsBonVent(
    modifier: Modifier = Modifier,
    viewModel: ZViewModel_Sec1Frag3 = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isMinimized = uiState.isMinimized
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val printHandler = remember { PrintReceiptHandlerP2() }

    val zAppComptRepositoryComposable =
        viewModel.uiStateCentralRepositorys.repo9AppCompt
    val comptAppActuelle = zAppComptRepositoryComposable.currentAppCompt

    val fVentCouleurOperationRepository =
        viewModel.uiStateCentralRepositorys.repo10OperationVentCouleur

    val ouvertPeriodKeyId = comptAppActuelle?.onVentHVentPeriodKeyId ?: ""

    // Get current BonVent from repository
    val currentBonVent = viewModel.aCentral.focusedVarsHandlerFacade.get.onVentM8BonVent

    if (comptAppActuelle != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(petitePaddine),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(petitePaddine),
                verticalArrangement = Arrangement.spacedBy(if (isMinimized) 6.dp else 12.dp)
            ) {
                if (!isMinimized) {
                    GBonVentInfosHeader(viewModel)
                }
                if (!isMinimized) {
                    PeriodDetailsSection(
                        viewModel = viewModel,
                        ouvertPeriodKeyId = ouvertPeriodKeyId,
                    )
                }

                ClientDetailsSection(
                    viewModel = viewModel,
                )

                if (!isMinimized) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }

                CartSummarySection(
                    viewModel,
                )
            }
        }
    } else {
        ErrorCard(modifier = modifier)
    }
}

