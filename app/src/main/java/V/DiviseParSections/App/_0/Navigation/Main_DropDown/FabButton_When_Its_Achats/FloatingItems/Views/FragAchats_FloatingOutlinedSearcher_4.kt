package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@OptIn(FlowPreview::class)
@Composable
fun FragAchats_FloatingOutlinedSearcher_4(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    // Use local state for immediate UI updates
    var localSearchText by remember { mutableStateOf("") }

    // Initialize local state from central state only once
    LaunchedEffect(Unit) {
        localSearchText = focusedValuesGetter.active_Central_Values.outlined_filter_searcher_achat
    }

    fun update_active(search: String): Unit {
        focusedValuesGetter.update_activeCentralValues(
            focusedValuesGetter.active_Central_Values.copy(
                outlined_filter_searcher_achat = search
            )
        )
    }

    // Debounce updates to central state to avoid crashes during rapid typing
    LaunchedEffect(localSearchText) {
        snapshotFlow { localSearchText }
            .debounce(300) // Wait 300ms after user stops typing
            .collect { searchText ->
                update_active(searchText)
            }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 350f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 350f) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        offsetX = offsetX.coerceIn(0f, screenWidth.value - 250f)
                        offsetY = offsetY.coerceIn(0f, screenHeightDp.value - 150f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    // Use local state for immediate UI responsiveness
                    value = localSearchText,
                    onValueChange = { newValue ->
                        localSearchText = newValue // Update local state immediately
                        // Central state is updated via LaunchedEffect with debounce
                    },
                    label = { Text("Search Achats") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
