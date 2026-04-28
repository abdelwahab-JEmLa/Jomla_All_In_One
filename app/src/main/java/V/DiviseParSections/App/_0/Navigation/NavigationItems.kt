package V.DiviseParSections.App._0.Navigation

// Navigation Items
object NavigationItems {

    /**
     * Gets the list of navigation items based on user permissions
     * @param isAdmin Whether the current user has admin privileges
     * @return List of screens to display in navigation
     */
    fun getItems(isAdmin: Boolean = true): List<Screen> {
        val baseItems = listOf(
            Screen.A_Clients_LocationGps,
            Screen.Fragment_Compact_Presentoir_Echantilliants,
            Screen.Compact_Presentoire_App_Produits_FragID5,
            Screen.TravailleTempRecorder,
            Screen.Achats_Produits_Chez_Grossists,
            Screen.ToggleFab,
        )

        // Admin-only items
        val adminItems = listOf(
            Screen.EditDatabaseWithCreateNewArticles,
            Screen.FragmentProduitFastSearchDialog,
        )

        return if (isAdmin) {
            baseItems + adminItems
        } else {
            baseItems
        }
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
