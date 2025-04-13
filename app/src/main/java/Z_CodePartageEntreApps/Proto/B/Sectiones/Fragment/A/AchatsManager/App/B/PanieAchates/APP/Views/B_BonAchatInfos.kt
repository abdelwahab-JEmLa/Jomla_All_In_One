package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.PanieAchates.APP.Views

import Views.Package_4.SoldCartScreen.Components.OrderSuccessMessage
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Modules.printReceipt
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

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
    database: AppDatabase = koinInject(),
) {
    val repositorysModel = _0_0_HeadOfRepositorys_Repository.repositorys_Model
    val relativeClientDataBase =
        repositorysModel._3_ClientsDataBase_Repository
            .modelDatasSnapList.find { it.vid == relativeBonAchate?.clientAcheteurID }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                IconButton(
                    onClick = {
                        printReceipt(
                            context = context,
                            bonAchat = relativeBonAchate,
                            repositorysModel = repositorysModel,
                            database = database,
                            scope = coroutineScope
                        )
                    },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Imprimer bon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            // First create a temporary product in the database
                            val tempProduct = Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase(
                                nom = "منتج جديد",
                                itsTempProduit = true,
                                monPrixVent = 0.0
                            )

                            repositorysModel._2_1_ProduitsDataBase_Repository.addDataAndReturnItVID(tempProduct) { productId ->
                                val produitOperation = _1_2_ProduitAcheteOperation(
                                    produitAcheterID = productId,
                                    parent_1_3_BonAchat = relativeBonAchate?.vid ?: 0L,
                                    etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                                )

                                // Add the product operation and get its ID
                                repositorysModel._1_2_ProduitAcheteOperation_Repository.addDataAndReturneItVID(produitOperation) { produitOperationId ->
                                    // Finally add a color operation with quantity 1
                                    val couleurOperation = _1_1_CouleurAcheteOperation(
                                        couleurIndex_ParentVID = 0L,
                                        parentProduitAchateOperationVID = produitOperationId,
                                        totaleQuantity = 1,
                                        etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                    )

                                    // Add color operation
                                    repositorysModel._1_1_CouleurAcheteOperation_Repository.addDataAndReturnItVID(couleurOperation)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "إضافة منتج",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

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
                        onShowOrderSuccessChange(true)
                        scope.launch {
                            // Delay to show success animation before navigating
                            delay(1500)
                            onShowOrderSuccessChange(false)
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

                            repositorysModel.activeId_1_3_BonAchat.value = 0L

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
