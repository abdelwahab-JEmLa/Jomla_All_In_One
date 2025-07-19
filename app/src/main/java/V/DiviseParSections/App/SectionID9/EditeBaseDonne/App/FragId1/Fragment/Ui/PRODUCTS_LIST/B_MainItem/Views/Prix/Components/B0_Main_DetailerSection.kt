package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.PriceAndUnitSection
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("UnrememberedMutableState")
@Composable
fun Prix_Detailer_Section(
    modifier: Modifier,
    viewModel: Sec9FragId1ViewId2ViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repo13TarificationInfos: Repo13TarificationInfos = aCentralFacade.repoMainGetter.repo13TarificationInfos,
    produit: ArticlesBasesStatsTable,
    shouldHideQuickInfoCards: Boolean,
    showDetailsExpanded: Boolean,
    onNextField: (() -> Unit)? = null,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    val relative_DefiniParGerant_Tariff by derivedStateOf {
        repo13TarificationInfos.datasValue.lastOrNull {
            it.parent_M1Produit_KeyId == produit.keyID
                    && it.typeChoisi == TypeChoisi.DefiniParGerant
        }
    }

    val isIndividuallyExpanded = viewModel.isProductDetailsExpanded(produit.bsonObjectId)
    val shouldShowDetails = showDetailsExpanded && isIndividuallyExpanded
    var selectedTypeChoisi by remember { mutableStateOf(TypeChoisi.PRIX_BASE) }

    if (shouldShowDetails) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriceAndUnitSection(
                    produit = produit,
                    updateProduct = updateProduct
                )

                TypeChoisiDropdownCard(
                    selectedType = selectedTypeChoisi,
                    onTypeSelected = { newType ->
                        selectedTypeChoisi = newType
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Right Card - Purchase & Profit
                    CardDroitPrixAchatEtBenVendeur(
                        shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                        produit = produit,
                        updateProduct = updateProduct,
                        onNextField = onNextField,
                        modifier = Modifier.weight(1f)
                    )

                    if (!shouldHideQuickInfoCards) {
                        // Left Card - Client Sales
                        CardGauchePrixVentEtBClient(
                            produit = produit,
                            updateProduct = updateProduct,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
