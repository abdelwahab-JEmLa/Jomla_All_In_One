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
): List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>> {
    val allowedTypes = setOf(
        TypeChoisi.Prix_Detaille,
        TypeChoisi.Prix_SupperGro_Et_PresentationService
    )

    val initialGroupedByTariffType = mutableMapOf<TypeChoisi, MutableList<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>()

    ventOperations.forEach { ventOperation ->
        val product = allProducts.find { it.keyID == ventOperation.parent_M1Produit_KeyId }

        if (product != null) {
            val tariffInfo: M13TarificationInfos = if (ventOperation.parentM13TarificationKeyID != "null") {
                tariffRepo.datasValue.find { it.keyID == ventOperation.parentM13TarificationKeyID }
                    ?: M13TarificationInfos(
                        typeChoisi = ventOperation.typeTarificationEnumT2,
                        parent_M1Produit_KeyId = product.keyID,
                        parent_M1Produit_DebugInfos = product.getDebugInfos(),
                        prixCurrency = product.prixVent
                    )
            } else {
                M13TarificationInfos(
                    typeChoisi = ventOperation.typeTarificationEnumT2,
                    parent_M1Produit_KeyId = product.keyID,
                    parent_M1Produit_DebugInfos = product.getDebugInfos(),
                    prixCurrency = product.prixVent
                )
            }

            val tariffType = tariffInfo.typeChoisi
            val productTariffPair = Pair(product, tariffInfo)
            initialGroupedByTariffType.getOrPut(tariffType) { mutableListOf() }.add(productTariffPair)
        }
    }

    val finalGroupedByTariffType = mutableMapOf<TypeChoisi, MutableList<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>()

    // Add allowed types directly
    initialGroupedByTariffType.filter { it.key in allowedTypes }.forEach { (type, productTariffPairs) ->
        finalGroupedByTariffType.getOrPut(type) { mutableListOf() }.addAll(productTariffPairs)
    }

    // Process non-allowed types and assign them to closest match
    initialGroupedByTariffType.filter { it.key !in allowedTypes }.forEach { (originalType, productTariffPairs) ->
        if (productTariffPairs.isNotEmpty()) {
            val products = productTariffPairs.map { it.first }
            val targetType = findClosestPriceType(products, initialGroupedByTariffType.entries.toList().map {
                it.key to it.value.map { pair -> pair.first }
            })

            // Update tariff info with new type and add to final grouped result
            val updatedPairs = productTariffPairs.map { (product, tariffInfo) ->
                Pair(product, tariffInfo.copy(typeChoisi = targetType))
            }
            finalGroupedByTariffType.getOrPut(targetType) { mutableListOf() }.addAll(updatedPairs)
        }
    }

    return finalGroupedByTariffType.entries.toList().sortedByDescending { entry ->
        when (entry.key) {
            TypeChoisi.Prix_Detaille -> 2
            TypeChoisi.Prix_SupperGro_Et_PresentationService -> 1
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

    val tariffication_ListGroupedVentsParProduit: List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>> =
        getGroupedVentsByTariffType(
            ventOperations = onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent,
            allProducts = repositorysMainGetter.repo1ProduitInfos.datasValue,
            tariffRepo = repositorysMainGetter.repo13TarificationInfos
        )

    val totalProducts = tariffication_ListGroupedVentsParProduit.sumOf { it.value.size }
    val totalRevenue = tariffication_ListGroupedVentsParProduit.sumOf { (_, productTariffPairs) ->
        productTariffPairs.sumOf { it.first.prixVent }
    }

    val profitabilityAnalysis = M13TarificationInfos.analyzeSalesDistribution(
        tariffication_ListGroupedVentsParProduit.map { entry ->
            object : Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>> {
                override val key = entry.key
                override val value = entry.value.map { pair -> pair.first }
            }
        }
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
    groupedSales: List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>>,
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
            processedSales.forEach { (tariffType, productTariffPairs) ->
                EnhancedTariffTypeRow(
                    tariffType = tariffType,
                    productCount = productTariffPairs.size,
                    productTariffPairs = productTariffPairs,
                    showLabels = showLabels,
                    allGroupedSales = groupedSales
                )
            }
        }
    }
}

private fun processHistoriqueSales(
    groupedSales: List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>>
): List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>> {
    val resultMap = mutableMapOf<TypeChoisi, MutableList<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>()

    val allowedTypes = setOf(
        TypeChoisi.Prix_Detaille,
        TypeChoisi.Prix_SupperGro_Et_PresentationService
    )

    // Add allowed types directly
    groupedSales.filter { it.key in allowedTypes }.forEach { (type, productTariffPairs) ->
        resultMap.getOrPut(type) { mutableListOf() }.addAll(productTariffPairs)
    }

    // Process non-allowed types
    groupedSales.filter { it.key !in allowedTypes }.forEach { (_, productTariffPairs) ->
        if (productTariffPairs.isNotEmpty()) {
            val products = productTariffPairs.map { it.first }
            val targetType = findClosestPriceType(products, groupedSales.map {
                it.key to it.value.map { pair -> pair.first }
            })

            // Update tariff info with new type
            val updatedPairs = productTariffPairs.map { (product, tariffInfo) ->
                Pair(product, tariffInfo.copy(typeChoisi = targetType))
            }
            resultMap.getOrPut(targetType) { mutableListOf() }.addAll(updatedPairs)
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
    productTariffPairs: List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>,
    showLabels: Boolean,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>> = emptyList()
) {
    val totalValue = productTariffPairs.sumOf { it.first.prixVent }
    var showProductDialog by remember { mutableStateOf(false) }

    if (showProductDialog) {
        ProductsListDialog(
            tariffType = tariffType,
            productTariffPairs = productTariffPairs,
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
        }
    }
}

private fun findClosestPriceType(
    historicalProducts: List<ArticlesBasesStatsTable>,
    allGroupedSales: List<Pair<TypeChoisi, List<ArticlesBasesStatsTable>>>
): TypeChoisi {
    if (historicalProducts.isEmpty()) return TypeChoisi.Prix_Detaille

    val avgHistoricalPrice = historicalProducts.map { it.prixVent }.average()

    val prixDetailleProducts = allGroupedSales.find { it.first == TypeChoisi.Prix_Detaille }?.second
    val prixSupperGroProducts = allGroupedSales.find { it.first == TypeChoisi.Prix_SupperGro_Et_PresentationService }?.second

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
