package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M3CouleurProduitInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject

@SuppressLint("UnrememberedMutableState")
@Composable
fun View_LikedTo_FragSearcher(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    relative_M10OperationVentCouleur: M10OperationVentCouleur?,
    viewModel: ViewModelsProduit_T1
) {
    val haptic = LocalHapticFeedback.current

    relative_M10OperationVentCouleur?.let { ventOperation ->
        val parent_linkVent_M10Vent = repositorysMainGetter.find_M10OperationVentCouleur(
            ventOperation.linked_To_M10OperationVent_KeyID
        )
        val parent_linkVent_M10Vent_Relative_M3Couleur =
            repositorysMainGetter.find_M3CouleurInfos_By_KeyID(
                parent_linkVent_M10Vent?.parent_M3CouleurProduit_KeyID ?: ""
            )

        val text = ventOperation.linked_To_M10OperationVent_DebugInfos
        if (text.isNotEmpty()) {

            fun handleDeleteLink() {
                val updatedVent = relative_M10OperationVentCouleur.copy(
                    its_Linked_To_Autre_Vent_Si_NonDispo = false,
                    linked_To_M10OperationVent_KeyID = "",
                    linked_To_M10OperationVent_DebugInfos = "",
                    siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId = "",
                    siNonDispoParentM10Vent_it_parent_M1Produit_Nom = "",
                )
                aCentralFacade.repositorysMainSetter.update_M10OperationVentCouleur(updatedVent)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }

            Card(
                modifier = modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .getSemanticsTag(relative_M10OperationVentCouleur, "")
                        .padding(8.dp)
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        SmallFloatingActionButton(
                            onClick = { handleDeleteLink() },
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.9f),
                            contentColor = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove link",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    val imageFile by derivedStateOf {
                        parent_linkVent_M10Vent_Relative_M3Couleur?.let {
                            viewModel.getImageFile(
                                it.nomImageFichieSansEtansion, it.extensionDisponible
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (parent_linkVent_M10Vent_Relative_M3Couleur != null) {
                            when (parent_linkVent_M10Vent_Relative_M3Couleur.aAffiche) {
                                M3CouleurProduitInfos.Type.Image -> {
                                    ImageDisplayerGlide_Sec2FragID2_SearchProduit(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clickable { handleDeleteLink() },
                                        imageFile = imageFile,
                                        colorName = parent_linkVent_M10Vent_Relative_M3Couleur.nomCouleurStrSiSonImageDispo,
                                        contentScale = ContentScale.Crop,
                                        imageSize = DpSize(60.dp, 60.dp),
                                        onClickToOpenWindow = { handleDeleteLink() }
                                    )
                                }

                                M3CouleurProduitInfos.Type.Nom -> {
                                    ColorNameDisplayer_Sec2FragID2(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clickable { handleDeleteLink() },
                                        colorName = parent_linkVent_M10Vent_Relative_M3Couleur.nomCouleurStrSiSonImageDispo,
                                        onClickToOpenWindow = { handleDeleteLink() }
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "si ${
                                    parent_linkVent_M10Vent?.parent_M1Produit_KeyId?.takeLast(
                                        4
                                    )
                                } est non dispo",
                                color = Color.White,
                                fontSize = 8.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

            }
        }
    }
}
