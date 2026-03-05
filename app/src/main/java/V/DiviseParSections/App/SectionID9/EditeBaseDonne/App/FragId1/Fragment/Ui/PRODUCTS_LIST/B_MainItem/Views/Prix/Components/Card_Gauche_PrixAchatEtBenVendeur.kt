package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun Card_Gauche_PrixAchatEtBenVendeur(
    modifier: Modifier = Modifier,
    repositorysMainSetter: RepositorysMainSetter,
    produit: M01Produit,
    relative_M13Tariffication_DefiniParGerant_Ac_ItsActiveTariff:
    Pair<M13TarificationInfos, Boolean>,
    updateProduct: (M01Produit) -> Unit,
    onNextField: (() -> Unit)? = null,
    shouldHideQuickInfoCards: Boolean
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

    val vertTurq = Color(0xFF066C62)

    Card(
        modifier = modifier
            .getSemanticsTag(get_Edited_Tariff(280.0), "get_Edited_Tariff"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Section header
            Text(
                text = "🏪 شراء",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = vertTurq
            )

            val benefice = produit.prixVent - produit.prixAchat

            if (produit.prixVent > 0.0) {
                PriceEditor(
                    currentPrice = benefice,
                    label = "ربحي الخاص",
                    onPriceUpdate = { newBenefice ->
                        if (itsActiveTariff) {
                            val newPrixVent = produit.prixAchat + newBenefice

                            // Update both the product and the tariff
                            updateProduct(produit.copy(prixVent = newPrixVent))
                            add_Definie_Tariff(get_Edited_Tariff(newPrixVent))
                        }
                    },
                    textColor = if (benefice > 0) {
                        vertTurq
                    } else MaterialTheme.colorScheme.error
                )
            }

            if (produit.nombreUniteInt > 1) {
                val prixUnitAchat =
                    round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
                PriceEditor(
                    currentPrice = prixUnitAchat,
                    label = "Unité",
                    onPriceUpdate = { newPrixUnit ->
                        if (itsActiveTariff) {
                            // Only allow editing if Prix_Detaille tariff is active
                            val newPrixAchat = newPrixUnit * produit.nombreUniteInt
                            updateProduct(
                                produit.copy(
                                    prixAchat = newPrixAchat,
                                    prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                                )
                            )
                        }
                    },
                    textColor = MaterialTheme.colorScheme.secondary
                )
            }

            // Total purchase price
            PriceEditor(
                currentPrice = produit.prixAchat,
                label = "Prix Achat Pack",
                onPriceUpdate = { newPrix ->
                    val newPrd = produit.copy(
                        prixAchat = newPrix,
                        prixAchatDernierTimeTempUpdate = System.currentTimeMillis(),
                        etateActuelleOnFusionAvecBaseDonne = if (produit.prixAchat == 0.0)
                            M01Produit
                                .EtateActuelleOnFusionAvecBaseDonne.PrixAchatPriseDepuitGrossist else
                            M01Produit
                                .EtateActuelleOnFusionAvecBaseDonne.CaprtureSonImage
                    )
                    updateProduct(newPrd)
                },
                textColor = vertTurq,
                shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                onNextField = onNextField
            )
        }
    }
}
