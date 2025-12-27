package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun FragAchats_FloatingOutlinedSearcher_4(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    fun update_active(search: String): Unit {
        focusedValuesGetter.update_activeCentralValues(
            active_Central_Values.copy(
                outlined_filter_searcher_achat = search
            )
        )
    }

    val currentValues = focusedValuesGetter.active_Central_Values

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 350f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 350f) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Column {
                // Zone de drag - SEULEMENT cette partie est draggable
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                // Pas de contraintes - drag libre partout sur l'écran

                                keyboardController?.hide()
                                focusRequester.freeFocus()
                            }
                        }
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "Drag to move",
                        tint = Color.Gray,
                        modifier = Modifier.height(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // TextField - PAS de pointerInput ici, il reste interactif
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = currentValues.outlined_filter_searcher_achat,
                        onValueChange = { newValue -> update_active(newValue) },
                        label = { Text("Search Achats") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (currentValues.outlined_filter_searcher_achat.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        update_active("")
                                        focusRequester.freeFocus()
                                        keyboardController?.hide()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .focusRequester(focusRequester),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }
    }
}
