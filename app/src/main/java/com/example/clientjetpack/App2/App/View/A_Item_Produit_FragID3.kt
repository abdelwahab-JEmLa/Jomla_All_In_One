package com.example.clientjetpack.App2.App.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import com.example.clientjetpack.App2.App.View.Components.Big_Principale_AppEcranPresntoireJemlaCom
import com.example.clientjetpack.App2.App.View.Components.SubColorCard_WithButton_app2
import com.example.clientjetpack.App2.App.View.ViewS.Compact_Header_AppEcranPresntoireJemlaCom
import org.koin.compose.koinInject

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_AppEcranPresntoireJemlaCom(
    relative_M1ProduitToListM3Couleur: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
    focusedValuesGetter_app2: FocusedValuesGetter_app2 = koinInject(),
) {
    val expanded_M1Produit = focusedValuesGetter_app2.active_Central_Values.expanded_M1Produit
    val expanded_M3CouleurProduitInfos = focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos
    val relative_M1produit = relative_M1ProduitToListM3Couleur.first
    val relativeList_M3ColorsProduit = relative_M1ProduitToListM3Couleur.second
    val modifier = Modifier

    val isThisProductExpanded = remember(expanded_M1Produit) {
        expanded_M1Produit?.keyID == relative_M1ProduitToListM3Couleur.first.keyID
    }

    val initialColorIndex = remember(expanded_M3CouleurProduitInfos, relativeList_M3ColorsProduit) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relativeList_M3ColorsProduit
                )
                if (matchingIndex != -1) matchingIndex else 0
            } else 0
        } ?: 0
    }

    // TODO(1) FIXED: big_presenter_couleur_produit is an Int index into relativeList_M3ColorsProduit,
    // initialised to initialColorIndex (which is already correctly computed above).
    var big_presenter_couleur_produit by remember(initialColorIndex) {
        mutableStateOf(initialColorIndex)
    }

    // TODO(FIXME) FIXED: LaunchedEffect only updates the index state.
    // The Column / Card composables are placed OUTSIDE the LaunchedEffect lambda
    // so they run in the correct @Composable context.
    LaunchedEffect(expanded_M3CouleurProduitInfos, relativeList_M3ColorsProduit) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relativeList_M3ColorsProduit
                )
                if (matchingIndex != -1 && matchingIndex != big_presenter_couleur_produit) {
                    big_presenter_couleur_produit = matchingIndex
                }
            }
        }
    } // <-- LaunchedEffect ends here; UI composables live below, in the @Composable scope

    val cardPadding = if (isThisProductExpanded) 8.dp else 4.dp
    val innerPadding = if (isThisProductExpanded) 8.dp else 4.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(cardPadding)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isThisProductExpanded) 8.dp else 4.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                Compact_Header_AppEcranPresntoireJemlaCom(
                    relative_M1produit = relative_M1produit,
                    isExpanded = isThisProductExpanded,
                    modifier = modifier
                )

                // TODO(FIXME) FIXED: Big_Principale_AppEcranPresntoireJemlaCom expects
                // M3CouleurProduitInfos, not an Int. Resolve the colour object from the
                // index before passing it in.
                Big_Principale_AppEcranPresntoireJemlaCom(
                    relative_M1ProduitToItListM3Couleur = relative_M1ProduitToListM3Couleur,
                    big_presenter_couleur_produit = relativeList_M3ColorsProduit.getOrNull(big_presenter_couleur_produit)
                        ?: relativeList_M3ColorsProduit.first(),
                )

                // FIXED: Sub-colors now visible in all modes (removed isHostPhone condition)
                if (relativeList_M3ColorsProduit.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isThisProductExpanded) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            maxItemsInEachRow = 4
                        ) {
                            relativeList_M3ColorsProduit.forEachIndexed { index, couleur ->
                                if (index != big_presenter_couleur_produit) {
                                    SubColorCard_WithButton_app2(
                                        relative_M1ProduitToItListM3Couleur = relative_M1ProduitToListM3Couleur,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
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
                                if (index != big_presenter_couleur_produit) {
                                    // TODO(FIXME) FIXED: removed stray extra comma,
                                    // and added the required relative_M1ProduitToItListM3Couleur argument.
                                    SubColorCard_WithButton_app2(
                                        relative_M1ProduitToItListM3Couleur = relative_M1ProduitToListM3Couleur,
                                        couleur = couleur,
                                        relative_M1produit = relative_M1produit,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // TODO(FIXME) "Expecting '}'" FIXED: all braces now properly balanced because
    // the Column/Card block is outside LaunchedEffect, closing the function correctly.
}
