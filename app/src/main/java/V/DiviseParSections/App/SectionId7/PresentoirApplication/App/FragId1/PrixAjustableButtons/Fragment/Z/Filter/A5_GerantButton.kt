package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.SortedMap

@Composable
fun GerantButton(
    relative_Tariff: M13TarificationInfos?,
    relative_M1Produit: ArticlesBasesStatsTable,
    viewModel: TariffsButtonsViewModelSec7ID2,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    showLabels: Boolean,
    tariffsGroupedByType: SortedMap<M13TarificationInfos.TypeChoisi, List<M13TarificationInfos>>,
    onClickPrixButton: () -> Unit,
    onClickAnulationButton: (() -> Unit)? = null,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedVarsHandlerFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    focusedValuesGetter: FocusedValuesGetter = focusedVarsHandlerFacade.focusedValuesGetter,
) {
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

    val gerantButtonHeight = remember(tariffsGroupedByType) {
        val fabButtonSize = 50
        val spacerBetweenItems = 4
        val numberOfTariffTypes = tariffsGroupedByType.size
        val totalItemsHeight = numberOfTariffTypes * fabButtonSize
        val totalSpacersHeight = if (numberOfTariffTypes > 1) {
            (numberOfTariffTypes - 1) * spacerBetweenItems
        } else 0
        val extraPadding = 16
        val calculatedHeight = totalItemsHeight + totalSpacersHeight + extraPadding
        val minHeight = 60
        maxOf(calculatedHeight, minHeight).dp
    }

    val m10OperationVentCouleurs =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit

    val edited_Tariff = relative_Tariff?.copy(
        parent_M1Produit_KeyId = relative_M1Produit.keyID,
        parent_M1Produit_DebugInfos = relative_M1Produit.nom,
        parent_M2Client_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client?.keyID
            ?: "null",
        parent_M2Client_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M2Client?.get_DebugInfos()
            ?: "null",
        parent_M8BonVent_KeyId = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeonVent_M8BonVent?.keyID
            ?: "null",
        parent_M8BonVent_DebugInfos = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeonVent_M8BonVent?.get_DebugInfos()
            ?: "null",
    )

    fun handelClick() {
        viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
            m13TarificationInfos_Pour_Produit = edited_Tariff,
            m10OperationVentCouleurs = m10OperationVentCouleurs
        )

        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.dismisses_By_toggle_CurrentApp_activeDialogSearchM1Produit()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showLabels) {
                    ElevatedCard(
                        Modifier.clickable {
                            onClickPrixButton()
                            handelClick()
                        }) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(animatedBackgroundColor)
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                                .height(gerantButtonHeight)
                                .width(30.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val fontSize = 12.sp

                                Text(
                                    text = "التقدير",
                                    maxLines = 1,
                                    fontSize = fontSize,
                                    modifier = Modifier.rotate(-90f),
                                    color = animatedTextColor
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "للمدير",
                                    maxLines = 1,
                                    fontSize = fontSize,
                                    modifier = Modifier.rotate(-90f),
                                    color = animatedTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Cancellation button positioned at top-right with proper padding
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
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
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
}
