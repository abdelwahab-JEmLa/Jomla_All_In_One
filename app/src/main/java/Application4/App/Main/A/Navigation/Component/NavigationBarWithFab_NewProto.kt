package Application4.App.Main.A.Navigation.Component

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu
import V.DiviseParSections.App._0.Navigation.Main_DropDown.Panie.FabDropdownMenu_WhenIts_Frag_Panie
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import kotlinx.coroutines.delay

@Composable
fun NavigationBarWithFab_NewProto(
    items: List<Screen_NewProtoPattern>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    showWarningState: Boolean = true,
    onClickImageToShowControles: () -> Unit = {},
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
) {
    val its_Panier = currentRoute == Screen_NewProtoPattern.Panier.route
    val its_A_Clients_LocationGps =
        currentRoute == Screen_NewProtoPattern.A_Clients_LocationGps.route
    val its_Compact_Presentoire =
        currentRoute == Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route

    var showFabDropdown_Panier by remember { mutableStateOf(false) }
    var showFabDropdown_Compact_Presentoire_App_Produits_FragID4 by remember { mutableStateOf(false) }

    var affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon by remember { mutableStateOf(false) }
    var snoozeActive by remember { mutableStateOf(false) }

    var showFabDropdown_Gps by remember { mutableStateOf(false) }

    // Re-show the dialog after 30 seconds when snoozed
    val scope = rememberCoroutineScope()
    LaunchedEffect(snoozeActive) {
        if (snoozeActive) {
            delay(30_000L)
            snoozeActive = false
            affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon = true
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            val middleIndex = items.size / 2
            items.forEachIndexed { index, screen ->
                if (index == middleIndex) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Box(Modifier.size(48.dp)) },
                        enabled = false
                    )
                }
                NavigationBarItem(
                    icon = {
                        if (screen.customIconRes != null) {
                            Image(
                                painter = painterResource(id = screen.customIconRes),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                colorFilter = if (currentRoute == screen.route) ColorFilter.tint(
                                    screen.color
                                )
                                else ColorFilter.tint(LocalContentColor.current.copy(alpha = ContentAlpha.medium))
                            )
                        } else {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (currentRoute == screen.route) screen.color
                                else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                            )
                        }
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        try {
                            onNavigate(screen.route)
                        } catch (e: IllegalStateException) {
                        }
                    }
                )
            }
        }

        when {
            its_Compact_Presentoire -> FabButton_newProto(
                showWarningState = showWarningState,
                isFabVisible = isFabVisible,
                its_Targeted_Frag = true,   // routes click through onShowDropdown
                onToggleFabVisibility = {},
                onShowDropdown = { showFabDropdown_Compact_Presentoire_App_Produits_FragID4 = true }
            )

            its_Panier -> FabButton_newProto(
                showWarningState = showWarningState, isFabVisible = isFabVisible,
                its_Targeted_Frag = true,
                onToggleFabVisibility = {
                    showFabDropdown_Panier = true
                },
                onShowDropdown = {
                    showFabDropdown_Panier = true
                }
            )

            else -> FabButton_newProto(
                showWarningState = showWarningState,
                isFabVisible = isFabVisible,
                its_Targeted_Frag = its_A_Clients_LocationGps,
                onToggleFabVisibility = onToggleFabVisibility,
                onShowDropdown = { showFabDropdown_Gps = true }
            )
        }



        if (showFabDropdown_Panier && its_Panier) {
            FabDropdownMenu_WhenIts_Frag_Panie(
                onDismissDropdown = {
                    showFabDropdown_Panier = false
                },
                onClick_to_initiateBackgroundPdfCreation = {
                    showFabDropdown_Panier = false
                    affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon = true
                },
                onClickImageToShowControles = onClickImageToShowControles
            )
        }

        if (showFabDropdown_Gps && its_A_Clients_LocationGps) {
            FabDropdownMenu(
                showFabDropdown = true,
                onDismissDropdown = { showFabDropdown_Gps = false },
            )
        }

        if (affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon) {
            AlertDialog(
                onDismissRequest = {
                    affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon = false
                },
                title = {
                    Text(text = "PDF généré ✅")
                },
                text = {
                    Text(text = "Le bon de commande a été généré avec succès.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon = false
                        snoozeActive = true
                    }) {
                        Text("Snooze (30s)")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        affiche_Win_La_Generation_Pdf_Est_Termine_du_Bon = false
                        snoozeActive = false
                    }) {
                        Text("Fermer")
                    }
                }
            )
        }
    }
}

data class Item_States(
    val function_noms_separatedStrings: String = ",",
    val avec_Premier_Click_Jane: Boolean = true,
    val time_pressing_millis: Int = 1000,
    val icon_imageVector: ImageVector = Icons.Default.Close,
) {
    companion object {
        fun get_Default() = Item_States()
    }
}
