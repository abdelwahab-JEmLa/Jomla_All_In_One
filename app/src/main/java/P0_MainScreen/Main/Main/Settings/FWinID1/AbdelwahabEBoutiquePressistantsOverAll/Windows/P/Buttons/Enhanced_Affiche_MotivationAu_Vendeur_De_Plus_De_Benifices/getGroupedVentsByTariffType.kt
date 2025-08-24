package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

fun getGroupedVentsByTariffType(
    ventOperations: List<M10OperationVentCouleur>,
    allProducts: List<ArticlesBasesStatsTable>,
    tariffRepo: Repo13TarificationInfos
): List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> {
    val groupedByTariffType = mutableMapOf<TypeChoisi, MutableList<ArticlesBasesStatsTable>>()

    ventOperations.forEach { ventOperation ->
        val product = allProducts.find { it.keyID == ventOperation.parent_M1Produit_KeyId }

        if (product != null) {
            val tariffType = if (ventOperation.parentM13TarificationKeyID != "null") {
                tariffRepo.datasValue
                    .find { it.keyID == ventOperation.parentM13TarificationKeyID }
                    ?.typeChoisi ?: ventOperation.typeTarificationEnumT2
            } else {
                ventOperation.typeTarificationEnumT2
            }

            groupedByTariffType.getOrPut(tariffType) { mutableListOf() }.add(product)
        }
    }

    return groupedByTariffType.entries.sortedByDescending { entry ->
        when (entry.key) {
            TypeChoisi.LeMaxPrixArrive -> 4
            TypeChoisi.Prix_Detaille -> 3
            TypeChoisi.Edited_Pour_Client -> 2
            TypeChoisi.Historique -> 1
            else -> 0
        }
    }
}

@Composable
fun Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    modifier: Modifier = Modifier
) {
    val onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent =
        focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent

    val repositorysMainGetter = aCentralFacade.repositorysMainGetter

    val tariffication_ListGroupedVentsParProduit: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> =
        getGroupedVentsByTariffType(
            ventOperations = onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent,
            allProducts = repositorysMainGetter.repo1ProduitInfos.datasValue,
            tariffRepo = repositorysMainGetter.repo13TarificationInfos
        )

    val totalProducts = tariffication_ListGroupedVentsParProduit.sumOf { it.value.size }
    val totalRevenue = tariffication_ListGroupedVentsParProduit.sumOf { (_, products) ->
        products.sumOf { it.prixVent }
    }

    val profitabilityAnalysis = M13TarificationInfos.analyzeSalesDistribution(
        tariffication_ListGroupedVentsParProduit
    )

    Column(modifier = modifier) {
        EnhancedTotalDisplayCard(
            totalProducts = totalProducts,
            totalRevenue = totalRevenue,
            profitabilityAnalysis = profitabilityAnalysis
        )

        EnhancedTariffTypeSalesDisplay(
            groupedSales = tariffication_ListGroupedVentsParProduit,
            showLabels = true
        )
    }
}

@Composable
private fun EnhancedTariffTypeSalesDisplay(
    groupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>,
    showLabels: Boolean
) {
    val processedSales = processHistoriqueSales(groupedSales)

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            processedSales.forEach { (tariffType, products) ->
                EnhancedTariffTypeRow(
                    tariffType = tariffType,
                    productCount = products.size,
                    products = products,
                    showLabels = showLabels,
                    allGroupedSales = groupedSales
                )
            }
        }
    }
}

