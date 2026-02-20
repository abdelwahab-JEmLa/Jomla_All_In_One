package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.MainList_Frag_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.ViewProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.M01Produit
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.koin.compose.koinInject

@Composable
fun MainListT1(
    modifier: Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    searchFilter: String,
    sortedProducts: List<M01Produit>,
    searchFieldFocusRequester: FocusRequester? = null,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    cartonEditModeProductId: String? = null,
    boitEditModeProductId: String? = null,
    on_PourEntre_CartonEditeMode: (String?) -> Unit = {},
    on_PourEntre_BoitEditeMode: (String?) -> Unit = {},
) {
    val affiche_Produit_OnGrid = ActiveCentralValues.get_Default().affiche_Produit_OnGrid

    if (affiche_Produit_OnGrid) {
        // Grid layout with 2 columns, RTL direction (right to left)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(petitePaddine),
                horizontalArrangement = Arrangement.spacedBy(petitePaddine)
            ) {
                if (searchFilter.isNotEmpty() && sortedProducts.isEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(petitePaddine),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Aucun produit trouvé pour \"$searchFilter\"",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                } else {
                    items(sortedProducts) { product ->
                        ViewProduit_T1(
                            product = product,
                            searchFieldFocusRequester = searchFieldFocusRequester,
                            on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit,
                            isCartonEditMode = cartonEditModeProductId == product.keyID,
                            isBoitEditMode = boitEditModeProductId == product.keyID,
                            on_PourEntre_CartonEditeMode = { isEditing ->
                                on_PourEntre_CartonEditeMode(if (isEditing) product.keyID else null)
                            },
                            on_PourEntre_BoitEditeMode = { isEditing ->
                                on_PourEntre_BoitEditeMode(if (isEditing) product.keyID else null)
                            }
                        )
                    }
                }

                if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                    item {
                        MainList_Frag_Panie(its_From_SearchPrd = true)
                    }
                }
            }
        }
    } else {
        // List layout (original)
        LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(petitePaddine)) {
            if (searchFilter.isNotEmpty() && sortedProducts.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(petitePaddine),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aucun produit trouvé pour \"$searchFilter\"",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            } else {
                items(sortedProducts) { product ->
                    ViewProduit_T1(
                        product = product,
                        searchFieldFocusRequester = searchFieldFocusRequester,
                        on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit,
                        isCartonEditMode = cartonEditModeProductId == product.keyID,
                        isBoitEditMode = boitEditModeProductId == product.keyID,
                        on_PourEntre_CartonEditeMode = { isEditing ->
                            on_PourEntre_CartonEditeMode(if (isEditing) product.keyID else null)
                        },
                        on_PourEntre_BoitEditeMode = { isEditing ->
                            on_PourEntre_BoitEditeMode(if (isEditing) product.keyID else null)
                        }
                    )
                }
            }

            if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                item {
                    MainList_Frag_Panie(its_From_SearchPrd = true)
                }
            }
        }
    }
}
