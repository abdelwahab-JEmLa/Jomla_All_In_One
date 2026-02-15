package com.example.clientjetpack.App2.App.View.ViewS

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.View.ViewS.Views.Image_Displaye_app2

@Composable
fun ColorImageCard_AppEcranPresntoireJemlaCom(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp), // Default: all corners rounded
    relative_M1ProduitToItListM3Couleur: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>
) {
    Card(
        modifier = modifier,
        shape = roundedCorners
    ) {
        Box(
            modifier =
                // Sub-color: wrap to content height
                Modifier.Companion
                    .fillMaxWidth()
                    .wrapContentHeight()
        ) {
            // Image always clickable
            Image_Displaye_app2(
                relative_M1ProduitToItListM3Couleur=relative_M1ProduitToItListM3Couleur,
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                contentScale = ContentScale.Companion.Crop,
                modifier = Modifier.Companion ,
            )
        }
    }
}
