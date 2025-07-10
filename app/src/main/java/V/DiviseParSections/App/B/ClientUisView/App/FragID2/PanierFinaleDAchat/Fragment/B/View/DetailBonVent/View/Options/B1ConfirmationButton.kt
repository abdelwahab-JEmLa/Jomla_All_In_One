package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.A.ViewModel.ZViewModel_Sec1Frag3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter.Companion.getSemanticsTagFocucedVars
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmationButton(
    showLabel: Boolean,
    viewModel: ZViewModel_Sec1Frag3,
) {
    val currentBonVent = viewModel.aCentral.focusedActiveValuesFacade.focusedValuesGetter.onVentM8BonVent

    fun updateBonVent(data: M8BonVent, newEtate: M8BonVent.EtateActuellementEst) =
        viewModel.aCentral.repositorysMainSetter.upsertM8BonVent(
            data.copy(
                etateActuellementEst = newEtate
            )
        )

    // Helper function to get color from enum
    @Composable
    fun getStateColor(state: M8BonVent.EtateActuellementEst?): Color {
        return state?.let {
            colorResource(it.color)
        } ?: Color(0xFF9E9E9E) // Default gray if state is null
    }

    // Helper function to get appropriate icon based on state
    fun getStateIcon(state: M8BonVent.EtateActuellementEst?): ImageVector {
        return when (state) {
            M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> Icons.Default.CheckCircle
            M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> Icons.Default.Cancel
            M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> Icons.Default.PlayArrow
            else -> Icons.AutoMirrored.Filled.HelpOutline
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val aCommandeConfirme = M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
        val onModeCommendActuellement = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        val etateActuellementEst = currentBonVent?.etateActuellementEst
            ?: M8BonVent.EtateActuellementEst.CreeMaisNonDefinie

        if (etateActuellementEst == aCommandeConfirme || etateActuellementEst == onModeCommendActuellement) {
            FloatingActionButton(
                modifier = Modifier
                    .getSemanticsTagFocucedVars(viewModel.aCentral.focusedActiveValuesFacade.focusedValuesGetter)
                    .size(48.dp),
                onClick = {
                    currentBonVent?.let { bonVent ->
                        when (bonVent.etateActuellementEst) {
                            aCommandeConfirme -> {
                                updateBonVent(
                                    bonVent,
                                    onModeCommendActuellement
                                )
                                viewModel.aCentral.focusedActiveValuesFacade.focusedValuesSetter.active_currentApp_M8BonVent(
                                    bonVent
                                )
                            }

                            onModeCommendActuellement -> {
                                updateBonVent(
                                    bonVent,
                                    aCommandeConfirme
                                )
                                viewModel.aCentral.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                            }

                            else -> {
                                updateBonVent(
                                    bonVent,
                                    aCommandeConfirme
                                )
                            }
                        }
                    }
                },
                containerColor = getStateColor(currentBonVent?.etateActuellementEst)
            ) {
                Icon(
                    imageVector = getStateIcon(currentBonVent?.etateActuellementEst),
                    contentDescription = when (currentBonVent?.etateActuellementEst) {
                        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> "Annuler la confirmation"
                        M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> "Confirmer la commande"
                        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> "Commande en cours"
                        else -> "Gérer la commande"
                    },
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }


            if (showLabel) {
                Text(
                    text = when (currentBonVent?.etateActuellementEst) {
                        M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> etateActuellementEst.nomArabe
                        M8BonVent.EtateActuellementEst.CreeMaisNonDefinie -> etateActuellementEst.nomArabe
                        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT -> etateActuellementEst.nomArabe
                        else -> currentBonVent?.etateActuellementEst?.nomArabe ?: "حالة أخرى"
                    },
                    modifier = Modifier
                        .background(
                            color = getStateColor(currentBonVent?.etateActuellementEst),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
