package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import org.osmdroid.views.MapView
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_2(
    mapView: MapView,
    aCentralFacade: ACentralFacade = koinInject(),
    viewModel: MapClientsViewModel,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "GPS Follow Mode",
        icons = Pair(Icons.Default.GpsNotFixed, Icons.Default.GpsFixed),
        colors = Pair(Color.Gray, Color.Blue)
    )
) {
    val currentValues = focusedValuesGetter.active_Central_Values
    val isGpsFollowActive = currentValues.gps_follow_mode_active ?: false

    val updatedButtonState = buttonState.copy(its_Active = isGpsFollowActive)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 200f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y

                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 100f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (updatedButtonState.showLabels) {
                    Text(
                        text = if (isGpsFollowActive) "GPS Active" else "GPS Inactive",
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                color = if (updatedButtonState.its_Active)
                                    updatedButtonState.colors.second.copy(alpha = 0.8f)
                                else
                                    updatedButtonState.colors.first.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FloatingActionButton(
                    modifier = Modifier
                        .size(48.dp),
                    onClick = {
                        val newValues = currentValues.copy(
                            gps_follow_mode_active = !isGpsFollowActive
                        )
                        focusedValuesGetter.update_activeCentralValues(newValues)
                    },
                    containerColor = if (updatedButtonState.its_Active)
                        updatedButtonState.colors.second
                    else
                        updatedButtonState.colors.first
                ) {
                    Icon(
                        imageVector = if (updatedButtonState.its_Active)
                            updatedButtonState.icons.second // GpsFixed when active
                        else
                            updatedButtonState.icons.first, // GpsNotFixed when inactive
                        contentDescription = if (isGpsFollowActive) "Disable GPS Follow" else "Enable GPS Follow",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // ── Reload FAB ────────────────────────────────────────────────────────
                FloatingActionButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        val center = mapView.mapCenter
                        Log.d("ProximityFilter", "=== FAB Reload clicked ===")
                        Log.d("ProximityFilter", "  mapView.mapCenter → lat=${center.latitude}  lng=${center.longitude}")
                        if (center.latitude == 0.0 && center.longitude == 0.0) {
                            Log.w("ProximityFilter", "  ⚠️ centre (0,0) — la map n'est peut-être pas encore initialisée !")
                        }
                        viewModel.relod_map_marques_du_3km_du_centre_map(
                            centerLat = center.latitude,
                            centerLng = center.longitude,
                        )
                    },
                    containerColor = Color(0xFF1565C0),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload markers within 3 km",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // ── Speed threshold input ──────────────────────────────────────────────
                // Outlined text field whose initial value is the current scrollSpeedThresholdMps.
                // Border = red while the user is editing (dirty), green once the value is applied.
                SpeedThresholdField(viewModel = viewModel)
            }
        }
    }
}


/**
 * Outlined text field that lets the user edit [MapClientsViewModel.scrollSpeedThresholdMps] live.
 *
 * Colour logic:
 *   - Red border  → the typed text differs from the applied value (dirty / en cours d'édition)
 *   - Green border → the typed value has been applied (confirmed with Done / Enter)
 */
@Composable
private fun SpeedThresholdField(viewModel: MapClientsViewModel) {
    // Local draft text — initialised from the ViewModel value
    var draftText by remember { mutableStateOf(viewModel.scrollSpeedThresholdMps.toString()) }
    // True while the draft differs from the applied ViewModel value
    val isDirty = draftText.toDoubleOrNull() != viewModel.scrollSpeedThresholdMps

    val borderColor = when {
        isDirty -> Color.Red    // editing — not yet applied
        else    -> Color.Green  // matches the applied value
    }

    fun applyValue() {
        val parsed = draftText.toDoubleOrNull()
        if (parsed != null && parsed > 0.0) {
            viewModel.scrollSpeedThresholdMps = parsed
            Log.d("SpeedThreshold", "scrollSpeedThresholdMps mis à jour → $parsed m/s")
        } else {
            // Revert draft to last valid value
            draftText = viewModel.scrollSpeedThresholdMps.toString()
        }
    }

    OutlinedTextField(
        value = draftText,
        onValueChange = { input ->
            draftText = input          // border turns red immediately
        },
        label = { Text("Vitesse (m/s)", color = Color.White, fontSize = 10.sp) },
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction    = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { applyValue() }  // border turns green on confirm
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = borderColor,
            unfocusedBorderColor = borderColor,
            cursorColor          = Color.White,
        ),
        modifier = Modifier.size(width = 110.dp, height = 64.dp)
    )
}
