package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.FragID_3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.View.A_GlideDisplayImageByKeyId_Proto_4_11
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Couleurs(
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    Produit: _1_2_ProduitAcheteOperation,
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
) {
    LazyRow {
        items(
            colorsForProduct
                .filter { it.totaleQuantity > 0 }
                .distinctBy { it.couleurIndex_ParentVID }
        ) { Couleur ->
            VerticalDivider(
                thickness = 9.dp,
                color = Color.Red
            )

            val totaleQuantity = colorsForProduct
                .filter { it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID }
                .sumOf { it.totaleQuantity }

            Card(
                Modifier.background(Color.Red)
            ) {
                Column {
                    A_GlideDisplayImageByKeyId_Proto_4_11(
                        Produit.produitAcheterID,
                        Couleur.couleurIndex_ParentVID + 1,
                        360.dp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.70f),
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "totaleQuantity>$totaleQuantity",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(4.dp)
                        )
                    }

                    // Now using LazyColumn for the list of buyers
                    Acheteurs(buyerIds, models, colorsForProduct, Couleur)
                }
            }
        }
    }
}

