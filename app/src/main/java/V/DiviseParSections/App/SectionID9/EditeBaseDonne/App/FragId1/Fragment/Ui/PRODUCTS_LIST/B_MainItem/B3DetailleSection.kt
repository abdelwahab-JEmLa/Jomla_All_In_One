package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DetailleSection(
    modifier: Modifier,
    shouldHideQuickInfoCards: Boolean,
    showDetailsExpanded: Boolean,
    onNextField: (() -> Unit)? = null,
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    if (showDetailsExpanded) {
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
                // Unit section with better styling
                PriceAndUnitSection(
                    produit = produit,
                    updateProduct = updateProduct
                )

                // Improved cards layout with better spacing
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

