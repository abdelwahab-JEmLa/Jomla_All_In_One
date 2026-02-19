package com.example.clientjetpack.App2.App.View.Pro0.Proto.Components

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.clientjetpack.App2.App.View.Pro0.Proto.ViewS.ColorImageCard_AppEcranPresntoireJemlaCom

@Composable
fun Big_Principale_AppEcranPresntoireJemlaCom(
    big_presenter_couleur_produit: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
) {
    ColorImageCard_AppEcranPresntoireJemlaCom(
        relative_M3CouleurProduitInfos = big_presenter_couleur_produit,
        expandState = expandState,
        isSelected = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
