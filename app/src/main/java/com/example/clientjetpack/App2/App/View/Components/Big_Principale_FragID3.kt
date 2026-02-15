package com.example.clientjetpack.App2.App.View.Components

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import com.example.clientjetpack.App2.App.View.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun Big_Principale_AppEcranPresntoireJemlaCom(
    relative_M1ProduitToItListM3Couleur: Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>,
    big_presenter_couleur_produit: M3CouleurProduitInfos,
) {
    ColorImageCard_AppEcranPresntoireJemlaCom(
        relative_M1ProduitToItListM3Couleur = relative_M1ProduitToItListM3Couleur,
        relative_M3CouleurProduitInfos =big_presenter_couleur_produit,
    )
}
