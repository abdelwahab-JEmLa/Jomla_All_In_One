package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default(): Button_State {
            return Button_State()
        }
    }
}
