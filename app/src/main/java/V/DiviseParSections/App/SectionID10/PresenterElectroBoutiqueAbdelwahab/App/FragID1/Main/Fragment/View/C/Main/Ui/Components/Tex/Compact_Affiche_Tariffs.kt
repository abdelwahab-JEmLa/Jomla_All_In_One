package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Tex

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Compact_Affiche_Tariffs(
    cAfficheurTelephone: Boolean,
    article: M01Produit,
    repositorysMainGetter: RepositorysMainGetter = koinInject()
) {
    // Get all tariffs for this product, grouped by type and sorted by last update
    val tariffsList = remember(article.keyID) {
        repositorysMainGetter.find_List_Tariffs_Du_Produit(
            article.keyID,
            its_grossist_app = false
        )
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) ->
                // Get the most recent tariff for each type
                tariffs.maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }
            }
            .values
            .filterNotNull()
            .sortedBy { it.prixCurrency }
    }

    if (cAfficheurTelephone) {
        if (article.prixVent > 0) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Display tariffs if available
                if (tariffsList.isNotEmpty()) {
                    tariffsList.forEach { tariff ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Abbreviated tariff name with color background
                            Text(
                                text = tariff.typeChoisi.abrgNom,
                                style = MaterialTheme.typography.bodySmall,
                                color = tariff.typeChoisi.couleur_Text,
                                modifier = Modifier
                                    .background(
                                        color = tariff.typeChoisi.couleur,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )

                            // Tariff price → unit price
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = remember(tariff.prixCurrency) {
                                        String.format("%.0f", tariff.prixCurrency)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tariff.typeChoisi.couleur
                                )

                                // Unit price if multiple units
                                if (article.nombreUniteInt > 1) {
                                    Text("→", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = remember(tariff.prixCurrency, article.nombreUniteInt) {
                                            val unitPrice = tariff.prixCurrency / article.nombreUniteInt
                                            String.format("%.2f", unitPrice)
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Fallback to article.prixVent if no tariffs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = remember(article.prixVent) {
                                    String.format("%.0f", article.prixVent)
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (article.nombreUniteInt > 1) {
                                Text("→", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = remember(article.prixVent, article.nombreUniteInt) {
                                        val unitPrice = article.prixVent / article.nombreUniteInt
                                        String.format("%.2f", unitPrice)
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "إن شاء الله نحاولو نديرولك سعر شباب",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
