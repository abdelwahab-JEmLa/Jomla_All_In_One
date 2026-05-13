package Application4.App.Fragment.View.ViewS.Views.Lenceur_Vent_Handler.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Tariffs_MainList(
    modifier: Modifier,
    sortedTariffs: List<M13TarificationInfos>,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_M1produit: M01Produit,
    selectedTariff_Par_AncienProto: M13TarificationInfos,
    compactMode: Boolean,
    onTariffSelected: (M13TarificationInfos) -> Unit,
    tariffsList: List<M13TarificationInfos>,
    tariff_achat_prix: Double,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
) {
    val isEchatillantsMode = uiState_NewProtoPatterns_viewModel.second.active_Datas.filterAffichageMode_Proto== Filter_Affichage_Mode_Proto.Echants_Seulement

    val tariffs_SansModeEditable = sortedTariffs.filter {
        it.typeChoisi != M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                it.typeChoisi != M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable &&
                !(isEchatillantsMode && it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService)
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(tariffs_SansModeEditable, key = { it.keyID }) { tariff ->
            TariffItemSelector(
                tariff = tariff,
                relative_M1produit = relative_M1produit,
                isSelected = tariff.keyID == selectedTariff_Par_AncienProto.keyID,
                compactMode = compactMode,
                onClick = { onTariffSelected(tariff) },
                tariffsList = tariffsList,
                tariff_achat_prix = tariff_achat_prix,
            )
        }

        item(key = "__entre_par_ecriture__") {
            EntreParEcriture_Tariff(
                relative_M1produit = relative_M1produit,
                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                onTariffSelected = onTariffSelected,
                on_update_M13TarificationInfos_par_ecriture= on_update_M13TarificationInfos_par_ecriture,
                compactMode = compactMode,
                isSelected = selectedTariff_Par_AncienProto.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
            )
        }
    }
}
