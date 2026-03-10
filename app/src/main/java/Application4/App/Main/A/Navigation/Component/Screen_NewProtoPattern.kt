package Application4.App.Main.A.Navigation.Component

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clientjetpack.R

sealed class Screen_NewProtoPattern(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color,
    @param:DrawableRes val customIconRes: Int? = null // Support for custom PNG/WebP from drawable
) {
    data object A_Clients_LocationGps : Screen_NewProtoPattern(
        route = "A_Clients_LocationGps",
        icon = Icons.Default.MapsHomeWork,
        title = "A_Clients_LocationGps",
        color = Color(0xFFFF5722)
    )

    data object Compact_Presentoire_App_Produits_FragID4: Screen_NewProtoPattern(
        route = "Compact_Presentoire_App_Produits_FragID4",
        icon = Icons.Default.PauseCircleOutline,
        title = "Compact_Presentoire_App_Produits_FragID4",
        color = Color(0xFFF44336),
        customIconRes = R.drawable.logo
    )
    data object Ancien_PresenterApp_FragID5: Screen_NewProtoPattern(
        route = "Ancien_PresenterApp_FragID5",
        icon = Icons.Default.ShoppingBasket,
        title = "Ancien_PresenterApp_FragID5",
        color = Color(0xFF9C27B0),
        customIconRes = R.drawable.logo
    )
}
