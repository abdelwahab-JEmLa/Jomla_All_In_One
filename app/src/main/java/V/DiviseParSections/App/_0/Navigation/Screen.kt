package V.DiviseParSections.App._0.Navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.MapsHomeWork
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.ProductionQuantityLimits
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clientjetpack.R

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val color: Color,
    @DrawableRes val customIconRes: Int? = null // Support for custom PNG/WebP from drawable
) {
    data object A_Clients_LocationGps : Screen(
        route = "A_Clients_LocationGps",
        icon = Icons.Default.MapsHomeWork,
        title = "A_Clients_LocationGps",
        color = Color(0xFFFF5722)
    )

    data object Fragment_Compact_Presentoir_Echantilliants: Screen(
        route = "Fragment_Compact_Presentoir_Echantilliants",
        icon = Icons.Default.ProductionQuantityLimits,
        title = "Fragment_Compact_Presentoir_Echantilliants",
        color = Color(0xFF9C27B0),
         customIconRes = R.drawable.logo
    )

    data object Compact_Presentoire_App_Produits_FragID4: Screen(
        route = "Compact_Presentoire_App_Produits_FragID4",
        icon = Icons.Default.PauseCircleOutline,
        title = "Compact_Presentoire_App_Produits_FragID4",
        color = Color(0xFFF44336)
    )

    data object EditDatabaseWithCreateNewArticles : Screen(
        route = "EditDatabaseWithCreateNewArticles",
        icon = Icons.Default.Dataset,
        title = "EditDatabaseWithCreateNewArticles",
        color = Color(0xFF7B351D)
    )

    data object Screen1PanieVentsFinale : Screen(
        route = "Screen1PanieVentsFinale",
        icon = Icons.Default.ShoppingCart,
        title = "Screen1PanieVentsFinale",
        color = Color(0xFFF44336)
    )

    data object Achats_Produits_Chez_Grossists : Screen(
        route = "Achats_Produits_Chez_Grossists",
        icon = Icons.Default.Receipt,
        title = "Achats_Produits_Chez_Grossists",
        color = Color(0xFF009688)
    )

    data object EducationFragment : Screen(
        route = "EducationFragment",
        icon = Icons.Default.Class,
        title = "EducationFragment",
        color = Color(0xFFFFEB3B)
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

    data object FragmentProduitFastSearchDialog : Screen(
        route = "FragmentProduitFastSearchDialog",
        icon = Icons.Default.Search,
        title = "FragmentProduitFastSearchDialog",
        color = Color(0xFF009688),
         customIconRes = R.drawable.store // Uncomment and set your store.png resource
    )

    data object Main_DataBaseInitFactory_1Produit : Screen(
        route = "Main_DataBaseInitFactory_1Produit",
        icon = Icons.Default.Storage,
        title = "Database Init Factory",
        color = Color(0xFF795548)
    )
}
