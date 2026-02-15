package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices.Affich.AffichePresentedCatalogues
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
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

@Composable
fun Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    modifier: Modifier = Modifier,
) {
    val ventOperations = focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter

    val groupedSales = getGroupedVentsByTariffType(
        aCentralFacade,
        ventOperations,
        repositorysMainGetter.repo1ProduitInfos.datasValue,
        repositorysMainGetter.repo13TarificationInfos
    )

    val totalProducts = groupedSales.sumOf { it.value.size }
    val totalRevenue = groupedSales.sumOf { (_, pairs) -> pairs.sumOf { it.first.prixVent } }
    val profitabilityAnalysis = M13TarificationInfos.analyzeSalesDistribution(
        groupedSales.map { entry ->
            object : Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>> {
                override val key = entry.key
                override val value = entry.value.map { it.first }
            }
        }
    )
    val focused_M1ProduitInfos_Pour_PrixDifineur = aCentralFacade
        .focusedActiveValuesFacade.focusedValuesGetter.focused_M1ProduitInfos_Pour_PrixDifineur == null

    Column(modifier = modifier) {
        (focused_M1ProduitInfos_Pour_PrixDifineur && !focusedValuesGetter.currentApp_ItsWorkChezGrossisst).ifTrue {
            AffichePresentedCatalogues()
            EnhancedTotalDisplayCard(totalProducts, totalRevenue, profitabilityAnalysis)
        }
        (totalRevenue > 0).ifTrue {
            EnhancedTariffTypeSalesDisplay(groupedSales)
        }
    }
}


fun getGroupedVentsByTariffType(
    aCentralFacade: ACentralFacade,
    ventOperations: List<M10OperationVentCouleur>,
    allProducts: List<ArticlesBasesStatsTable>,
    tariffRepo: Repo13TarificationInfos
): List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>> {
    val allowedTypes =
        setOf(TypeChoisi.Prix_Detaille, TypeChoisi.Prix_SupperGro_Et_PresentationService)
    val initialGroups =
        mutableMapOf<TypeChoisi, MutableList<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>()

    ventOperations
        .filter { it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve }
        .forEach { ventOperation ->
            allProducts.find { it.keyID == ventOperation.parent_M1Produit_KeyId }?.let { product ->
                val existing_Prix_Detaille_Du_Produit =
                    find_existing_Prix_Detaille_Du_Produit(aCentralFacade, product)

                val tariffInfo = if (ventOperation.parentM13TarificationKeyID != "null") {
                    tariffRepo.datasValue.find { it.keyID == ventOperation.parentM13TarificationKeyID }
                        ?: createDefaultTariffInfo(ventOperation, product)
                } else {
                    createDefaultTariffInfo(ventOperation, product)
                }

                // If no existing retail price found, force the product to use Prix_Detaille
                val adjustedTariffInfo = when {
                    existing_Prix_Detaille_Du_Produit == null -> {
                        // No existing retail price found, set to Prix_Detaille
                        tariffInfo.copy(
                            typeChoisi = TypeChoisi.Prix_Detaille,
                            prixCurrency = if (product.prixVent > 0) product.prixVent else tariffInfo.prixCurrency
                        )
                    }

                    tariffInfo.typeChoisi == TypeChoisi.Prix_SupperGro_Et_PresentationService && product.prixVent == 0.0 -> {
                        tariffInfo.copy(typeChoisi = TypeChoisi.Prix_Detaille)
                    }

                    else -> tariffInfo
                }

                if (adjustedTariffInfo.prixCurrency > 0) {
                    initialGroups.getOrPut(adjustedTariffInfo.typeChoisi) { mutableListOf() }
                        .add(Pair(product, adjustedTariffInfo))
                }
            }
        }

    val finalGroups =
        mutableMapOf<TypeChoisi, MutableList<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>()

    initialGroups.filter { it.key in allowedTypes }.forEach { (type, pairs) ->
        finalGroups.getOrPut(type) { mutableListOf() }.addAll(pairs)
    }

    initialGroups.filter { it.key !in allowedTypes }.forEach { (_, pairs) ->
        if (pairs.isNotEmpty()) {
            val targetType = findClosestPriceType(pairs.map { it.first }, initialGroups)
            val updatedPairs = pairs.map { (product, tariffInfo) ->
                Pair(product, tariffInfo.copy(typeChoisi = targetType))
            }
            finalGroups.getOrPut(targetType) { mutableListOf() }.addAll(updatedPairs)
        }
    }

    return finalGroups.entries.sortedByDescending { entry ->
        when (entry.key) {
            TypeChoisi.Prix_Detaille -> 2
            TypeChoisi.Prix_SupperGro_Et_PresentationService -> 1
            else -> 0
        }
    }
}

