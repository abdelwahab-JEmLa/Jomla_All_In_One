package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import org.osmdroid.views.MapView
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_FragMap_Button_2(
    mapView: MapView,
    aCentralFacade: ACentralFacade = koinInject(),
    viewModel: MapClientsViewModel= koinInject(),

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

                //<-- Reload: show only markers within 1 km of the current map centre
                FloatingActionButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        val center = mapView.mapCenter
                        viewModel.relod_map_marques_du_1km_du_centre_map(
                            centerLat = center.latitude,
                            centerLng = center.longitude,
                        )
                    },
                    containerColor = Color(0xFF1565C0),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload markers within 1 km",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
