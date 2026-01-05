package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.Expand_Produit_Couleur.updateExpandedCouleur
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.ColorImageCard_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.Lenceur_Vent_Handler.View.Lenceur_Vent_Handler_FragID3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.ViewS.Views.Pricipale_Tariffs_Vendeurs_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiTransferDatas
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Item_Produit_FragID3(
    relative_M1produit: ArticlesBasesStatsTable,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    focusedValuesSetter: FocusedValuesSetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    wifiTransferDatas: WifiTransferDatas = koinInject()
) {            //<--
//TODO(1): fait que si nin expand de diminue max paddings
    val developement_test = true
    val expand_affiche_button_Lence_vent = developement_test

    val relative_ListM3Couleurs = remember(relative_M1produit.keyID) {
        repositorysMainGetter.find_ListM3CouleurInfos_By_Parent_Produit_KeyID(relative_M1produit.keyID)
    }

    var big_presenter_couleur_produit by remember { mutableStateOf(0) }

    val expanded_M3CouleurProduitInfos = focusedValuesGetter.active_Central_Values.expanded_M3CouleurProduitInfos

    // Check if THIS product is expanded
    val isThisProductExpanded = remember(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            relative_ListM3Couleurs.any { it.keyID == expandedColor.keyID }
        } ?: false
    }

    LaunchedEffect(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )

                if (matchingIndex != -1 && matchingIndex != big_presenter_couleur_produit) {
                    big_presenter_couleur_produit = matchingIndex
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

    // Show buttons when host phone (always show lence_vent)
    val shouldShowButtons = isHostPhone

    fun onClick_Icon(relative_M3CouleurProduitInfos: M3CouleurProduitInfos) {
        updateExpandedCouleur(
            relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
            focusedValuesGetter = focusedValuesGetter,
            on_pour_send_data = on_pour_send_data
        )

        on_pour_send_data(
            WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran.prefix,
            relative_M3CouleurProduitInfos.keyID
        )
    }

    fun onCollapse() {
        focusedValuesGetter.update_activeCentralValues(
            focusedValuesGetter.active_Central_Values.copy(
                expanded_M3CouleurProduitInfos=null
            )
        )

        on_pour_send_data(
            WifiUpdateClientDisplayerStats.Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran.prefix,
            ""
        )
    }

    val selectedCouleur = relative_ListM3Couleurs[big_presenter_couleur_produit]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isThisProductExpanded) 8.dp else 4.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // 1. Main color image - clickable to expand/collapse
                ColorImageCard_FragID3(
                    relative_M3CouleurProduitInfos = selectedCouleur,
                    isSelected = true,
                    onIconClick = { onClick_Icon(selectedCouleur) },
                    on_pour_send_data = on_pour_send_data,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Tariffs and Lence_vent - displayed based on expansion state
                // Tariffs only shown when EXPANDED, Lence_vent ALWAYS shown (when host)
                if (shouldShowButtons) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White.copy(alpha = 0.95f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Tariffs - only show when EXPANDED
                        if (isThisProductExpanded) {
                            Pricipale_Tariffs_Vendeurs_FragID3(
                                relative_M1produit = relative_M1produit,
                                tariffsList = datasValue
                            )
                        }

                        // Sale button - ALWAYS show when host
                        Lenceur_Vent_Handler_FragID3(
                            relative_M1produit = relative_M1produit,
                            selectedCouleur = selectedCouleur,
                            finale_Tariff = finale_Tariff
                        )
                    }
                }

                // 3. Sub-colors display - at the end
                if (relative_ListM3Couleurs.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (isThisProductExpanded) {
                        // EXPANDED: Show sub-colors in horizontal FlowRow (wraps if needed)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            maxItemsInEachRow = 4
                        ) {
                            relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                                if (index != big_presenter_couleur_produit) {
                                    ColorImageCard_FragID3(
                                        relative_M3CouleurProduitInfos = couleur,
                                        isSelected = false,
                                        onIconClick = {
                                            big_presenter_couleur_produit = index
                                            onClick_Icon(couleur)
                                        },
                                        on_pour_send_data = on_pour_send_data,
                                        modifier = Modifier
                                            .weight(1f, fill = false)
                                            .height(80.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // COLLAPSED: Show sub-colors in vertical COLUMN
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                                if (index != big_presenter_couleur_produit) {
                                    ColorImageCard_FragID3(
                                        relative_M3CouleurProduitInfos = couleur,
                                        isSelected = false,
                                        onIconClick = {
                                            big_presenter_couleur_produit = index
                                            onClick_Icon(couleur)
                                        },
                                        on_pour_send_data = on_pour_send_data,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                    )
                                }
                            }
                        }
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
