package com.example.clientjetpack.App2.App.View.Components

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.clientjetpack.App2.App.View.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom

@Composable
fun SubColorCard_WithButton_app2(
    couleur: M3CouleurProduitInfos,
    relative_M1produit: ArticlesBasesStatsTable,
    relative_M1ProduitToItListM3Couleur: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
) {
    Column(
    ) {
        ColorImageCard_AppEcranPresntoireJemlaCom(
            relative_M1ProduitToItListM3Couleur=relative_M1ProduitToItListM3Couleur,
            relative_M3CouleurProduitInfos = couleur,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}
