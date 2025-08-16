// Fixed NavigationBarWithFab.kt
package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabButton_When_ItsEditeBaseDonne
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabDropdownMenu_BaseDonneEdite
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import org.koin.compose.koinInject

private const val TAG = "NavigationBarWithFab"

@Composable
fun NavigationBarWithFab(
    viewModelInitApp: ViewModelInitApp,
    aCentralFacade: ACentralFacade = viewModelInitApp.aCentralFacade,
    repo8BonVent: Repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showWarningState: Boolean = true // New parameter to control warning display
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showDialogTests by remember { mutableStateOf(false) }
    val activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    var showFabDropdown by remember { mutableStateOf(false) }
    var showFabDropdownBaseDonne by remember { mutableStateOf(false) }

    // Debug: Add logging to see current state
    LaunchedEffect(activeFragment) {
        println("DEBUG: Current active fragment: $activeFragment")
    }

    LaunchedEffect(showFabDropdownBaseDonne) {
        println("DEBUG: showFabDropdownBaseDonne state: $showFabDropdownBaseDonne")
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            // Calculate middle index
            val middleIndex = items.size / 2

            items.forEachIndexed { index, screen ->
                if (index == middleIndex) {
                    // Add empty space for FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Box(modifier = Modifier.size(48.dp)) },
                        enabled = false
                    )
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (currentRoute == screen.route) screen.color
                            else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (screen.route == Screen.DialogTests.route) {
                            showDialogTests = true
                        } else {
                            onNavigate(screen.route)
                        }
                    }
                )
            }
        }

        val its_Targeted_Frag = activeFragment == Screen.A_Clients_LocationGps
        val its_EditDatabaseWithCreateNewArticles = activeFragment == Screen.EditDatabaseWithCreateNewArticles

        // FIXED: Always show one FAB button based on current fragment
        if (its_EditDatabaseWithCreateNewArticles) {
            // Show special FAB button for edit database mode
            FabButton_When_ItsEditeBaseDonne(
                showWarningState = showWarningState,
                isFabVisible = isFabVisible,
                its_Targeted_Frag = true, // FIXED: Set to true for dropdown functionality
                onToggleFabVisibility = onToggleFabVisibility,
                onShowDropdown = {
                    println("DEBUG: Setting showFabDropdownBaseDonne to true")
                    showFabDropdownBaseDonne = true
                }
            )
        } else {
            // Show regular FAB button for normal mode
            FabButton(
                showWarningState = showWarningState,
                isFabVisible = isFabVisible,
                its_Targeted_Frag = its_Targeted_Frag,
                onToggleFabVisibility = onToggleFabVisibility,
                onShowDropdown = { showFabDropdown = true }
            )
        }

        if (showCatalogDialog) {
            CatalogSelectionDialog(
                onDismiss = {
                    showCatalogDialog = false
                },
                onCatalogSelected = { categoryId ->
                    onCatalogSelected(categoryId)
                    showCatalogDialog = false
                    onNavigate(Screen.FacadePresentoireProduits.route)
                },
                viewModelInitApp = viewModelInitApp
            )
        }

        if (showDialogTests) {
            TestScreens(
                onDismiss = { showDialogTests = false },
                fragmentNavigationHandler = fragmentNavigationHandler
            )
        }

        // Regular dropdown menu for normal mode
        if (showFabDropdown && !its_EditDatabaseWithCreateNewArticles) {
            FabDropdownMenu(
                showFabDropdown = showFabDropdown,
                onDismissDropdown = { showFabDropdown = false },
                repo8BonVent = repo8BonVent
            )
        }

        // FIXED: Database edit dropdown menu - removed conflicting condition
        if (showFabDropdownBaseDonne && its_EditDatabaseWithCreateNewArticles) {
            println("DEBUG: Showing FabDropdownMenu_BaseDonneEdite")
            FabDropdownMenu_BaseDonneEdite(
                onDismissDropdown = {
                    println("DEBUG: Dismissing FabDropdownMenu_BaseDonneEdite")
                    showFabDropdownBaseDonne = false
                }
            )
        }
    }
}

@Composable
fun DropDownItem_Displaye_TogleFilterMarquers(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isFilterMarkersVisible = currentValues.affiche_Floating_Button_TogleFilterMarquers

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFilterMarkersVisible)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (isFilterMarkersVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Filter Markers",
                    tint = if (isFilterMarkersVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            text = {
                Text(
                    text = if (isFilterMarkersVisible)
                        "Hide Filter Markers"
                    else
                        "Show Filter Markers",
                    color = if (isFilterMarkersVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                // Toggle the filter markers visibility
                val newValues = currentValues.copy(
                    affiche_Floating_Button_TogleFilterMarquers = !isFilterMarkersVisible
                )
                focusedValuesGetter.update_activeCentralValues(newValues)
            }
        )
    }
}

@Composable
fun DropDownItem_DisplayeGpsFlowFAB(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isGpsButtonVisible = currentValues.affiche_Floating_Button_gps_follow_mode_active

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGpsButtonVisible)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = if (isGpsButtonVisible) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed,
                    contentDescription = "Toggle GPS Button Visibility",
                    tint = if (isGpsButtonVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            text = {
                Text(
                    text = if (isGpsButtonVisible) "Hide GPS Button" else "Show GPS Button",
                    color = if (isGpsButtonVisible)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onClick = {
                val newValues = currentValues.copy(
                    affiche_Floating_Button_gps_follow_mode_active = !isGpsButtonVisible
                )
                focusedValuesGetter.update_activeCentralValues(newValues)
            }
        )
    }
}

data class Item_States(
    val function_noms_separatedStrings: String = ",",
    val avec_Premier_Click_Jane: Boolean = true,
    val time_pressing_millis: Int = 1000,
    val icon_imageVector: ImageVector = Icons.Default.Close,
) {
    companion object {
        fun get_Arab_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(1) ?: ""
        }

        fun get_English_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(0) ?: ""
        }

        fun extract_Noms(function_noms_separatedStrings: String): List<String> {
            return function_noms_separatedStrings.split(",").map { it.trim() }
        }

        fun get_Default(): Item_States {
            return Item_States()
        }
    }
}

