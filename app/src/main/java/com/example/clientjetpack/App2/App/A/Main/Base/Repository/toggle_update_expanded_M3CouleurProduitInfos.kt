package com.example.clientjetpack.App2.App.A.Main.Base.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


fun toggle_update_expanded_M3CouleurProduitInfos_app2(
    focusedValuesGetter_app2: FocusedValuesGetter_app2,
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos
) {
    val currentExpanded = focusedValuesGetter_app2.active_Central_Values.expanded_M3CouleurProduitInfos

    // Determine new expanded color value
    val newExpandedColor = if (currentExpanded?.keyID == relative_M3CouleurProduitInfos.keyID) {
        null
    } else {
        relative_M3CouleurProduitInfos
    }

    // Get the parent product for the color being expanded
    val repositorysMainGetter = object : KoinComponent {
        val repo: RepositorysMainGetter by inject()
    }.repo

    val parentProduct = repositorysMainGetter.repoM1Produit.datasValue.find {
        it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
    }

    // When expanding a color, also expand its parent product
    // When collapsing, also collapse the parent product
    val newExpandedProduit = if (newExpandedColor != null) {
        parentProduct
    } else {
        null
    }

    focusedValuesGetter_app2.update_ActiveCentralValues_app2(
        focusedValuesGetter_app2.active_Central_Values.copy(
            expanded_M3CouleurProduitInfos = newExpandedColor,
            expanded_M1Produit = newExpandedProduit
        )
    )
}
