package V.DiviseParSections.App._0.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color
) {
    data object A_ClientsLocationGps : Screen(
        route = "A_ClientsLocationGps",
        icon = Icons.Default.MapsHomeWork,
        title = "A_ClientsLocationGps",
        color = Color(0xFFFF5722)
    )

    data object FacadePresentoireProduits : Screen(
        route = "FacadePresentoireProduits",
        icon = Icons.Default.EditRoad,
        title = "FacadePresentoireProduits",
        color = Color(0xFF151414)
    )

    data object EditDatabaseWithCreateNewArticles : Screen(
        route = "EditDatabaseWithCreateNewArticles",
        icon = Icons.Default.Dataset,
        title = "EditDatabaseWithCreateNewArticles",
        color = Color(0xFF7B351D)
    )


    data object SoldCart : Screen(
        route = "sold_cart",
        icon = Icons.Default.ShoppingCart,
        title = "Panier Sold",
        color = Color(0xFFF44336)
    )

    data object CommandeProduits : Screen(
        route = "CommandeProduits",
        icon = Icons.Default.Receipt,
        title = "CommandeProduits",
        color = Color(0xFF009688)
    )

    data object TravailleTempRecorder : Screen(
        route = "TravailleTempRecorder",
        icon = Icons.Default.Work,
        title = "TravailleTempRecorder",
        color = Color(0xFF9C27B0)
    )

    data object NewFragTest : Screen(
        route = "NewFragTest",
        icon = Icons.Default.TravelExplore,
        title = "NewFragTest",
        color = Color(0xFF4CAF50)
    )

    data object DialogTests : Screen(
        route = "DialogTests",
        icon = Icons.Default.DeveloperMode,
        title = "DialogTests",
        color = Color(0xFF009688)
    )

    data object ToggleFab : Screen(
        route = "toggle_fab",
        icon = Icons.Default.Visibility,
        title = "Toggle FAB",
        color = Color(0xFF2196F3)
    )

    data object TestProduitFastSearchDialog : Screen(
        route = "TestProduitFastSearchDialog",
        icon = Icons.Default.Search,
        title = "TestProduitFastSearchDialog",
        color = Color(0xFF009688)
    )
}