private fun processHistoriqueSales(
    groupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>
): List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> {
    val resultMap = mutableMapOf<TypeChoisi, MutableList<ArticlesBasesStatsTable>>()

    val allowedTypes = setOf(
        TypeChoisi.Prix_Detaille,
        TypeChoisi.Prix_SupperGro_Et_PresentationService
    )

    groupedSales.filter { it.key in allowedTypes }.forEach { (type, products) ->
        resultMap.getOrPut(type) { mutableListOf() }.addAll(products)
    }

    groupedSales.filter { it.key !in allowedTypes }.forEach { (_, products) ->
        if (products.isNotEmpty()) {
            val targetType = findClosestPriceType(products, groupedSales)
            resultMap.getOrPut(targetType) { mutableListOf() }.addAll(products)
        }
    }

    return resultMap.entries.toList().sortedByDescending { entry ->
        when (entry.key) {
            TypeChoisi.Prix_Detaille -> 2
            TypeChoisi.Prix_SupperGro_Et_PresentationService -> 1
            else -> 0
        }
    }
}

@Composable
private fun EnhancedTariffTypeRow(
    tariffType: TypeChoisi,
    productCount: Int,
    products: List<ArticlesBasesStatsTable>,
    showLabels: Boolean,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> = emptyList()
) {
    val totalValue = products.sumOf { it.prixVent }
    var showProductDialog by remember { mutableStateOf(false) }

    if (showProductDialog) {
        ProductsListDialog(
            tariffType = tariffType,
            products = products,
            onDismiss = { showProductDialog = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { showProductDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = tariffType.couleur.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tariffType.iconVector?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = tariffType.nomArabe.ifBlank { tariffType.name },
                            modifier = Modifier.size(24.dp),
                            tint = tariffType.couleur_Text
                        )
                    }

                    if (showLabels) {
                        Column {
                            Text(
                                text = tariffType.nomArabe.ifBlank { tariffType.name },
                                style = MaterialTheme.typography.bodyMedium,
                                color = tariffType.couleur_Text,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${totalValue.toInt()} دج",
                                style = MaterialTheme.typography.bodySmall,
                                color = tariffType.couleur_Text.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Badge(
                    containerColor = tariffType.couleur,
                    contentColor = tariffType.couleur_Text
                ) {
                    Text(
                        text = productCount.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = getEnhancedMotivationalMessage(tariffType, productCount, allGroupedSales),
                style = MaterialTheme.typography.bodySmall,
                color = tariffType.couleur_Text.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

private fun findClosestPriceType(
    historicalProducts: List<ArticlesBasesStatsTable>,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>
): TypeChoisi {
    if (historicalProducts.isEmpty()) return TypeChoisi.Prix_Detaille

    val avgHistoricalPrice = historicalProducts.map { it.prixVent }.average()

    val prixDetailleProducts = allGroupedSales.find { it.key == TypeChoisi.Prix_Detaille }?.value
    val prixSupperGroProducts = allGroupedSales.find { it.key == TypeChoisi.Prix_SupperGro_Et_PresentationService }?.value

    val avgPrixDetaille = prixDetailleProducts?.map { it.prixVent }?.average()
        ?: (historicalProducts.first().prixVent * 1.2)

    val avgPrixSupperGro = prixSupperGroProducts?.map { it.prixVent }?.average()
        ?: (historicalProducts.first().prixVent * 0.8)

    val midPoint = (avgPrixSupperGro + avgPrixDetaille) / 2

    return if (avgHistoricalPrice <= midPoint) {
        TypeChoisi.Prix_SupperGro_Et_PresentationService
    } else {
        TypeChoisi.Prix_Detaille
    }
}

private fun getEnhancedMotivationalMessage(
    tariffType: TypeChoisi,
    count: Int,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> = emptyList()
): String {
    return when (tariffType) {
        TypeChoisi.Prix_Detaille -> "⭐ ممتاز! $count ${get_BestNomArabDuPlurieul(count)} بسعر التجزئة - ربح جيد!"
        TypeChoisi.Prix_SupperGro_Et_PresentationService -> "⚠️ تحذير: $count ${get_BestNomArabDuPlurieul(count)} بسعر الجملة - ربح منخفض، حاول رفع السعر للتجزئة"
        else -> "🔍 $count ${get_BestNomArabDuPlurieul(count)} بهذا السعر - راجع التسعيرة"
    }
}
