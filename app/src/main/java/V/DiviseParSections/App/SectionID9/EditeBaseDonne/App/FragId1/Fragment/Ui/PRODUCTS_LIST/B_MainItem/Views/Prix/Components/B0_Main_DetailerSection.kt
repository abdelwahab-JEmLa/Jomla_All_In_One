package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.PriceAndUnitSection
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun Prix_Detailer_Section(
    modifier: Modifier,
    viewModel: Sec9FragId1ViewId2ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = viewModel.aCentralFacade.repositorysMainSetter,
    repo13TarificationInfos: Repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
    relative_M1Produit: M01Produit,
    shouldHideQuickInfoCards: Boolean,
    showDetailsExpanded: Boolean,
    onNextField: (() -> Unit)? = null,
    updateProduct: (M01Produit) -> Unit
) {
    // FIXED: Get selectedTypeChoisi from viewModel's uiState
    val uiState by viewModel.uiState.collectAsState()
    val selectedTypeChoisi = uiState.selectedTypeChoisi

    val datasValue = repo13TarificationInfos.datasValue
    val relative_M13Tariffication by derivedStateOf {
        datasValue.lastOrNull {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID
                    && it.typeChoisi == TypeChoisi.Prix_Detaille
        } ?: M13TarificationInfos
            .get_default()
            .copy(
                typeChoisi = TypeChoisi.Prix_Detaille,
                parent_M1Produit_KeyId = relative_M1Produit.keyID,
                parent_M1Produit_DebugInfos = relative_M1Produit.getDebugInfos(),
            )
    }

    val relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff by derivedStateOf {
        val isDefiniParGerantActive = selectedTypeChoisi == TypeChoisi.Prix_Detaille

        val effectiveTariff = relative_M13Tariffication

        Pair(effectiveTariff, isDefiniParGerantActive)
    }

    val isIndividuallyExpanded = viewModel.isProductDetailsExpanded(relative_M1Produit.bsonObjectId)
    val shouldShowDetails = showDetailsExpanded && isIndividuallyExpanded

    if (shouldShowDetails) {
        Surface(
            modifier = Modifier
                .getSemanticsTag(datasValue, "datasValue")
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriceAndUnitSection(
                    produit = relative_M1Produit,
                    updateProduct = updateProduct
                )

                TypeChoisiDropdownCard(
                    relative_M1Produit = relative_M1Produit,
                    relative_M13Tariffication = relative_M13Tariffication,
                    selectedType = selectedTypeChoisi,
                    onTypeSelected = { newType ->
                        // FIXED: Use viewModel method to update selectedTypeChoisi
                        viewModel.updateSelectedTypeChoisi(newType)
                        if (newType == TypeChoisi.Prix_Detaille &&
                            relative_M13Tariffication.prixCurrency > 0
                        ) {
                            updateProduct(
                                relative_M1Produit.copy(
                                    prixVent = relative_M13Tariffication.prixCurrency
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Card_Gauche_PrixAchatEtBenVendeur(
                        modifier = Modifier.weight(1f),
                        produit = relative_M1Produit,
                        relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff = relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff,
                        repositorysMainSetter = repositorysMainSetter,
                        updateProduct = updateProduct,
                        onNextField = onNextField,
                        shouldHideQuickInfoCards = shouldHideQuickInfoCards
                    )

                    if (!shouldHideQuickInfoCards) {
                        Card_Droite_PrixVentEtBClient(
                            modifier = Modifier.weight(1f),
                            repositorysMainSetter,
                            produit = relative_M1Produit,
                            relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff = relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff,
                            updateProduct = updateProduct
                        )
                    }
                }
            }
        }
    }
}
