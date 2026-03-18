package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.A.ViewModel.ViewModelPresistantButtonsSec8FWinID1
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Extracted composable for the Panier (Cart) toggle FAB
 * Handles toggling the fast cart dialog and clearing focused tariff values
 */
@Composable
fun FloatingPanierToggleFAB(
    focusedValuesGetter: FocusedValuesGetter,
    focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    showLabels: Boolean,
    modifier: Modifier = Modifier.Companion
) {
    val isPanierOpen = focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie
    val currentActive_M9AppCompt = focusedValuesGetter.currentActive_M9AppCompt
    val affiche_Dialog_Fast_Affiche_Panie_App4 = currentActive_M9AppCompt?.affiche_Dialog_Fast_Affiche_Panie_App4
       val con =LocalContext.current
    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = isPanierOpen, key = SemanticsPropertyKey("isPanierOpen"))
            }
            .semantics(mergeDescendants = true) {
                set(value = affiche_Dialog_Fast_Affiche_Panie_App4, key = SemanticsPropertyKey("affiche_Dialog_Fast_Affiche_Panie_App4"))
            }
    ) {
        FloatingActionButton(
            modifier = Modifier.Companion
                .getSemanticsTag(currentActive_M9AppCompt, "")
                .size(40.dp),
            onClick = {
                val latestValues = focusedValuesGetter.active_Central_Values
                val newPanierState = !latestValues.affiche_Dialog_Fast_Affiche_Panie
                Log.d(
                    "FloatingPanierToggleFAB",
                    "onClick => affiche_Dialog_Fast_Affiche_Panie: ${latestValues.affiche_Dialog_Fast_Affiche_Panie} -> $newPanierState"
                )
                Log.d(
                    "FloatingPanierToggleFAB",
                    "onClick => affiche_Dialog_Fast_Affiche_Panie: $affiche_Dialog_Fast_Affiche_Panie_App4 -> $newPanierState"
                )

                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.update_activeCentralValues(
                    latestValues.copy(
                        affiche_Dialog_Fast_Affiche_Panie = true
                    )
                )

                affiche_Dialog_Fast_Affiche_Panie_App4?.let {
                    viewModel.update_M9(currentActive_M9AppCompt.copy(
                        affiche_Dialog_Fast_Affiche_Panie_App4 =!affiche_Dialog_Fast_Affiche_Panie_App4)
                    )
                }

                Log.d(
                    "FloatingPanierToggleFAB",
                    "after update => affiche_Dialog_Fast_Affiche_Panie: ${focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie}"
                )
                focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()
                vibrateOnUpdate(con)

            },
            containerColor = if (isPanierOpen) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            },
        ) {
            Icon(
                imageVector = Icons.Default.Paid,
                contentDescription = if (isPanierOpen) {
                    "Fermer Dialog Fast Affiche Panier"
                } else {
                    "Ouvrir Dialog Fast Affiche Panier"
                },
                tint = Color.Companion.White
            )
        }

        if (showLabels) {
            Text(
                text = if (isPanierOpen) {
                    "Fermer Panier"
                } else {
                    "Ouvrir Panier"
                },
                modifier = Modifier.Companion
                    .background(
                        color = if (isPanierOpen) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.Companion.White,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@SuppressLint("ObsoleteSdkInt")
@RequiresPermission(Manifest.permission.VIBRATE)
fun vibrateOnUpdate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(600L, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(600L)
    }
}
