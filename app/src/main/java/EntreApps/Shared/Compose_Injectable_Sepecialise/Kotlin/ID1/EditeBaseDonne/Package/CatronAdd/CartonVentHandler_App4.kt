package EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.CatronAdd

import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.ActivationTigger
import EntreApps.Shared.Modules.Utils.M1.Module.Views.FastInit_Outlined_Int_Edite_Modulable_Proto4
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.FunctionsBase.ifTrue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CartonVentHandler_App4(
    activation: Boolean = ActivationTigger.CatronAdd.activation,
    currentCartons: Int,
    depotEnCartons: Int,
    isAvailable: Boolean,
    isAdmin: Boolean,
    compactMode: Boolean,
    containerColor: Color,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
    onDepotUpdate: (newDepotCartons: Int) -> Unit,
    onVentUpdate: (newCartons: Int) -> Unit,
) {
    val cartonShape = RoundedCornerShape(
        topStart = 12.dp, topEnd = 12.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
    activation.ifTrue {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(cartonShape)
                .background(containerColor.copy(alpha = 0.10f))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding / 2),
            contentAlignment = Alignment.CenterEnd
        ) {
            FastInit_Outlined_Int_Edite_Modulable_Proto4(
                start_count = currentCartons,
                au_depot = depotEnCartons,
                standard_count = 1,                     // 1 carton par premier clic
                startCouleur = Color(0xFFF44336),
                icon = Icons.Default.Inventory2,
                isAvailable = isAvailable,
                compact_taille = compactMode,
                show_depot_card_on_top_in_flow_row = true,
                is_admin = isAdmin,
                add_spacing_between_depot_and_sale = isAdmin,
                on_admin_depot_update = onDepotUpdate,
                on_Data_Update = onVentUpdate,
            )
        }
    }
}
