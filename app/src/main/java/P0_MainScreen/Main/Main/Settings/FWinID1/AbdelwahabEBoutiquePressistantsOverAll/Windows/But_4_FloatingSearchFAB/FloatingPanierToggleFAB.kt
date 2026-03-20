package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB

import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.Z_AppCompt
import EntreApps.Shared.Modules.Base.AppDatabase
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun FloatingPanierToggleFAB(
    focusedValuesGetter: FocusedValuesGetter,
    focusedVarsHandlerFacade: FocusedActiveValuesFacade,
    viewModel: ViewModelPresistantButtonsSec8FWinID1,
    showLabels: Boolean,
    appDatabase: AppDatabase= koinInject (),
    modifier: Modifier = Modifier.Companion
) {
    val isPanierOpen = focusedValuesGetter.active_Central_Values.affiche_Dialog_Fast_Affiche_Panie

    // Observe Room via Flow → MutableState réactif, se recompose automatiquement à chaque update BDD
    val allAppCompts by appDatabase.dao_M9AppCompt().getAllFlow().collectAsState(initial = emptyList())
    val m9: Z_AppCompt? = allAppCompts.find {
        it.keyID == M18CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
    }
    val affiche_Dialog_Fast_Affiche_Panie_App4: Boolean? = m9?.affiche_Dialog_Fast_Affiche_Panie_App4

    val con =LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = isPanierOpen, key = SemanticsPropertyKey("isPanierOpen"))
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = affiche_Dialog_Fast_Affiche_Panie_App4,
                    key = SemanticsPropertyKey("affiche_Dialog_Fast_Affiche_Panie_App4")
                )
            }
    ) {
        FloatingActionButton(
            modifier = Modifier.Companion
                .getSemanticsTag(affiche_Dialog_Fast_Affiche_Panie_App4, "")
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

                viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.update_activeCentralValues(
                    latestValues.copy(
                        affiche_Dialog_Fast_Affiche_Panie = true
                    )
                )

                affiche_Dialog_Fast_Affiche_Panie_App4?.let {
                    coroutineScope.launch {
                        appDatabase.dao_M9AppCompt().update(
                            m9.copy(
                                affiche_Dialog_Fast_Affiche_Panie_App4 = !affiche_Dialog_Fast_Affiche_Panie_App4
                            )
                        )
                    }
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

// NOTE: dao_M9AppCompt() doit exposer getAllAsFlow(): Flow<List<Z_AppCompt>>
// Exemple dans le DAO:
//   @Query("SELECT * FROM z_app_compt")
//   fun getAllAsFlow(): Flow<List<Z_AppCompt>>

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
