package Application4.App.Fragment.View.Components.A_Header.View

import Application2.App.View.Pro0.Proto.ViewS.getPrixDrawables
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Fragment.View.ViewS.Views.Image_Displaye
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import org.koin.compose.koinInject

@Composable
fun ColorImageCard_App4(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos,
    isSelected: Boolean,
    modifier: Modifier = Modifier.Companion,
    roundedCorners: RoundedCornerShape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,  //<--
    header: @Composable () -> Unit = {}
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val focusedValuesGetter: FocusedValuesGetter = koinInject()
    val isEditMode = focusedValuesGetter.active_Central_Values.affiche_buttons_lien_unite_couleur_au_couleut_parent
        || focusedValuesGetter.active_Central_Values.currentApp_Est_Admin

    val relative_M1produit = remember(relative_M3CouleurProduitInfos.parentBProduitInfosKeyID) {
        viewModel.active_Datas.list_M1Produit?.find {
            it.keyID == relative_M3CouleurProduitInfos.parentBProduitInfosKeyID
        }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
    val storageRef = Firebase.storage.reference.child("Images Articles Data Base").child("produits")

    val elevation = if (isSelected) 4.dp else 2.dp

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = roundedCorners
    ) {
        androidx.compose.foundation.layout.Column {
            header()

            Box(
                modifier = if (isSelected) {
                    Modifier.Companion
                        .fillMaxWidth()
                        .aspectRatio(370.dp / 500.dp)
                } else {
                    Modifier.Companion
                        .fillMaxWidth()
                        .wrapContentHeight()
                }
            ) {
                Image_Displaye(
                    modifier = Modifier.Companion,
                    relative_M3CouleurProduitInfos = relative_M3CouleurProduitInfos,
                    contentScale = if (isSelected) ContentScale.Companion.Fit else ContentScale.Companion.Crop,
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    list_M1Produit = uiState_NewProtoPatterns_viewModel.second.active_Datas
                        .list_M1Produit,
                )

                if (isSelected) {
                    val price = relative_M1produit?.clientPrixVentUnite ?: 0.0
                    val drawables = getPrixDrawables(price.toInt())
                    if (drawables.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(if (isEditMode) Alignment.Companion.BottomStart else Alignment.Companion.BottomEnd)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            drawables.forEachIndexed { index, res ->
                                Image(
                                    painter = painterResource(id = res),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .offset(x = (index * 14).dp, y = (index * 14).dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}
