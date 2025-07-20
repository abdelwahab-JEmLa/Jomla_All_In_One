package P0_MainScreen.Main.Main.Settings.UnderAll.Dialogs

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ModernToastMessageLo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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

@Composable
fun Dialog_MainFastSearchProduitPourVent(
    focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    focusedValuesGetter: FocusedValuesGetter = focusedVarsHandlerFacade.focusedValuesGetter,
    sourceLenceurDeCetteFragment: ActiveCentralValues.RoleDefinieParSourceACetteFragment
) {
    var showToast by remember { mutableStateOf(false) }

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

                MainFastSearchProduitPourVent(
                    sourceLenceurDeCetteFragment = sourceLenceurDeCetteFragment,
                )

                PressistatntMainActivityButtons_Sec8FWinID1()

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .zIndex(100f),
                ) {

                    FloatingActionButton(
                        onClick = {
                            val activeCentralValues = focusedValuesGetter.active_Central_Values
                            focusedValuesGetter.update_activeCentralValues(
                                activeCentralValues
                                    .copy(
                                        affiche_Panier_au_Search_Dialog = !activeCentralValues.affiche_Panier_au_Search_Dialog
                                    )
                            )
                        },

                        containerColor = if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        contentColor = if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSecondary
                        }
                    ) {
                        Icon(
                            imageVector = if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                                Icons.Filled.ShoppingCart
                            } else {
                                Icons.Outlined.ShoppingCart
                            },
                            contentDescription = if (focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog) {
                                "إخفاء السلة"
                            } else {
                                "إظهار السلة"
                            }
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeDialogSearchM1Produit()
                            focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(
                                ""
                            )
                            focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
                            focusedVarsHandlerFacade.focusedValuesSetter.desactive_CurrentApp_dialogAboveAll_OutlinedSearchListProduits()
                            focusedValuesGetter.update_activeCentralValues(
                                focusedValuesGetter.active_Central_Values
                                    .copy(
                                        affiche_Panier_au_Search_Dialog = false
                                    )
                            )
                        },

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
