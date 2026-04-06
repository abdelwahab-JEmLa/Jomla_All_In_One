package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GerantButton(
    relative_Tariff: M13TarificationInfos?,
    relative_M1Produit: M01Produit,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    showLabels: Boolean,
    onClickPrixButton: () -> Unit ={},
    onClickAnulationButton: (() -> Unit)? = null,
    focusedVarsHandlerFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    focusedValuesGetter: FocusedValuesGetter = focusedVarsHandlerFacade.focusedValuesGetter,
) {
    fun save_Tariff_au_relative_vent_et_ferm_fabs_tariffs() {
        if (relative_Tariff != null) {
            repositorysMainSetter
                .saveTariff_Et_RelateIt_Au_Vents_Correspond(
                    aCentralFacade = aCentralFacade,
                    m13TarificationInfos_Pour_Produit = relative_Tariff.copy(
                        laisse_Au_Gerant = false
                    ),
                    m10OperationVentCouleurs =
                        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                            .focused_ListM10OpeVentCouleur_Par_PD_M1Produit
                )
        }

        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
            .dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    val color = Color(0xFF4CAF50)
    val cancelColor = Color(0xFFFF5722)

    // Blinking animation state
    var isRedBackground by remember { mutableStateOf(false) }

    // Toggle state every 500ms for blinking effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(500L)
            isRedBackground = !isRedBackground
        }
    }

    // Animated colors
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isRedBackground) Color.Red else Color.White,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundColorAnimation"
    )

    val animatedTextColor by animateColorAsState(
        targetValue = if (isRedBackground) Color.White else Color.Red,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textColorAnimation"
    )

    val m10OperationVentCouleurs =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    val edited_Tariff = relative_Tariff?.copy(
        parent_M1Produit_KeyId = relative_M1Produit.keyID,
        parent_M1Produit_DebugInfos = relative_M1Produit.nom,
        parent_M2Client_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client?.keyID
            ?: "null",
        parent_M2Client_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client?.get_DebugInfos()
            ?: "null",
        parent_M8BonVent_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M8BonVent?.keyID
            ?: "null",
        parent_M8BonVent_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M8BonVent?.get_DebugInfos()
            ?: "null",
    )

    val currentApp_ItsWorkChezGrossisst =
        focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    // Now it's a horizontal row like other tariff buttons
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        currentApp_ItsWorkChezGrossisst.ifTrue {
            FloatingActionButton(
                modifier = Modifier.width(80.dp),
                onClick = {
                    onClickPrixButton()
                    save_Tariff_au_relative_vent_et_ferm_fabs_tariffs()
                },
                containerColor = animatedBackgroundColor
            ) {
                Text(
                    text = "تقدير",
                    color = animatedTextColor,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        if (showLabels) {
            ElevatedCard(
                Modifier.clickable {
                    onClickPrixButton()
                    save_Tariff_au_relative_vent_et_ferm_fabs_tariffs()
                }
            ) {
                val text = if (currentApp_ItsWorkChezGrossisst) "غ.م" else "لم يعطى سعر"
                val width = if (currentApp_ItsWorkChezGrossisst) 30.dp else 100.dp

                Text(
                    text = text,
                    maxLines = 2,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .width(width)
                        .background(animatedBackgroundColor)
                        .padding(4.dp),
                    color = animatedTextColor
                )
            }
        }

        currentApp_ItsWorkChezGrossisst.ifFalse {
            FloatingActionButton(
                modifier = Modifier.width(80.dp),
                onClick = {
                    onClickPrixButton()
                    save_Tariff_au_relative_vent_et_ferm_fabs_tariffs()
                },
                containerColor = animatedBackgroundColor
            ) {
                Text(
                    text = "تقدير",
                    color = animatedTextColor,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        // Cancellation button (smaller, positioned at the end)
        if (onClickAnulationButton != null) {
            FloatingActionButton(
                onClick = {
                    onClickAnulationButton()
                    focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeDialogSearchM1Produit()
                    focusedVarsHandlerFacade.focusedValuesSetter.set_Current_startTextSearchM1Produit(
                        ""
                    )
                    focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
                    focusedVarsHandlerFacade.focusedValuesSetter.desactive_CurrentApp_dialogAboveAll_OutlinedSearchListProduits()
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            affiche_Panier_au_Search_Dialog = false,
                            handled_M10OperationVent_Pour_Link = null
                        )
                    )
                },
                modifier = Modifier.size(40.dp),
                containerColor = cancelColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "إلغاء",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
