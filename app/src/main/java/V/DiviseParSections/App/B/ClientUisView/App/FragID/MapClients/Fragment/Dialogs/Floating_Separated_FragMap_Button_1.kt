package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Dialogs

import kotlin.math.roundToInt
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import org.koin.compose.koinInject
  //<--
  //TODO(1): fix le code 
data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color,Color> =,
    val colors: Pair<Icons.Default,Icons.Default> =,
    val description_Functionement: String = "Toggle product details expansion",
) {
    companion object {
        fun get_Default(): Button_State {
            return Button_State()
        }
    }
}

@Composable
fun Floating_Separated_FragMap_Button_1(
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    buttonState: Button_State = Button_State.get_Default()
) {
    var showLabels by remember { mutableStateOf(buttonState.showLabels) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 200f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 200f)) } // Fixed: Better initial position

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y

                        // Optional: Add boundary constraints
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
                if (buttonState.showLabels) {
                    Text(
                        text = "its_Active text_Label",
                        color = Color.White, // Better contrast
                        modifier = Modifier
                            .background(
                                color = if ()
                                    Color.Red.copy(alpha = 0.8f)
                                else
                                    Color.Green.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                FloatingActionButton(
                    modifier = Modifier

                        .getSemanticsTag(buttonState, "buttonState")
                        .size(48.dp),
                    onClick = {
                        val currentValues = focusedValuesGetter.active_Central_Values
                        val newValues = currentValues.copy(
                            click_On_Marque = toggle_retrn()
                        )
                        focusedValuesGetter.update_activeCentralValues(newValues)
                    },
                    containerColor = if ()
                        Color.Red
                    else
                        Color.Green
                ) {
                    Icon(
                        imageVector = if ()
                          ,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
enum class Click_On_Marque {
      Standart,
      ADD_Au_Ciblage_Clients;

    fun toggle_retrn(): Click_On_Marque {
          //<--
          //TODO(1): regle
    }
}
