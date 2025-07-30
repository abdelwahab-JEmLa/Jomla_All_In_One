// Fixed NavigationBarWithFab.kt
package V.DiviseParSections.App._0.Navigation

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import com.example.clientjetpack.R
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
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showDialogTests by remember { mutableStateOf(false) }
    val activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    var showFabDropdown by remember { mutableStateOf(false) }

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
                        // Handle DialogTests specially - show dialog instead of navigating
                        if (screen.route == Screen.DialogTests.route) {
                            showDialogTests = true
                        } else {
                            onNavigate(screen.route)
                        }
                    }
                )
            }
        }

        val its_Targeted_Frag =
            activeFragment == Screen.A_Clients_LocationGps

        Surface(
            modifier = Modifier
                .offset(y = (-28).dp)
                .size(56.dp),
            shape = CircleShape,
        ) {
            Box {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            when (its_Targeted_Frag) {
                                false -> onToggleFabVisibility()
                                true -> {
                                    showFabDropdown = true
                                }
                            }
                        },
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = if (isFabVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle FAB",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
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

        if (showFabDropdown) {
            Box(
                modifier = Modifier
                    .offset(y = (-90).dp)
                    .align(Alignment.BottomCenter)
            ) {
                DropdownMenu(
                    expanded = showFabDropdown,
                    onDismissRequest = { showFabDropdown = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropDownItem_2(
                        item_States = Item_States.get_Default(),
                        nomFun = "Supprimer par Période",
                        onDismissDropdown = { showFabDropdown = false },
                        onExecute = {
                            repo8BonVent.refresh_Datas()
                        }
                    )

                    DropDownItem_1(
                        viewModel = koinInject(),
                        nomFun = "Toggle Client Button",
                        onDismissDropdown = { showFabDropdown = false }
                    )
                }
            }
        }
    }
}

data class Item_States(
    val icon_imageVector: ImageVector =Icons.Default.Delete,
) {
    companion object {
        fun get_Default(): Item_States {
            return Item_States()
        }
    }
}

@Composable
private fun DropDownItem_2(
    item_States: Item_States,
    nomFun: String,
    onDismissDropdown: () -> Unit,
    onExecute: () -> Unit,
    context: Context = LocalContext.current
) {
    var is_Button_Pressed by remember { mutableStateOf(false) }
    var is_Button_Yellow by remember { mutableStateOf(false) }
    var deleteProgress by remember { mutableStateOf(0f) }

    // Animation pour le progress du bouton _Button_
    val animatedProgress by animateFloatAsState(
        targetValue = if (is_Button_Pressed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
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
                // Expressive_Button_Icon intégré directement dans DropDownItem_2
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
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    // Commence l'animation seulement si le bouton est jaune
                                    if (is_Button_Yellow) {
                                        is_Button_Pressed = true
                                    }
                                    tryAwaitRelease()
                                    if (deleteProgress < 1f) {
                                        is_Button_Pressed = false
                                    }
                                },
                                onTap = {
                                    // Premier clic : active l'état jaune
                                    if (!is_Button_Yellow) {
                                        is_Button_Yellow = true
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Fond du bouton
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

                    // Indicateur de progression pour le bouton _Button_
                    if (deleteProgress > 0f) {
                        // Cercle de progression
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val strokeWidth = 2.dp.toPx()
                            drawArc(
                                color = Color(0xFFDC2626), // Rouge pour delete
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
            },
            text = {
                Text(
                    nomFun,
                    color = when {
                        is_Button_Yellow -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            },
            onClick = {
                // Premier clic : active l'état jaune
                if (!is_Button_Yellow) {
                    is_Button_Yellow = true
                }
            }
        )
    }
}


@Composable
private fun DropDownItem_1(
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
