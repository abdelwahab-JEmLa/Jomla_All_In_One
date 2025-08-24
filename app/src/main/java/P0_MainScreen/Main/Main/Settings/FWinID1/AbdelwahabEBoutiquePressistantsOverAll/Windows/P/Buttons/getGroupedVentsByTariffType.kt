package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

/**
 * Returns the correct Arabic plural form for "product" based on the number
 * Following Arabic grammar rules:
 * - 1 or >=11: منتج (single form)
 * - 2: منتجين (dual form)
 * - 3-10: منتجات (plural form)
 */
fun get_BestNomArabDuPlurieul(nmbr: Int = 0): String {
    return when {
        nmbr == 1 || nmbr >= 11 -> "منتج"
        nmbr == 2 -> "منتجين"
        nmbr in 3..10 -> "منتجات"
        else -> "منتج" // fallback for 0 or negative numbers
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
private fun EnhancedTotalDisplayCard(
    totalProducts: Int,
    totalRevenue: Double,
    profitabilityAnalysis: String
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 إجمالي المبيعات اليوم",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalProducts",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = get_BestNomArabDuPlurieul(totalProducts),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalRevenue.toInt()}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "دج",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = profitabilityAnalysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EnhancedTariffTypeSalesDisplay(
    groupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>,
    showLabels: Boolean
) {
    // Filtrer pour enlever l'affichage de Historique
    val filteredSales = groupedSales.filter { it.key != TypeChoisi.Historique }

    // Si on avait des ventes Historique, suggérer le type le plus proche
    val historiqueSales = groupedSales.find { it.key == TypeChoisi.Historique }

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
            // Afficher les types non-historiques
            filteredSales.forEach { (tariffType, products) ->
                EnhancedTariffTypeRow(
                    tariffType = tariffType,
                    productCount = products.size,
                    products = products,
                    showLabels = showLabels,
                    allGroupedSales = groupedSales
                )
            }

            // Si on avait des ventes historiques, afficher une suggestion
            if (historiqueSales != null && historiqueSales.value.isNotEmpty()) {
                val historicalProducts = historiqueSales.value
                val closestType = findClosestPriceType(historicalProducts, groupedSales)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFA726).copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "💡 اقتراح للتحسين",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFF57C00),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "لديك ${historicalProducts.size} ${get_BestNomArabDuPlurieul(historicalProducts.size)} بأسعار قديمة",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF57C00).copy(alpha = 0.8f)
                        )
                        Text(
                            text = "اقتراح: حول إلى ${closestType.nomArabe} لربح أفضل",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF57C00).copy(alpha = 0.9f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
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
                            contentDescription = null,
                            tint = tariffType.couleur_Text,
                            modifier = Modifier.size(24.dp)
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

/**
 * Trouve le TypeChoisi le plus proche par prix pour remplacer Historique
 */
private fun findClosestPriceType(
    historicalProducts: List<ArticlesBasesStatsTable>,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>>
): TypeChoisi {
    if (historicalProducts.isEmpty()) return TypeChoisi.Prix_Detaille

    val avgHistoricalPrice = historicalProducts.map { it.prixVent }.average()

    val otherTypes = allGroupedSales.filter { it.key != TypeChoisi.Historique }

    if (otherTypes.isEmpty()) return TypeChoisi.Prix_Detaille

    val closestType = otherTypes.minByOrNull { (type, products) ->
        val avgTypePrice = products.map { it.prixVent }.average()
        kotlin.math.abs(avgTypePrice - avgHistoricalPrice)
    }?.key ?: TypeChoisi.Prix_Detaille

    return closestType
}

private fun getEnhancedMotivationalMessage(
    tariffType: TypeChoisi,
    count: Int,
    allGroupedSales: List<Map.Entry<TypeChoisi, List<ArticlesBasesStatsTable>>> = emptyList()
): String {
    return when (tariffType) {
        TypeChoisi.LeMaxPrixArrive -> "🏆 ممتاز! $count ${get_BestNomArabDuPlurieul(count)} بأعلى سعر - أقصى فائدة محققة!"
        TypeChoisi.Prix_Detaille -> "⭐ جيد جداً! $count ${get_BestNomArabDuPlurieul(count)} بسعر التجزئة - حاول الوصول للحد الأقصى"
        TypeChoisi.Edited_Pour_Client -> "👍 لا بأس! $count ${get_BestNomArabDuPlurieul(count)} بسعر مخصص - يمكن تحسينه"
        TypeChoisi.Historique -> {
            // Ne pas afficher Historique, trouver le type le plus proche
            val historicalProducts = allGroupedSales.find { it.key == TypeChoisi.Historique }?.value ?: emptyList()
            val closestType = findClosestPriceType(historicalProducts, allGroupedSales)
            "🔄 $count ${get_BestNomArabDuPlurieul(count)} تحويل إلى ${closestType.nomArabe} للربح الأمثل"
        }
        TypeChoisi.Prix_SupperGro_Et_PresentationService -> "⬆️ $count ${get_BestNomArabDuPlurieul(count)} بسعر الجملة - ارفع للتجزئة"
        TypeChoisi.Tariff_Achat_Depuit_Grossisst -> "🚨 $count ${get_BestNomArabDuPlurieul(count)} بسعر الشراء - يجب رفع السعر فوراً!"
        else -> "🔍 $count ${get_BestNomArabDuPlurieul(count)} بهذا السعر - راجع التسعيرة"
    }
}
