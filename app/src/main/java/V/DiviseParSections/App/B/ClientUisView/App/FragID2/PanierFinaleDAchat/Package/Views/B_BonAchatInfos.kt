package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.ViewModel.PanierFinaleDAchatViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.printReceipt
import Views.Package_4.SoldCartScreen.Components.OrderSuccessMessage
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GmodelTransactionCommercial
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ColumnScope.BonVentInfos(
    viewModel: PanierFinaleDAchatViewModel,
    _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
    relativeBonAchate: GmodelTransactionCommercial?,
    itemCount: Int,
    formattedTotalPrice: String,
    showOrderSuccess: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()
    val datasB_ClientInfosProtoJuin3List by remember(uiState.B_ClientInfosProtoJuin3List) { mutableStateOf(uiState.B_ClientInfosProtoJuin3List) }

    val _0_0_HeadSQLRepositorysrepositorysModel = _0_0_HeadSQLRepositorys.repositorys_Model

    val relativeClientDataBase =
        datasB_ClientInfosProtoJuin3List.find { it.id == relativeBonAchate?.clientAcheteurID }

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
                            repositorysModel = _0_0_HeadSQLRepositorysrepositorysModel,
                            scope = coroutineScope,
                            datasB_ClientInfosProtoJuin3List=datasB_ClientInfosProtoJuin3List
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
                            // First create add temporary product in the database
                            val tempProduct = _2_1_ProduitsDataBase(
                                nom = "منتج جديد",
                                itsTempProduit = true,
                                monPrixVent = 0.0
                            )

                            _0_0_HeadSQLRepositorysrepositorysModel._2_1_ProduitsDataBase_Repository.addDataAndReturnItVID(tempProduct) { productId ->
                                val produitOperation = _1_2_ProduitAcheteOperation(
                                    produitAcheterID = productId,
                                    parent_1_3_TransactionCommercial = relativeBonAchate?.vid ?: 0L,
                                    etateActuellementEst = _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME
                                )

                                // Add the product operation and get its ID
                                _0_0_HeadSQLRepositorysrepositorysModel.repositoryC2_ProduitAcheteOperation.addDataAndReturneItVID(produitOperation) { produitOperationId ->
                                    // Finally upsert add color operation with quantity 1
                                    val couleurOperation = _1_1_CouleurAcheteOperation(
                                        couleurIndex_ParentVID = 0L,
                                        parentProduitAchateOperationVID = produitOperationId,
                                        totaleQuantity = 1,
                                        etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                    )

                                    // Add color operation
                                    _0_0_HeadSQLRepositorysrepositorysModel._1_1_CouleurAcheteOperation_Repository.addDataAndReturnItVID(couleurOperation)
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
