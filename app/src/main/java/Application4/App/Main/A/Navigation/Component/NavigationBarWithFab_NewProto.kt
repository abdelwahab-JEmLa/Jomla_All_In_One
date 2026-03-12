package Application4.App.Main.A.Navigation.Component

import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu
import V.DiviseParSections.App._0.Navigation.Main_DropDown.Panie.FabDropdownMenu_WhenIts_Frag_Panie
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.FabDropdownMenu_WhenIts_FacadeBoutiqueElectro
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
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
) {
    val its_Panier = currentRoute == Screen_NewProtoPattern.Panier.route
    val its_A_Clients_LocationGps =
        currentRoute == Screen_NewProtoPattern.A_Clients_LocationGps.route
    val its_Compact_Presentoire =
        currentRoute == Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4.route

    var showFabDropdown_Panier by remember { mutableStateOf(false) }
    var showFabDropdown_Gps by remember { mutableStateOf(false) }
    var showFabDropdown_MainPresenterFragment by remember { mutableStateOf(false) }

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

        val scope = rememberCoroutineScope()

        when {
            its_Compact_Presentoire -> FabButton_newProto(
                showWarningState = showWarningState, isFabVisible = isFabVisible,
                its_Targeted_Frag = true, onToggleFabVisibility = onToggleFabVisibility,
                onShowDropdown = { showFabDropdown_MainPresenterFragment = true }
            )

            // FIX: replace onToggleFabVisibility with a lambda that unconditionally opens
            // the dropdown, so the menu appears on every FAB press on the Panier screen.
            its_Panier -> FabButton_newProto(
                showWarningState = showWarningState, isFabVisible = isFabVisible,
                its_Targeted_Frag = true,
                onToggleFabVisibility = {
                    Log.d("FAB_PANIER", "▶ onToggleFabVisibility called → showFabDropdown_Panier = true")
                    showFabDropdown_Panier = true
                },
                onShowDropdown = {
                    Log.d("FAB_PANIER", "▶ onShowDropdown called → showFabDropdown_Panier = true")
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
            Log.d("FAB_PANIER", "✅ FabDropdownMenu_WhenIts_Frag_Panie — affiché")
            FabDropdownMenu_WhenIts_Frag_Panie(
                onDismissDropdown = {
                    Log.d("FAB_PANIER", "❌ dropdown dismissed → showFabDropdown_Panier = false")
                    showFabDropdown_Panier = false
                },
                onClick_to_initiateBackgroundPdfCreation = {
                    showFabDropdown_Panier = false
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

        if (showFabDropdown_MainPresenterFragment && its_Compact_Presentoire) {
            FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
                onDismissDropdown = { showFabDropdown_MainPresenterFragment = false },
                onClickImageToShowControles = onClickImageToShowControles
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
