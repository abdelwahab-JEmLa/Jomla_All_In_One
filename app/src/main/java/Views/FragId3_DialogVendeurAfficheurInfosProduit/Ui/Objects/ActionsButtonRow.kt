package Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_2_ProduitAcheteOperation
import V.DiviseParSections.App.SectionID9_AtelieModbile.Fragment.View.FragmentMain
import Views.FragId3_DialogVendeurAfficheurInfosProduit.updateState
import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
fun ActionsButtonRow(
    viewModel: HeadViewModel,
    currentSale: SoldArticlesTabelle,
    currentClient: B_ClientsDataBase?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModelInitApp: ViewModelInitApp,
    parentCompose_1_2_ProduitAcheteOperationVid: Long
) {
    // State for showing the pricing history dialog
    var showPricingHistoryDialog by remember { mutableStateOf(false) }

    // Handle the pricing history dialog display
    if (showPricingHistoryDialog) {
        Dialog(
            onDismissRequest = { showPricingHistoryDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp
            ) {
                FragmentMain()
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
    ) {

        OutlinedButton(
            onClick = {
                updateState(
                    viewModelInitApp,
                    parentCompose_1_2_ProduitAcheteOperationVid,
                    _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
                )

                viewModel.deleteSoldArticle(currentSale.vid)
                onDismiss()
                _DisplayeProductInfosToSeller(viewModelInitApp)
                    .onClickOnMain(
                        viewModelInitApp,
                        currentSale,
                        currentClient
                    )
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            )
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.cancel_button))
        }

        // Confirm purchase button
        FilledTonalButton(
            onClick = {
                updateState(
                    viewModelInitApp,
                    parentCompose_1_2_ProduitAcheteOperationVid,
                    _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                )

                onConfirm()
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Outlined.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.confirm_purchase_button))
        }
    }
}
