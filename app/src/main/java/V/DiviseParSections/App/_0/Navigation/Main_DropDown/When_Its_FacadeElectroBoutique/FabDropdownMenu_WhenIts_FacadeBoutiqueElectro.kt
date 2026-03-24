package V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.FilterState_Facad_Boutique_FragId5
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.FilterTunnel
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.Filter.GroupTunnel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.UploadFilteredData_DropdownMenuItem.View.UploadFilteredData_DropdownMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    onClickImageToShowControles: () -> Unit,
) {
    val activeCentralValues = focusedValuesGetter.active_Central_Values
    val filterState_Facad_Boutique_FragId5 = activeCentralValues.filterState_Facad_Boutique_FragId5 ?: FilterState_Facad_Boutique_FragId5()

    val allCategories = repositorysMainGetter.repoM16CategorieProduit.datasValue
    val allProducts = repositorysMainGetter.repoM1Produit.datasValue
    val allColors = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
        .sortedByDescending { it.creationTimestamp }

    val groupe_Par_Catalogue = GroupTunnel(
        allColors = allColors,
        allProducts = allProducts,
        allCategories = allCategories
    )

    val filteredCatalogues = FilterTunnel(
        groupe_Par_Catalogue = groupe_Par_Catalogue,
        catalogueFilter = focusedValuesGetter.currentActive_M9AppCompt
            ?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId,
        filterState = filterState_Facad_Boutique_FragId5
    )
    Box(modifier = modifier) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
        ) {

            // Button: open filter/sort dialog
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = "Filtres et tri",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = {
                    focusedValuesGetter.update_activeCentralValues(
                        activeCentralValues.copy(
                            filterState_Facad_Boutique_FragId5 = filterState_Facad_Boutique_FragId5.copy(
                                affiche_dialog_editeur = true
                            )
                        )
                    )
                    onDismissDropdown()
                }
            )
            Fab_Stigns(onClickImageToShowControles, onDismissDropdown)
            UploadFilteredData_DropdownMenuItem(
                groupe_Par_Catalogue = filteredCatalogues,
                onDismissDropdown = onDismissDropdown,
            )
        }
    }
}

@Composable
fun Fab_Stigns(
    onClickImageToShowControles: () -> Unit,
    onDismissDropdown: () -> Unit,
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = "onClickImageToShowControles",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = {
            onClickImageToShowControles()
            onDismissDropdown()
            focusedValuesGetter.update_oneMutableStateLesseRessources(true)
        }
    )
}
