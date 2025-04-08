package Views.FragId3_DialogVendeurAfficheurInfosProduit.Ui.Objects
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.R
import com.example.clientjetpack.ViewModel.HeadViewModel

@Composable
 fun ConfirmExitDialog(
    viewModelInitApp: ViewModelInitApp,
    showConfirmDialog: Boolean,
    viewModel: HeadViewModel,
    parentCompose_1_2_ProduitAcheteOperationVid: Long,
    onDismiss: () -> Unit,
): Boolean {
    var showConfirmDialog1 = showConfirmDialog
    if (showConfirmDialog1) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog1 = false },
            icon = { Icon(Icons.Outlined.Warning, contentDescription = null) },
            title = {
                Text(
                    text = stringResource(R.string.confirm_exit_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.save_changes_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        val rep =
                            viewModelInitApp._1_2_ProduitAcheteOperation_Repository
                        rep.modelDatasSnapList
                            .find {
                                it.vid == parentCompose_1_2_ProduitAcheteOperationVid }
                            ?.apply {
                                etateActuellementEst =
                                    _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME                            }
                            ?.let { rep.updateUnSeulData(it) }

                        viewModel.saveSaleTransactionToSoldAriclesList()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.confirm_order_button))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val rep =
                            viewModelInitApp._1_2_ProduitAcheteOperation_Repository
                        rep.modelDatasSnapList
                            .find {
                                it.vid == parentCompose_1_2_ProduitAcheteOperationVid }
                            ?.apply {
                                etateActuellementEst =
                                    _1_2_ProduitAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK                            }
                            ?.let { rep.updateUnSeulData(it) }

                        onDismiss()

                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(stringResource(R.string.discard_button))
                    }
                }
            }
        )
    }
    return showConfirmDialog1
}
