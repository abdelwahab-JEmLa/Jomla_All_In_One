package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import Views.Package_4.SoldCartScreen.Components.OrderSuccessMessage
import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views.Models._1_3_BonAchat
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.BonAchatInfos(
    composeKeyVID: Long?,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository,
    relativeBonAchate: _1_3_BonAchat?,
    itemCount: Int,
    formattedTotalPrice: String,
    showOrderSuccess: Boolean,
    scope: CoroutineScope,
    onConfirmOrder: () -> Unit,
    onShowOrderSuccessChange: (Boolean) -> Unit,
) {
    val repositorysModel = _0_0_HeadOfRepositorys_Repository.repositorys_Model
    val relativeClientDataBase=
    repositorysModel._3_ClientsDataBase_Repository
        .modelDatasSnapList.find { it.vid== composeKeyVID}

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "العميل: ${relativeClientDataBase?.nom ?: ""}",
                style = MaterialTheme.typography.titleMedium
            )

            // Order Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "عدد المنتجات: $itemCount",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "المجموع: $formattedTotalPrice",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    onShowOrderSuccessChange(true) // Use the callback instead of modifying local variable
                    scope.launch {
                        // Delay to show success animation before navigating
                        delay(1500)
                        onShowOrderSuccessChange(false) // Use the callback instead of modifying local variable
                        relativeBonAchate?.apply {
                            etateActuellementEst = _1_3_BonAchat
                                .EtateActuellementEst
                                .A_COMMANDE_CONFIRME
                        }?.let {
                            repositorysModel
                                ._1_3_BonAchat_Repository
                                .updateUnSeulData(
                                    it
                                )
                        }

                        repositorysModel.activeId_1_3_BonAchat.value=0L

                        onConfirmOrder()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = itemCount > 0
            ) {
                Text("تأكيد الطلب")
            }
        }
    }

    // Success Animation Overlay
    AnimatedVisibility(
        visible = showOrderSuccess,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically(),
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        OrderSuccessMessage()
    }
}
