package com.example.clientjetpack.App2.App.View.Pro0.Proto.ViewS

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.View.Pro0.Proto.Components.ProduitExpandState
import com.example.clientjetpack.App2.App.View.Pro0.Proto.ViewS.Views.Image_Displaye_app2

@Composable
fun ColorImageCard_AppEcranPresntoireJemlaCom(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    expandState: ProduitExpandState,
    isSelected: Boolean = false,
    onImageClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    roundedCorners: RoundedCornerShape = RoundedCornerShape(12.dp),
) {
    Card(
        modifier = modifier,
        shape = roundedCorners,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Box(
            modifier = if (isSelected) Modifier.fillMaxWidth().aspectRatio(370f / 500f)
            else Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Image_Displaye_app2(
                relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                expandState = expandState,
                contentScale = if (isSelected) ContentScale.Fit else ContentScale.Crop,
                modifier = Modifier,
                onImageClick = onImageClick,
            )
        }
    }
}
