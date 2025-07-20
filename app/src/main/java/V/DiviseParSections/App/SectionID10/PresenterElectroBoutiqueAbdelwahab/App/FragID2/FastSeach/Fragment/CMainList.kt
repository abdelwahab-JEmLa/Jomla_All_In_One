package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.Z.Main.PanierFinaleDAchatSec1Frag3
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.ViewProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ModernToastMessageLo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import org.koin.compose.koinInject

@Composable
fun MainListT1(
    modifier: Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    searchFilter: String,
    sortedProducts: List<ArticlesBasesStatsTable>,
) {

    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (searchFilter.isNotEmpty() && sortedProducts.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun produit trouvé pour \"$searchFilter\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        } else {
            items(sortedProducts) { product ->
                ViewProduit_T1(
                    product = product,
                )
            }
        }
    }

    // Show Dialog_Panie when opnerDialog_Panier_M10OperationVentCouleur is not null
    Dialog_Panie(
        focusedValuesGetter = focusedValuesGetter,
    )
}

@Composable
private fun Dialog_Panie(
    focusedValuesGetter: FocusedValuesGetter,
) {
    var showToast by remember { mutableStateOf(false) }

    if (focusedValuesGetter.active_Central_Values.opnerDialog_Panier_M10OperationVentCouleur != null) {
        Dialog(
            onDismissRequest = {
                showToast = true
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(
                        visible = showToast,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                                scaleIn(
                                    initialScale = 0.8f,
                                    animationSpec = tween(durationMillis = 300)
                                ),
                        exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                                scaleOut(
                                    targetScale = 0.8f,
                                    animationSpec = tween(durationMillis = 300)
                                ),
                        modifier = Modifier.zIndex(999f)
                    ) {
                        ModernToastMessageLo(
                            message = "يرجى استخدام الأزرار لتحديد السعر",
                            onDismiss = { showToast = false }
                        )
                    }

                    Column {
                        PanierFinaleDAchatSec1Frag3()
                    }

                    PressistatntMainActivityButtons_Sec8FWinID1()

                    FloatingActionButton(
                        onClick = {
                            focusedValuesGetter.update_activeCentralValues(
                                focusedValuesGetter.active_Central_Values.copy(
                                    opnerDialog_Panier_M10OperationVentCouleur = null
                                )
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .zIndex(100f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق"
                        )
                    }
                }
            }
        }
    }
}
