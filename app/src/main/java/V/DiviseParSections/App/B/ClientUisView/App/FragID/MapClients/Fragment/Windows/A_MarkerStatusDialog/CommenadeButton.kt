package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
 fun CommenadeButton(
    context: Context,
    coroutineScope: CoroutineScope,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
    relatedClients: B_ClientDataBase?,
    viewModel: ViewModel_MapClients_App2FragID1,
    selectedMarker: Marker,
    onUpdateLongAppSetting: () -> Unit,
    onDismiss: () -> Unit,
) {
    StatusButton(
        text = "Mode Commande",
        icon = Icons.Default.ShoppingCart,
        color = Color(
            ContextCompat.getColor(
                context,
                B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT.color
            )
        ),
        onClick = {
            coroutineScope.launch {
                val repositorysModel =
                    _0_0_HeadOfRepositorys_Repository.repositorys_Model

                val ceComptVendeurInsertBonsAchatAuPeriodID =
                    repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
                        .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
                        ?.ceComptVendeurInsertBonsAchatAuPeriodID

                val clientId = relatedClients?.id ?: 0L

                // Check if a BonAchat already exists for this client in the active period
                val existingBonAchat = viewModel.modelDatasSnapList_1_3_BonAchat.find {
                    it.clientAcheteurID == clientId && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
                }

                if (existingBonAchat != null) {
                    // Update the existing BonAchat
                    val updatedBonAchat = existingBonAchat.copy(
                        etateActuellementEst = _1_3_BonAchat.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                        heurDebutInString = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date())
                    )

                    // Use upsert to update the existing record
                    repositorysModel._1_3_BonAchat_Repository.upsertUneDataEtReturnVID(
                        updatedBonAchat
                    ) { vid ->
                        repositorysModel.activeId_1_3_BonAchat.value = vid
                    }
                } else {
                    // Create a new BonAchat if none exists
                    repositorysModel._1_3_BonAchat_Repository.addDataAndReturneItVID(
                        _1_3_BonAchat(
                            clientAcheteurID = clientId,
                            parentVID_1_4_PeriodeVent = ceComptVendeurInsertBonsAchatAuPeriodID!!,
                            etateActuellementEst = _1_3_BonAchat.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                            heurDebutInString = SimpleDateFormat(
                                "HH:mm",
                                Locale.getDefault()
                            ).format(Date())
                        )
                    ) { newVid ->
                        // Update the MutableStateFlow with the new value
                        repositorysModel.activeId_1_3_BonAchat.value = newVid
                    }
                }

                //----------------------------------------------------------------------------------------/
                _01_Upsert(
                    viewModel,
                    ceComptVendeurInsertBonsAchatAuPeriodID,
                    repositorysModel,
                    clientId
                )

                //----------------------------------------------------------------------------------------/

                // Update the selected marker ID
                val selectedMarkedID = selectedMarker.id.toLong()
                viewModel.updateLongAppSetting(selectedMarkedID)

                // Finish and dismiss the dialog
                onUpdateLongAppSetting()
                onDismiss()
            }
        }
    )
}

