package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.Buttons.Enhanced_Affiche_MotivationAu_Vendeur_De_Plus_De_Benifices.Affich

import EntreApps.Shared.Models.M21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.get_ListM21CataloguesCategorie
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val affiche_animation = false        //<--
    //TODO(1): fai que sifalse de ne pas affiche les animation

    val catalogues by remember {
        derivedStateOf { get_ListM21CataloguesCategorie() }
    }

    focusedValuesGetter.active_Central_Values.let { active_Central_Values ->
        Card(
            modifier = modifier
                .fillMaxWidth()
                ,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ما عرض  من المتجر",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Dynamic catalogue rows - now reactive to active_Central_Values changes
                catalogues.sortedBy { it.position }
                    .filter { it.keyID != "t4" } // Exclude "Sans Catalogue"
                    .forEach { catalogue ->
                        val percentage = when (catalogue.keyID) {
                            "t1" -> active_Central_Values.pourcentage_AffichageDuCatalogue_Conficerie
                            "t2" -> active_Central_Values.pourcentage_AffichageDuCatalogue_Cosmitiques
                            "t3" -> active_Central_Values.pourcentage_AffichageDuCatalogue_tebnage
                            else -> 0.0
                        }

                        CompactCatalogueRow(
                            catalogue = catalogue,
                            percentage = percentage,
                            affiche_animation = affiche_animation  // ← added
                        )
                    }
            }
        }
    }
}

@Composable
private fun CompactCatalogueRow(
    catalogue: M21CataloguesCategorie,
    percentage: Double,
    affiche_animation: Boolean,   // ← added parameter
    modifier: Modifier = Modifier
) {
    val isComplete = percentage >= 100.0

    // Only animate when affiche_animation = true AND row is not complete
    val alpha = if (affiche_animation && !isComplete) {
        val infiniteTransition = rememberInfiniteTransition(label = "blinking")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )
        animatedAlpha
    } else {
        1f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .semantics(mergeDescendants = true) {
                val catalogueDebugInfo = SemanticsPropertyKey<String>("CatalogueDebug")
                set(
                    catalogueDebugInfo,
                    "Catalogue: ${catalogue.nom}, ID: ${catalogue.keyID}, Percentage: $percentage%"
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) {
                Color.White
            } else {
                catalogue.couleur.copy(alpha = alpha)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                    color = if (isComplete) catalogue.couleur else Color.White
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { (percentage / 100.0).coerceIn(0.0, 1.0).toFloat() },
                    modifier = Modifier.width(40.dp),
                    color = if (isComplete) catalogue.couleur else Color.White,
                    trackColor = if (isComplete) {
                        catalogue.couleur.copy(alpha = 0.3f)
                    } else {
                        Color.White.copy(alpha = 0.3f)
                    }
                )
                Text(
                    text = if (percentage.isNaN() || percentage.isInfinite()) {
                        "0%"
                    } else {
                        "${percentage.toInt()}%"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isComplete) catalogue.couleur else Color.White
                )
            }
        }
    }
}
