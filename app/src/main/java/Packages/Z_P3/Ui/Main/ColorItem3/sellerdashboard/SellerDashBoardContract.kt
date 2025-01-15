package Packages.Z_P3.Ui.Main.ColorItem3.sellerdashboard

import kotlinx.serialization.Serializable


/**
 * Object used for a type safe destination to a SellerDashBoard route
 */
@Serializable
object SellerDashBoardDestination

/**
 * UI State that represents SellerDashBoardScreen
 **/
class SellerDashBoardState

/**
 * SellerDashBoard Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
data class SellerDashBoardActions(
    val onClick: () -> Unit = {}
)


