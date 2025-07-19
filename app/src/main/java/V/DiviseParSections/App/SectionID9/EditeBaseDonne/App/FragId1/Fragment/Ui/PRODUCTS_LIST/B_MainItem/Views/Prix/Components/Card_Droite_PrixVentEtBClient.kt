package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FireTruck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun Card_Droite_PrixVentEtBClient(
    modifier: Modifier = Modifier,
    repositorysMainSetter: RepositorysMainSetter,
    produit: ArticlesBasesStatsTable,
    relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff: Pair<M13TarificationInfos, Boolean>,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
) {
    val (relative_Definie_Tariff, itsActiveTariff) = relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff

    fun get_Edited_Tariff(newPrixVent: Double): M13TarificationInfos {
        return relative_Definie_Tariff.copy(
            prixCurrency = newPrixVent,
            creationTimestamps = System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    // FIXED: Use upsert instead of add to ensure proper state updates
    fun add_Definie_Tariff(relative_Definie_Tariff: M13TarificationInfos) {
        repositorysMainSetter.upsert_M13TarificationInfos(relative_Definie_Tariff)
    }

    val currentPrice = when (itsActiveTariff) {
        true -> relative_Definie_Tariff.prixCurrency
        false -> produit.prixVent
    }

    Card(
        modifier = modifier
            .getSemanticsTag(get_Edited_Tariff(250.0), "get_Edited_Tariff")
            .getSemanticsTag(itsActiveTariff, "itsActiveTariff"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row {
                Text(
                    text = "📊 بيع",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Spacer(Modifier.padding(5.dp))
                Icon(
                    imageVector = Icons.Default.FireTruck,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.Green
                )
            }

            // Client benefit calculation with proper logic
            val beneficeClient =
                (produit.clientPrixVentUnite * produit.nombreUniteInt) - currentPrice
            PriceEditor(
                currentPrice = beneficeClient,
                label = "ربح الزبون",
                onPriceUpdate = { newBenClient ->
                    if (itsActiveTariff) {
                        // Only allow editing if DefiniParGerant tariff is active
                        if (produit.nombreUniteInt > 0) {
                            val newPrixVent =
                                (produit.clientPrixVentUnite * produit.nombreUniteInt) - newBenClient

                            // Update both the product and the tariff
                            updateProduct(produit.copy(prixVent = newPrixVent))
                            add_Definie_Tariff(get_Edited_Tariff(newPrixVent))
                        }
                    }
                },
                textColor = if (beneficeClient > 0)
                    Color(0xFFFF8C00)
                else MaterialTheme.colorScheme.error
            )

            // Unit sale price (if units > 0)
            if (produit.nombreUniteInt > 1) {
                val prixUnitVente =
                    round((currentPrice / produit.nombreUniteInt) * 100.0) / 100.0
                PriceEditor(
                    currentPrice = prixUnitVente,
                    label = "Unité",
                    onPriceUpdate = { newPrixUnit ->
                        val newPrixVent = newPrixUnit * produit.nombreUniteInt

                        when (itsActiveTariff) {
                            true -> add_Definie_Tariff(get_Edited_Tariff(newPrixVent))
                            false -> updateProduct(produit.copy(prixVent = newPrixVent))
                        }
                    },
                    textColor = MaterialTheme.colorScheme.secondary
                )
            }

            PriceEditor(
                currentPrice = currentPrice,
                label = "Prix Vent Pack",
                onPriceUpdate = { newPrix_Vent ->
                    when (itsActiveTariff) {
                        true -> add_Definie_Tariff(get_Edited_Tariff(newPrix_Vent))
                        false -> updateProduct(
                                produit.copy(
                                    prixVent = newPrix_Vent,
                                    etateActuelleOnFusionAvecBaseDonne = if (produit.prixAchat == 0.0)
                                        ArticlesBasesStatsTable
                                            .EtateActuelleOnFusionAvecBaseDonne.PrixDeVentDefinie else
                                        ArticlesBasesStatsTable
                                            .EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
                                )
                            )
                    }
                },
                textColor = Color.Red
            )
        }
    }
}
