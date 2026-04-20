package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.ColorImageCard
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.ViewS.Views.Pricipale_Tariffs_Vendeurs
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur.updateExpandedCouleur
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject

@Composable
fun A_Expanded_Produit_Ac_Multi_Couleurs(
    relative_M1produit: M01Produit,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    
    modifier: Modifier = Modifier,
    wifiTransferDatas: WifiTransferDatas = koinInject()
) {
    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    var top_presanted_prisipame_couleur by remember { mutableStateOf(0) }

    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    LaunchedEffect(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )

                if (matchingIndex != -1 && matchingIndex != top_presanted_prisipame_couleur) {
                    top_presanted_prisipame_couleur = matchingIndex
                }
            }
        }
    }

    // Get the tariff for this product
    val datasValue = repositorysMainGetter.repo13TarificationInfos.datasValue
    val findTariff = remember(relative_M1produit.keyID, focusedValuesGetter.currentApp_ItsWorkChezGrossisst) {
        datasValue.find { tariff ->
            val type_A_Cherche = if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst)
                M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
            else
                M13TarificationInfos.TypeChoisi.Prix_Detaille

            tariff.typeChoisi == type_A_Cherche && tariff.parent_M1Produit_KeyId == relative_M1produit.keyID
        }
    }

    val default_Tariff = M13TarificationInfos.get_default_P0(
        relative_M1produit,
        start_Prix_Depuit_Ancient = relative_M1produit.prixAchat
    )
    val finale_Tariff = findTariff ?: default_Tariff.first
    val developement_affiche = true

    // Check if this phone is NOT a client and is in host mode (big display mode)
    val isHostPhone = wifiTransferDatas.connectionUiState.value.isHostPhone
            && wifiTransferDatas.connectionUiState.value.isConnected || developement_affiche

    fun onClick_Icon(relative_M3CouleurProduitInfos: M3CouleurProduitInfos) {
        updateExpandedCouleur(
            relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
            focusedValuesGetter = focusedValuesGetter,
            
        )
    }

    // Define selectedCouleur at the top level so it's accessible throughout the Box
    val selectedCouleur = relative_ListM3Couleurs[top_presanted_prisipame_couleur]

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Box only wraps the big presenter image
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            ColorImageCard(
                relative_M3CouleurProduitInfos = selectedCouleur,
                isSelected = true,
                onIconClick = {
                    onClick_Icon(selectedCouleur)
                },
                
                modifier = Modifier.fillMaxWidth()
            )

            // Floating action row positioned at bottom end of the big image only
            if (isHostPhone) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tariffs list on the left
                    Pricipale_Tariffs_Vendeurs(
                        relative_M1produit = relative_M1produit,
                        tariffsList = datasValue
                    )

                    // Sale button on the right
                    Lenceur_Vent_Handler(
                        relative_M1produit = relative_M1produit,
                        selectedCouleur = selectedCouleur,
                        finale_Tariff = finale_Tariff
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Other color cards row outside the Box
        if (relative_ListM3Couleurs.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                    if (index != top_presanted_prisipame_couleur) {
                        ColorImageCard(
                            relative_M3CouleurProduitInfos = couleur,
                            isSelected = false,
                            onIconClick = {
                                top_presanted_prisipame_couleur = index
                                onClick_Icon(couleur)
                            },
                            
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun RequestBuilder<Drawable>.applyOptimizedImageOptions(
    couleur: M3CouleurProduitInfos
) = this
    .dontAnimate()
    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
    .priority(Priority.NORMAL)
    .dontTransform()
    .signature(ObjectKey("${couleur.keyID}_${couleur.dernierTimeTampsSynchronisationAvecFireBase}"))
    .override(400, 400)
    .disallowHardwareConfig()
    .format(DecodeFormat.PREFER_RGB_565)
    .encodeQuality(70)
    .skipMemoryCache(false)
