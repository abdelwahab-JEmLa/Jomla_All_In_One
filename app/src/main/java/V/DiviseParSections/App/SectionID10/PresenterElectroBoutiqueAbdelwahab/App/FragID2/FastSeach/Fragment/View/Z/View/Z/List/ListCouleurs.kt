package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.AddNewCouleur
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.ViewVentCouleur_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ListCouleurs(
    produitWithColors: Pair<M01Produit, List<M3CouleurProduitInfos>>,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val produit = produitWithColors.first
    val colors = produitWithColors.second

    // Check if grid mode is enabled to apply RTL consistently
    val affiche_Produit_OnGrid = ActiveCentralValues.get_Default().affiche_Produit_OnGrid

    val content = @Composable {
        LazyRow(
            modifier = Modifier
                .getSemanticsTag(produitWithColors, "")
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(colors, key = { it.keyID }) { color ->
                ViewVentCouleur_T1(
                    modifier = Modifier.padding(4.dp),
                    relative_M3CouleurInfos = color,
                    relative_produit = produit,
                    viewModel = viewModel,
                    size = 120.dp
                )
            }
            item {
                AddNewCouleur(
                    modifier = Modifier.padding(4.dp),
                    produit = produit,
                    viewModel = viewModel,
                    size = 120.dp
                )
            }
        }
    }

    // Apply RTL direction when grid mode is enabled for consistency
    if (affiche_Produit_OnGrid) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    } else {
        content()
    }
}
