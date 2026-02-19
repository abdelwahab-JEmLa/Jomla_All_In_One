package com.example.clientjetpack.App2.App.View.Pro0.Proto.Components

import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2

@Stable
class ProduitExpandState(
    private val relative_M1produit: ArticlesBasesStatsTable,
    private val relativeList_M3ColorsProduit: List<M3CouleurProduitInfos>,
    private val focusedValuesGetter: FocusedValuesGetter_app2,
) {
    val isExpanded: Boolean
        get() = focusedValuesGetter.active_Central_Values.expanded_M1Produit?.keyID == relative_M1produit.keyID

    var bigPresenterIndex: Int by mutableIntStateOf(resolveInitialIndex())
        private set

    val bigPresenterCouleur: M3CouleurProduitInfos
        get() = relativeList_M3ColorsProduit.getOrNull(bigPresenterIndex)
            ?: relativeList_M3ColorsProduit.first()

    val cardPadding get() = if (isExpanded) 8 else 4
    val innerPadding get() = if (isExpanded) 8 else 4
    val cardElevation get() = if (isExpanded) 8 else 4

    fun selectColor(couleur: M3CouleurProduitInfos) {
        val idx = relativeList_M3ColorsProduit.indexOf(couleur)
        if (idx != -1) bigPresenterIndex = idx
    }

    fun syncFromFocusedValues(expandedColor: M3CouleurProduitInfos?) {
        expandedColor ?: return
        if (expandedColor.parentBProduitOldID != relative_M1produit.id) return
        val idx = findMatchingColorIndex(
            expandedColor = expandedColor,
            availableColors = relativeList_M3ColorsProduit
        )
        if (idx != -1 && idx != bigPresenterIndex) bigPresenterIndex = idx
    }

    fun findMatchingColorIndex(
        expandedColor: M3CouleurProduitInfos,
        availableColors: List<M3CouleurProduitInfos>
    ): Int {
        val exactMatch = availableColors.indexOfFirst { it.keyID == expandedColor.keyID }
        if (exactMatch != -1) return exactMatch

        val indexMatch = availableColors.indexOfFirst {
            it.parentBProduitOldID == expandedColor.parentBProduitOldID &&
                    it.indexCouleurDansAncienProto == expandedColor.indexCouleurDansAncienProto
        }
        if (indexMatch != -1) return indexMatch

        if (expandedColor.nomCouleurStrSiSonImageDispo.isNotBlank()) {
            val colorNameMatch = availableColors.indexOfFirst {
                it.nomCouleurStrSiSonImageDispo.equals(
                    expandedColor.nomCouleurStrSiSonImageDispo,
                    ignoreCase = true
                )
            }
            if (colorNameMatch != -1) return colorNameMatch
        }

        return -1
    }

    fun onImageTap(tappedCouleur: M3CouleurProduitInfos) {
        val current = focusedValuesGetter.active_Central_Values
        val isSameProduct = current.expanded_M1Produit?.keyID == relative_M1produit.keyID
        val isDifferentColor = current.expanded_M3CouleurProduitInfos?.keyID != tappedCouleur.keyID

        if (isSameProduct && isDifferentColor) {
            focusedValuesGetter.update_ActiveCentralValues_app2(
                current.copy(expanded_M3CouleurProduitInfos = tappedCouleur)
            )
        } else {
            val newProduct = if (isSameProduct) null else relative_M1produit
            focusedValuesGetter.update_ActiveCentralValues_app2(
                current.copy(
                    expanded_M1Produit = newProduct,
                    expanded_M3CouleurProduitInfos = if (newProduct != null) tappedCouleur else null
                )
            )
        }
    }

    private fun resolveInitialIndex(): Int {
        val expandedColor = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos
            ?: return 0
        if (expandedColor.parentBProduitOldID != relative_M1produit.id) return 0
        val idx = findMatchingColorIndex(
            expandedColor = expandedColor,
            availableColors = relativeList_M3ColorsProduit
        )
        return if (idx != -1) idx else 0
    }
}
