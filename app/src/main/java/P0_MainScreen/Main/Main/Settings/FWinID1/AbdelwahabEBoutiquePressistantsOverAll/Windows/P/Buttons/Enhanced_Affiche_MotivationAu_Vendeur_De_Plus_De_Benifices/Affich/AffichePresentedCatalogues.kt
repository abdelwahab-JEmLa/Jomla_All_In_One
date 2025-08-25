package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices.Affich
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue.CataloguesCaegorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun AffichePresentedCatalogues(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    modifier: Modifier = Modifier
) {
    val activeOnVent_M8BonVent by remember {
        derivedStateOf { focusedValuesGetter.activeOnVent_M8BonVent }
    }

    val catalogues by remember {
        derivedStateOf { B4CatalogueCategoriesRepository() }
    }

    activeOnVent_M8BonVent?.let { bonVent ->
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عرض المتجر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Dynamic catalogue rows - now reactive to bonVent changes
                catalogues.sortedBy { it.position }.forEach { catalogue ->
                    val percentage = when (catalogue.keyID) {
                        "t1" -> bonVent.pourcentage_AffichageDuCatalogue_Conficerie
                        "t2" -> bonVent.pourcentage_AffichageDuCatalogue_Cosmitiques
                        "t3" -> bonVent.pourcentage_AffichageDuCatalogue_tebnage
                        "t4" -> {
                            val totalUsed = bonVent.pourcentage_AffichageDuCatalogue_Conficerie +
                                    bonVent.pourcentage_AffichageDuCatalogue_Cosmitiques +
                                    bonVent.pourcentage_AffichageDuCatalogue_tebnage
                            (100.0 - totalUsed).coerceAtLeast(0.0)
                        }
                        else -> 0.0
                    }

                    // Add a key to force recomposition when bonVent changes
                    key("${catalogue.keyID}_${bonVent.keyID}_$percentage") {
                        CompactCatalogueRow(
                            catalogue = catalogue,
                            percentage = percentage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCatalogueRow(
    catalogue: CataloguesCaegorie,
    percentage: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                val catalogueDebugInfo = SemanticsPropertyKey<String>("CatalogueDebug")
                set(catalogueDebugInfo, "Catalogue: ${catalogue.nom}, ID: ${catalogue.keyID}, Percentage: $percentage%")
            }
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = catalogue.nom,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LinearProgressIndicator(
                progress = { (percentage / 100.0).coerceIn(0.0, 1.0).toFloat() },
                modifier = Modifier.width(40.dp),
                color = catalogue.couleur,
                trackColor = catalogue.couleur.copy(alpha = 0.3f)
            )
            Text(
                text = "${String.format("%.0f", percentage)}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = catalogue.couleur
            )
        }
    }
}
