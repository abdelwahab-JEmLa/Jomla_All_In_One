package Application4.App.Main.A.Navigation.Component

// Navigation Items
object NavigationItems {

    fun getItems(isAdmin: Boolean = true): List<Screen_NewProtoPattern> {
        return listOf(
            Screen_NewProtoPattern.A_Clients_LocationGps,
            Screen_NewProtoPattern.Compact_Presentoire_App_Produits_FragID4,
        )
    }

    /**
     * Legacy method for backward compatibility
     * Returns all items (assumes admin access)
     */
    @Deprecated(
        "Use getItems(isAdmin: Boolean) instead to properly filter by permissions",
        ReplaceWith("getItems(isAdmin = true)")
    )
    fun getItems() = getItems(isAdmin = true)
}