// Helper function to find existing retail price for a product
fun find_existing_Prix_Detaille_Du_Produit(
    aCentralFacade: ACentralFacade,
    product: ArticlesBasesStatsTable
): M13TarificationInfos? {
    return aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
        .lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == product.keyID
        }
}

@Composable
private fun EnhancedTariffTypeSalesDisplay(
    groupedSales: List<Map.Entry<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.9f
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column() {
            groupedSales.forEach { (tariffType, pairs) ->
                EnhancedTariffTypeRow(tariffType, pairs)
            }
        }
    }
}

@Composable
private fun EnhancedTariffTypeRow(
    tariffType: TypeChoisi,
    productTariffPairs: List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ProductsListDialog(tariffType, productTariffPairs) { showDialog = false }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(
            // FIXED: Using tariffType.couleur for background with good visibility
            containerColor = when (tariffType) {
                TypeChoisi.Prix_Detaille -> tariffType.couleur.copy(alpha = 0.4f)
                TypeChoisi.Prix_SupperGro_Et_PresentationService -> tariffType.couleur.copy(alpha = 0.1f)
                else -> tariffType.couleur.copy(alpha = 0.3f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            // FIXED: Using tariffType.couleur_Text for icon tint
                            tint = tariffType.couleur_Text
                        )
                    }
                    Text(
                        text = tariffType.nomArabe.ifBlank { tariffType.name },
                        style = MaterialTheme.typography.bodyMedium,
                        // FIXED: Using tariffType.couleur_Text for text color
                        color = tariffType.couleur_Text,
                        fontWeight = FontWeight.Medium
                    )
                }

                Badge(
                    // FIXED: Using tariffType.couleur for badge background
                    containerColor = tariffType.couleur,
                    // FIXED: Using tariffType.couleur_Text for badge content
                    contentColor = tariffType.couleur_Text
                ) {
                    Text(
                        text = productTariffPairs.size.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun createDefaultTariffInfo(
    ventOperation: M10OperationVentCouleur,
    product: ArticlesBasesStatsTable
): M13TarificationInfos {
    return M13TarificationInfos(
        typeChoisi = ventOperation.typeTarificationEnumT2,
        parent_M1Produit_KeyId = product.keyID,
        parent_M1Produit_DebugInfos = product.getDebugInfos(),
        prixCurrency = product.prixVent
    )
}

private fun findClosestPriceType(
    products: List<ArticlesBasesStatsTable>,
    allGroups: Map<TypeChoisi, List<Pair<ArticlesBasesStatsTable, M13TarificationInfos>>>
): TypeChoisi {
    if (products.isEmpty()) return TypeChoisi.Prix_Detaille

    val avgPrice = products.map { it.prixVent }.average()

    val retailAvg = allGroups[TypeChoisi.Prix_Detaille]?.map { it.first.prixVent }?.average()
        ?: (products.first().prixVent * 1.2)

    val wholesaleAvg =
        allGroups[TypeChoisi.Prix_SupperGro_Et_PresentationService]?.map { it.first.prixVent }
            ?.average()
            ?: (products.first().prixVent * 0.8)

    val midPoint = (wholesaleAvg + retailAvg) / 2

    return if (avgPrice <= midPoint) {
        TypeChoisi.Prix_SupperGro_Et_PresentationService
    } else {
        TypeChoisi.Prix_Detaille
    }
}
