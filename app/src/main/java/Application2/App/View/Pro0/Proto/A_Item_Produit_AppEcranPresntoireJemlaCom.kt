package Application2.App.View.Pro0.Proto

import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application2.App.Fragment.ViewModel.ViewModel_MainFragment
import Application2.App.View.Pro0.Proto.Components.Big_Principale_AppEcranPresntoireJemlaCom
import Application2.App.View.Pro0.Proto.Components.ProduitExpandState
import Application2.App.View.Pro0.Proto.Components.SubColorCard_WithButton_app2
import Application2.App.View.Pro0.Proto.ViewS.Compact_Header_AppEcranPresntoireJemlaCom
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

private const val DBG_TAG_ITEM  = "TargetedM3_Affichage"
private const val DBG_M3_KEY    = "-OWDMIC_UdVXmSNw-Dz0"
private const val DBG_M3_PARENT = "-OV3rmZ-9sy3P5rnINL3"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_AppEcranPresntoireJemlaCom(
    relative_M1ProduitToListM3Couleur: Pair<M01Produit, List<M3CouleurProduitInfos>>,
    focusedValuesGetter_app2: RepositorysMainGetter_app2 = koinInject(),
    viewModel: ViewModel_MainFragment,
) {
    val relative_M1produit = relative_M1ProduitToListM3Couleur.first
    val relativeList_M3ColorsProduit = relative_M1ProduitToListM3Couleur.second

    val expandState = remember(relative_M1produit.keyID, relativeList_M3ColorsProduit) {
        ProduitExpandState(
            relative_M1produit = relative_M1produit,
            relativeList_M3ColorsProduit = relativeList_M3ColorsProduit,
            focusedValuesGetter = focusedValuesGetter_app2,
        )
    }

    // ── DEBUG: log targeted M3 render state after expandState is ready ─────────
    if (relative_M1produit.keyID == DBG_M3_PARENT) {
        val targeted = relativeList_M3ColorsProduit.find { it.keyID == DBG_M3_KEY }
        Log.d(DBG_TAG_ITEM, "[Item render] productKeyID=${relative_M1produit.keyID}" +
                " | isExpanded=${expandState.isExpanded}" +
                " | bigPresenterIndex=${expandState.bigPresenterIndex}" +
                " | cardElevation=${expandState.cardElevation}" +
                " | targetedM3 present=${targeted != null}" +
                " | targetedM3 img=${targeted?.nomImageFichieSansEtansion}" +
                " | bigPresenterCouleur keyID=${expandState.bigPresenterCouleur?.keyID}")
    }

    LaunchedEffect(focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos) {
        expandState.syncFromFocusedValues(
            focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos
        )
        // ── DEBUG: log sync of targeted M3 expansion ──────────────────────────
        if (relative_M1produit.keyID == DBG_M3_PARENT) {
            val focusedM3 = focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos
            Log.d(DBG_TAG_ITEM, "[Item syncFromFocused] productKeyID=${relative_M1produit.keyID}" +
                    " | focusedM3 keyID=${focusedM3?.keyID}" +
                    " | isTargeted=${focusedM3?.keyID == DBG_M3_KEY}" +
                    " | isExpanded after sync=${expandState.isExpanded}")
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
                    viewModel   =viewModel,
                    big_presenter_couleur_produit = expandState.bigPresenterCouleur,
                    expandState = expandState,
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
                                        viewModel   =viewModel,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        expandState = expandState,
                                        isExpanded = true,
                                        modifier = Modifier.weight(1f, fill = false),
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
                                        viewModel   =viewModel,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                        expandState = expandState,
                                        modifier = Modifier.fillMaxWidth(),
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
