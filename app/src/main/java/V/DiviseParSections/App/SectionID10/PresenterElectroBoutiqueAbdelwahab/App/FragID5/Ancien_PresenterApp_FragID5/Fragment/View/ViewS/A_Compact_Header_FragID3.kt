package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import EntreApps.Shared.Models.M01Produit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Compact_Header_FragID3(
    modifier: Modifier = Modifier,
    relative_M1produit: M01Produit,
    isExpanded: Boolean,
    shouldShowButtons: Boolean = true,
    onUpdateTariff: () -> Unit,
    onUpdateProduit: (M01Produit) -> Unit = {},
    currentApp_Est_Admin: Boolean,
    onDelete: (M01Produit) -> Unit
) {
    val nameTextSize   = if (isExpanded) 14.sp else 10.sp
    val arabicTextSize = if (isExpanded) 12.sp else 9.sp
    val cardPadding    = if (isExpanded) 6.dp  else 3.dp
    val itemPadding    = if (isExpanded) 4.dp  else 2.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (isExpanded) 8.dp else 6.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 2.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(itemPadding)
        ) {
            // ── Nom + nom arabe ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = relative_M1produit.nom,
                        fontSize = nameTextSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = if (isExpanded) 16.sp else 12.sp
                    )
                    if (relative_M1produit.nomArab.isNotBlank() && isExpanded) {
                        Text(
                            text = relative_M1produit.nomArab,
                            fontSize = arabicTextSize,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // ── Bouton suppression (hauteur indépendante des info-cards) ──
            if (shouldShowButtons && currentApp_Est_Admin) {
                DeleteProductHeader(
                    productName = relative_M1produit.nom,
                    onDelete = { onDelete(relative_M1produit) }
                )
            }

            // ── Barre d'info-cards (droit → gauche, hauteur uniforme) ───
            // Row scrollable + Arrangement.End : les items collent à droite
            // même quand ils ne remplissent pas toute la largeur.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(itemPadding, Alignment.End)
            ) {
                if (relative_M1produit.quantite_Boit_Par_Carton > 1 || currentApp_Est_Admin) {
                    Item_Carton_FragID3(
                        quantite_Boit_Par_Carton = relative_M1produit.quantite_Boit_Par_Carton,
                        isExpanded = isExpanded,
                        currentApp_Est_Admin = currentApp_Est_Admin,
                        onUpdate = { new ->
                            onUpdateProduit(relative_M1produit.copy(quantite_Boit_Par_Carton = new))
                        }
                    )
                }

                if (relative_M1produit.nombreUniteInt > 1 || currentApp_Est_Admin) {
                    Item_Unite_FragID3(
                        nombreUniteInt = relative_M1produit.nombreUniteInt,
                        isExpanded = isExpanded,
                        currentApp_Est_Admin = currentApp_Est_Admin,
                        onUpdate = { new ->
                            onUpdateProduit(relative_M1produit.copy(nombreUniteInt = new))
                        }
                    )
                }

                if (shouldShowButtons && currentApp_Est_Admin) {
                    Item_Tarif_FragID3(
                        isExpanded = isExpanded,
                        onUpdateTariff = onUpdateTariff
                    )
                }

                Item_BeneficeClient_FragID3(
                    clientPrixVentUnite = relative_M1produit.clientPrixVentUnite,
                    nombreUniteInt = relative_M1produit.nombreUniteInt,
                    prixVent = relative_M1produit.prixVent,
                    isExpanded = isExpanded
                )

                Item_TotalClient_FragID3(
                    clientPrixVentUnite = relative_M1produit.clientPrixVentUnite,
                    nombreUniteInt = relative_M1produit.nombreUniteInt,
                    isExpanded = isExpanded
                )

                Item_PrixUnitaireClient_FragID3(
                    clientPrixVentUnite = relative_M1produit.clientPrixVentUnite,
                    isExpanded = isExpanded,
                    onUpdate = { new ->
                        onUpdateProduit(relative_M1produit.copy(clientPrixVentUnite = new))
                    }
                )
            }
        }
    }
}
