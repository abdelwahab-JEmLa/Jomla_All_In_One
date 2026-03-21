package Application2.App.View.Pro0.Proto

import Application2.App.App.ViewModel.ViewModel_MainFragment
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application2.App.View.Pro0.Proto.Components.Big_Principale_AppEcranPresntoireJemlaCom
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.Components.SubColorCard_WithButton_app2
import Application2.App.View.Pro0.Proto.ViewS.Compact_Header_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_AppEcranPresntoireJemlaCom(
    relative_M1ProduitToListM3Couleur: Pair<M01Produit, List<M3CouleurProduitInfos>>,
    focusedValuesGetter_app2: RepositorysMainGetter_app2 = koinInject(),
    viewModel: ViewModel_MainFragment = koinViewModel(),
) {
    val relative_M1produit = relative_M1ProduitToListM3Couleur.first
    val relativeList_M3ColorsProduit = relative_M1ProduitToListM3Couleur.second

    val wifiState by viewModel.wifiState.collectAsState()
    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected

    val expandState = remember(relative_M1produit.keyID, relativeList_M3ColorsProduit) {
        ProduitExpandState(
            relative_M1produit = relative_M1produit,
            relativeList_M3ColorsProduit = relativeList_M3ColorsProduit,
            focusedValuesGetter = focusedValuesGetter_app2,
        )
    }

    LaunchedEffect(focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos) {
        expandState.syncFromFocusedValues(
            focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos
        )
    }

    // Single WiFi-aware tap handler for both Big_Principale and all SubColorCards.
    // Toggles expand locally, then broadcasts to the client when this device is the host.
    val onImageTapWithWifi: (M3CouleurProduitInfos) -> Unit = { couleur ->
        expandState.onImageTap(couleur)
        if (isHostPhone && isConnected) {
            viewModel.sendOrderToClientDisplayerT(
                WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran,
                couleur.keyID
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(expandState.cardPadding.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = expandState.cardElevation.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(expandState.innerPadding.dp)
            ) {
                Compact_Header_AppEcranPresntoireJemlaCom(
                    relative_M1produit = relative_M1produit,
                    isExpanded = expandState.isExpanded,
                )

                Big_Principale_AppEcranPresntoireJemlaCom(
                    big_presenter_couleur_produit = expandState.bigPresenterCouleur,
                    expandState = expandState,
                    onImageTap = onImageTapWithWifi,  // FIX: was missing, caused compile error
                )

                if (relativeList_M3ColorsProduit.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (expandState.isExpanded) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            maxItemsInEachRow = 4
                        ) {
                            relativeList_M3ColorsProduit.forEachIndexed { index, couleur ->
                                if (index != expandState.bigPresenterIndex) {
                                    SubColorCard_WithButton_app2(
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        expandState = expandState,
                                        isExpanded = true,
                                        modifier = Modifier.weight(1f, fill = false),
                                        onTap = { onImageTapWithWifi(couleur) },  // FIX: was missing
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            relativeList_M3ColorsProduit.forEachIndexed { index, couleur ->
                                if (index != expandState.bigPresenterIndex) {
                                    SubColorCard_WithButton_app2(
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        expandState = expandState,
                                        isExpanded = false,
                                        modifier = Modifier.fillMaxWidth(),
                                        onTap = { onImageTapWithWifi(couleur) },  // FIX: was missing
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