@Composable
fun DropDownItem_2(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    item_States: Item_States,
    onDismissDropdown: () -> Unit,
    onExecute: () -> Unit,
    context: Context = LocalContext.current
) {
    val currentApp_Not_Admin = !focusedValuesGetter.currentApp_Est_Admin

    // Use Arabic name if not admin, otherwise use English name
    val displayText = if (currentApp_Not_Admin) {
        Item_States.get_Arab_Nom(item_States.function_noms_separatedStrings)
    } else {
        Item_States.get_English_Nom(item_States.function_noms_separatedStrings)
    }

    var is_Button_Pressed by remember { mutableStateOf(false) }
    var is_Button_Yellow by remember { mutableStateOf(false) }
    var deleteProgress by remember { mutableStateOf(0f) }

    // Animation pour le progress du bouton
    val animatedProgress by animateFloatAsState(
        targetValue = if (is_Button_Pressed) 1f else 0f,
        animationSpec = tween(durationMillis = item_States.time_pressing_millis),
        finishedListener = { progress ->
            if (progress == 1f && is_Button_Pressed) {
                // Security check and execution
                onExecute()
                // Show Toast
                Toast.makeText(context, "Opération terminée avec succès", Toast.LENGTH_SHORT).show()
                is_Button_Pressed = false
                is_Button_Yellow = false
                onDismissDropdown()
            }
        }
    )

    LaunchedEffect(animatedProgress) {
        deleteProgress = animatedProgress
    }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                ExpressiveButtonIcon(
                    item_States = item_States,
                    is_Button_Pressed = is_Button_Pressed,
                    is_Button_Yellow = is_Button_Yellow,
                    deleteProgress = deleteProgress,
                    onButtonPressed = { pressed -> is_Button_Pressed = pressed },
                    onButtonYellow = { yellow -> is_Button_Yellow = yellow }
                )
            },
            text = {
                Text(
                    displayText,
                    color = when {
                        is_Button_Yellow -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            },
            onClick = {
                // Handle click based on avec_Premier_Click_Jane setting
                if (item_States.avec_Premier_Click_Jane) {
                    // Original behavior: first click activates yellow state
                    if (!is_Button_Yellow) {
                        is_Button_Yellow = true
                    }
                } else {
                    // Skip yellow state: go directly to hold action
                    is_Button_Yellow = true
                }
            }
        )
    }
}

@Composable
private fun ExpressiveButtonIcon(
    item_States: Item_States,
    is_Button_Pressed: Boolean,
    is_Button_Yellow: Boolean,
    deleteProgress: Float,
    onButtonPressed: (Boolean) -> Unit,
    onButtonYellow: (Boolean) -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (is_Button_Pressed || is_Button_Yellow) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
            .clip(CircleShape)
            .pointerInput(is_Button_Yellow) { // Add key to restart gesture detection when state changes
                detectTapGestures(
                    onPress = {
                        // Only start hold animation if button is in yellow state
                        if (is_Button_Yellow) {
                            onButtonPressed(true)
                            // Wait for release or timeout
                            val released = tryAwaitRelease()
                            // If user released before animation completed, stop the animation
                            if (released && deleteProgress < 1f) {
                                onButtonPressed(false)
                            }
                        }
                    },
                    onTap = {
                        // Handle initial tap to activate yellow state
                        if (item_States.avec_Premier_Click_Jane && !is_Button_Yellow) {
                            onButtonYellow(true)
                        } else if (!item_States.avec_Premier_Click_Jane) {
                            // Skip yellow state and go directly to hold mode
                            onButtonYellow(true)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Button background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when {
                        is_Button_Yellow -> Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFBBF24),
                                Color(0xFFF59E0B)
                            )
                        )

                        else -> Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent
                            )
                        )
                    },
                    shape = CircleShape
                )
        )

        // Progress indicator for hold action
        if (deleteProgress > 0f && is_Button_Pressed) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val strokeWidth = 2.dp.toPx()
                drawArc(
                    color = Color(0xFFDC2626), // Red for delete/action
                    startAngle = -90f,
                    sweepAngle = 360f * deleteProgress,
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    ),
                    size = Size(
                        size.width - strokeWidth,
                        size.height - strokeWidth
                    ),
                    topLeft = Offset(
                        strokeWidth / 2,
                        strokeWidth / 2
                    )
                )
            }
        }

        Icon(
            imageVector = item_States.icon_imageVector,
            contentDescription = null,
            tint = when {
                is_Button_Yellow -> Color.White
                else -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun DropDownItem_1(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    nomFun: String,
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Execute function",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = { Text(nomFun) },
            onClick = {
                val currentValues = focusedValuesGetter.active_Central_Values
                val newValues = currentValues.copy(
                    affiche_Floating_Button_Cible_Client = !currentValues.affiche_Floating_Button_Cible_Client
                )
                focusedValuesGetter.update_activeCentralValues(newValues)
                onDismissDropdown()
            }
        )
    }
}
