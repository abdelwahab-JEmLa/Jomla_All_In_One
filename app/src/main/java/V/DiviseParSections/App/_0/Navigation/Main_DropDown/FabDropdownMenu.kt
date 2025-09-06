package V.DiviseParSections.App._0.Navigation.Main_DropDown

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App._0.Navigation.DropDownItem_1
import V.DiviseParSections.App._0.Navigation.DropDownItem_2
import V.DiviseParSections.App._0.Navigation.DropDownItem_DisplayeGpsFlowFAB
import V.DiviseParSections.App._0.Navigation.DropDownItem_Displaye_TogleFilterMarquers
import V.DiviseParSections.App._0.Navigation.Item_States
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun FabDropdownMenu(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,

    showFabDropdown: Boolean,
    onDismissDropdown: () -> Unit,
    repo8BonVent: Repo8BonVent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = showFabDropdown,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            //  DropDownItem_FABs_AddClient()

            DropDownItem_Displaye_TogleFilterMarquers()

            DropDownItem_DisplayeGpsFlowFAB()

            DropDownItem_2(
                item_States = Item_States.get_Default()
                    .copy(
                        icon_imageVector = Icons.Default.Receipt,
                        function_noms_separatedStrings = "refresh_Datas(),تحديث تقارير المبيعات",
                        time_pressing_millis = 1500
                    ),
                onDismissDropdown = onDismissDropdown,
                onExecute = {
                    repo8BonVent.refresh_Datas()
                    repositorysMainGetter.repo10OperationVentCouleur.refresh_Datas()
                    repositorysMainGetter.repo1ProduitInfos.refresh_Datas()
                }
            )

            DropDownItem_1(
                viewModel = koinInject(),
                nomFun = "Toggle Client Button",
                onDismissDropdown = onDismissDropdown
            )
        }
    }
}

