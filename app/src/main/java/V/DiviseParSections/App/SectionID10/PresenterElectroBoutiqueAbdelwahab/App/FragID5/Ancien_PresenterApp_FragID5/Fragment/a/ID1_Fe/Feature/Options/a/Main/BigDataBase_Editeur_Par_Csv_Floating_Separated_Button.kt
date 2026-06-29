package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main

import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clientjetpack.R
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.M03_Operations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.M8Bon_Operations_FragMap_DropdownMenu
import kotlin.math.roundToInt

private enum class DialState { Closed, ChildsVisible, M8Open, M03Open }

@Composable
fun FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(
    on_vent_key: String = "",
    onClick_Lence_Capture: (() -> Unit)? = null,
    onClick_Affiche_Pub: () -> Unit = {},
    context: Context = LocalContext.current,
    appDatabase: AppDatabase,
    viewModel: FeatureID1_ViewModel = viewModel(
        factory = viewModelFactory { initializer { FeatureID1_ViewModel(appDatabase = appDatabase) } }
    ),
) {
    val haptic = LocalHapticFeedback.current
    var dialState by remember { mutableStateOf(DialState.Closed) }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val fabSizePx = with(density) { 58.dp.toPx() }
    val paddingPx = with(density) { 16.dp.toPx() }

    // Initial position: bottom-right corner (mirrors A_FastAdd_FloatingSeparated_Button_1)
    var offsetX by remember { mutableFloatStateOf(screenWidthPx - fabSizePx - paddingPx) }
    var offsetY by remember { mutableFloatStateOf(screenHeightPx - fabSizePx * 5f) }

    val logoRotation by animateFloatAsState(
        targetValue = if (dialState != DialState.Closed) 45f else 0f,
        animationSpec = tween(250),
        label = "logoRot",
    )

    // ── Overlay plein écran, FAB draggable ───────────────────────────────────
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(
                            0f,
                            screenWidthPx - fabSizePx - paddingPx
                        )
                        offsetY = (offsetY + dragAmount.y).coerceIn(
                            0f,
                            screenHeightPx - fabSizePx - paddingPx
                        )
                    }
                }
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            // ── Boutons enfants — apparaissent au-dessus du FAB principal ─────
            AnimatedVisibility(
                visible = dialState != DialState.Closed,
                enter = fadeIn(tween(200)) + slideInVertically(tween(220)) { it },
                exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it },
            ) {
                val showEditedProducts = remember { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // ── Afficher les édités ────────────────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Afficher édités",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE91E63).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    showEditedProducts.value = !showEditedProducts.value
                                },
                                modifier = Modifier.size(46.dp),
                                containerColor = if (showEditedProducts.value) Color(0xFFE91E63) else Color(
                                    0xFF757575
                                ),
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector = if (showEditedProducts.value) Icons.Default.Edit else Icons.Default.EditOff,
                                    contentDescription = "Edit Switch",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }

                    // ── M8 BonVent ────────────────────────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Affiche Pub",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1565C0).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onClick_Affiche_Pub()
                                },
                                modifier = Modifier.size(46.dp),
                                containerColor = Color(0xFF1565C0),
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Public",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            // dropdown ancré sur ce Box
                            M8Bon_Operations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M8Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }

                    // ── M8 BonVent ────────────────────────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "M8 BonVent",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1565C0).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M8Open
                                },
                                modifier = Modifier.size(46.dp),
                                containerColor = Color(0xFF1565C0),
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AllInbox,
                                    contentDescription = "M8",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            // dropdown ancré sur ce Box
                            M8Bon_Operations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M8Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }

                    // ── M03 Couleur ───────────────────────────────────────────
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "M03 Couleur",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))

                                .background(Color(0xFF6A1B9A).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M03Open
                                },
                                modifier = Modifier.size(46.dp),
                                containerColor = Color(0xFF6A1B9A),
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "M03",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp),
                                )
                            }

                            M03_Operations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M03Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }
                }
            }

            // ── FAB principal — logo / × ──────────────────────────────────────
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    dialState = if (dialState == DialState.Closed)
                        DialState.ChildsVisible
                    else
                        DialState.Closed
                },
                modifier = Modifier.size(58.dp),
                containerColor = Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(6.dp, 6.dp),
                shape = CircleShape,
            ) {
                if (dialState != DialState.Closed) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF37474F)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White,
                            modifier = Modifier
                                .size(26.dp)
                                .rotate(logoRotation),
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Menu",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape),
                    )
                }
            }
        }
    }
}
