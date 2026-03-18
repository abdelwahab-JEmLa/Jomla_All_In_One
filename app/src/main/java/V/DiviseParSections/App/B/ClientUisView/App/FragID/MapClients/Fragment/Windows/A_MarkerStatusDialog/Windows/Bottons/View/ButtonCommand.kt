package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View

import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Screen_NewProtoPattern
import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import EntreApps.Shared.Models.M8BonVent
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.vibrateOnUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.koin.compose.koinInject

private const val TAG = "CommandButton"

@Composable
fun CommandButton(
    aCentralFacade: ACentralFacade = koinInject(),
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    modifier: Modifier = Modifier,
    relative_M2Client: M2Client,
    relative_Etate: M8BonVent.EtateActuellementEst,
    viewModel: MapClientsViewModel,
    context: Context,
    onUpdateLongAppSetting: () -> Unit = {},
) {
    val found_Or_Default_M8BonVent =
        get_Found_Or_Default_M8BonVent(aCentralFacade, relative_M2Client, relative_Etate)
            ?: return
    val con =LocalContext.current
    FilledTonalButton(
        modifier = modifier
            .getSemanticsTag(found_Or_Default_M8BonVent.default_If_No_Found, "")
            .fillMaxWidth(),
        onClick = {
            if (found_Or_Default_M8BonVent.found != null) {
                aCentralFacade.repositorysMainSetter.update_M8BonVent(
                    found_Or_Default_M8BonVent.found
                )
            } else {
                aCentralFacade.repositorysMainSetter
                    .addNew_M8BonVent(found_Or_Default_M8BonVent.default_If_No_Found)
            }

            if (relative_Etate == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                aCentralFacade.focusedActiveValuesFacade
                    .focusedValuesSetter
                    .setIN_M9CurrentApp_onVentM8BonVentKey(
                        found_Or_Default_M8BonVent.found
                            ?: found_Or_Default_M8BonVent.default_If_No_Found
                    )
            }

            if (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
                    ?.c_Ouvert_Pour_Au_Command_Add_Period == true
            ) {
                viewModel.startRecordIfNot()
            }

            val currentActiveCentralValues = focusedValuesGetter.active_Central_Values
            val catalogueId =
                focusedValuesGetter.currentActive_M9AppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

            val updatedActiveCentralValues = when (catalogueId) {
                "t1" -> currentActiveCentralValues.copy(
                    pourcentage_AffichageDuCatalogue_Conficerie = 100.0,
                    pourcentage_AffichageDuCatalogue_Cosmitiques = 0.0,
                    pourcentage_AffichageDuCatalogue_tebnage = 0.0,
                )

                "t2" -> currentActiveCentralValues.copy(
                    pourcentage_AffichageDuCatalogue_Cosmitiques = 100.0,
                    pourcentage_AffichageDuCatalogue_Conficerie = 0.0,
                    pourcentage_AffichageDuCatalogue_tebnage = 0.0,
                )

                "t3" -> currentActiveCentralValues.copy(
                    pourcentage_AffichageDuCatalogue_tebnage = 100.0,
                    pourcentage_AffichageDuCatalogue_Cosmitiques = 0.0,
                    pourcentage_AffichageDuCatalogue_Conficerie = 0.0,
                )

                else -> currentActiveCentralValues.copy()
            }
            vibrateOnUpdate(con)

            focusedValuesGetter.update_activeCentralValues(updatedActiveCentralValues)

            if (M18CentralParametresOfAllApps.get_Default().its_AppType == AppType.AllInOne) {
                viewModel.updateLongAppSetting(relative_M2Client.id)
                onUpdateLongAppSetting()
            } else {
                // FIX TODO(1): was calling updateCurrentFragment() which only updates the state
                // flow but never invokes the NavController — so the UI state changed but the
                // screen never actually changed. navigateTo() does both: it updates
                // _currentFragment AND calls _navController?.navigate(...).
                Log.d(TAG, "navigateTo Compact_Presentoire_App_Produits_FragID4")
                fragmentNavigationHandler.navigateTo(
                    Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4
                )
            }



        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(
                ContextCompat.getColor(
                    context,
                    relative_Etate.color
                )
            ).copy(alpha = 0.2f),
            contentColor = Color(
                ContextCompat.getColor(
                    context,
                    relative_Etate.color
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Mode Commande",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(relative_Etate.nomArabe)
        }
    }
}
