package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.MainList_Frag_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.ViewProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun MainListT1(
    modifier: Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    searchFilter: String,
    sortedProducts: List<ArticlesBasesStatsTable>,
    on_Pour_FocuceAfficheClavieSearcherProduit: () -> Unit = {},
    isCartonEditMode: Boolean,
    on_PourEntre_EditeMode: (Boolean) -> Unit = {},  // FIXED: Accept Boolean parameter
    ) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (searchFilter.isNotEmpty() && sortedProducts.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
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
                    on_Pour_FocuceAfficheClavieSearcherProduit = on_Pour_FocuceAfficheClavieSearcherProduit,
                    isCartonEditMode = isCartonEditMode,
                    on_PourEntre_EditeMode = on_PourEntre_EditeMode  // FIXED: Pass through directly
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
