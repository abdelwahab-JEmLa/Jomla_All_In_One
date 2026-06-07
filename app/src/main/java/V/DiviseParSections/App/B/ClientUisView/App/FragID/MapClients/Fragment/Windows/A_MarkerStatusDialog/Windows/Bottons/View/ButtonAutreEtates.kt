package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun M8BonVent.EtateActuellementEst.ButtonAutreEtates(
    modifier: Modifier = Modifier,
    viewModel: MapClientsViewModel = koinViewModel(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo10OperationVentCouleur: Repo10OperationVentCouleur = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur,
    clickedClient: Long,
    onClick: (M8BonVent) -> Unit = {},
) {
    var toastData by remember { mutableStateOf<ToastData?>(null) }
    var showSubtractDepotConfirmDialog by remember { mutableStateOf(false) }
    var pendingSubtractBonVentKeyID by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val relative_Etate = this

    val relative_M2Client = aCentralFacade.repositorysMainGetter.repo2Client.datasValue
        .find { it.id == clickedClient }

    if (relative_M2Client == null) {
        toastData = ToastData(
            message = "Client non trouvé",
        )
        return
    }

    ModernToastMessage(
        toastData = toastData,
        onDismiss = { toastData = null }
    )

    FilledTonalButton(
        modifier = Modifier,
        onClick = {
        /*    (relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME) .ifTrue {
                viewModel.update_list_M3(
                    aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.map {
                        it.dernierTimeTampsSynchronisationAvecFireBase
                    }
                )

            }              */

            val found_Or_Default_M8BonVent =
                get_Found_Or_Default_M8BonVent(aCentralFacade, relative_M2Client, relative_Etate)
                    ?: return@FilledTonalButton

            if (found_Or_Default_M8BonVent.found != null) {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    found_Or_Default_M8BonVent.found
                )
                onClick(found_Or_Default_M8BonVent.found)
            } else {
                aCentralFacade.repositorysMainSetter
                    .addNew_M8BonVent(found_Or_Default_M8BonVent.default_If_No_Found)
                onClick(found_Or_Default_M8BonVent.default_If_No_Found)
            }

            (relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME)
                .ifTrue {

                    val lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT =
                        repositorysMainGetter.repo8BonVent.datasValue.lastOrNull {
                            it.parent_M2Client_KeyID == relative_M2Client.keyID
                                    && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                        }
                    if (lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT != null
                        && lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT.confirmeCommande_TimeTamp == 0L
                    ) {
                        aCentralFacade.repositorysMainSetter.update_M8BonVent(
                            lastCommande_Transaction_ON_MODE_COMMEND_ACTUELLEMENT.copy(
                                confirmeCommande_TimeTamp = System.currentTimeMillis()
                            )
                        )
                    }

                    // instead of subtracting inline, we defer to AlertDialog below
                    val confirmedBonVentKeyID =
                        found_Or_Default_M8BonVent.found?.keyID
                            ?: found_Or_Default_M8BonVent.default_If_No_Found.keyID
                    pendingSubtractBonVentKeyID = confirmedBonVentKeyID
                    showSubtractDepotConfirmDialog = true
                }

            if (relative_Etate == M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                || relative_Etate == M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
            ) {
                relative_M2Client.edite_Exact_Gps_est_fait.ifFalse {
                    viewModel.update_uiState_m2Client_In_ShowEditMarkerMode(relative_M2Client)
                }
                viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
            }

            aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = relative_Etate.color.copy(alpha = 0.2f),
            contentColor = relative_Etate.color
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = relative_Etate.nomArabe,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(relative_Etate.nomArabe)
        }
    }

    if (showSubtractDepotConfirmDialog) {
        val keyID = pendingSubtractBonVentKeyID
        AlertDialog(
            onDismissRequest = {
                showSubtractDepotConfirmDialog = false
                pendingSubtractBonVentKeyID = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "تأكيد الخصم من المستودع",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "هل تريد خصم كميات هذا الطلب من المستودع؟",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "سيتم تحديث عدد الألوان في المستودع تلقائياً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لا يمكن التراجع عن هذا الإجراء",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (keyID != null) {
                            repositorysMainGetter.repo10OperationVentCouleur.datasValue
                                .filter { it.parent_M8BonVent_KeyId == keyID && it.quantity > 0 }
                                .forEach { operation ->
                                    val couleur = repositorysMainGetter.find_M3CouleurInfos_By_KeyID(
                                        operation.parent_M3CouleurProduit_KeyID
                                    )
                                    if (couleur != null) {
                                        val updatedCouleur = couleur.copy(
                                            count_Don_Depot = maxOf(0, couleur.count_Don_Depot - operation.quantity),
                                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                        )
                                        aCentralFacade.repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(updatedCouleur)
                                    }
                                }
                        }
                        showSubtractDepotConfirmDialog = false
                        pendingSubtractBonVentKeyID = null
                    }
                ) {
                    Text("تأكيد الخصم", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSubtractDepotConfirmDialog = false
                        pendingSubtractBonVentKeyID = null
                    }
                ) {
                    Text("إلغاء", style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }
}
