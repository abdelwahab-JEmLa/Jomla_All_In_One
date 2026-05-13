package Application4.App.Modules.Wi.Module

enum class Wifi_Messages_Types_NewProto(private val _prefix: String = "") {
    Update_Depot_Count_Par_Chain_Key_to_NewCount("Update_Depot_Count_Par_Chain_Key_to_NewCount"),
    ClientMainGridScrollPosition("ClientMainGridScrollPosition"),
    ClientWindowsLazyRowSupColorsScrolle("ClientWindowsLazyRowSupColorsScrolle"),
    ClientWindowsDisplayedProductId("ClientWindowsDisplayedProductId"),
    ClientWindowsSelectedColorId("clientWindowsSelectedColorId"),
    DISMISS_PRODUCT_INFO("DismissWindowsInfosProduct"),
    WindowsPickerDisplayedQuantity("WindowsPickerDisplayedQuantity"),
    SearchWindowsDisplaye("SearchWindowsDisplaye"),
    NewArregmentColorsJsonStruct("NewArregmentColorsJsonStruct"),
    FilterProduitsParCatalogueBsonID_ET_Autres_Types("FilterProduitsParCatalogueBsonID_ET_Autres_Types"),
    Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran("Update_ActiveCompt_active_ProduitKeyID_Au_DroopDown_PresenterEcran"),
    Collapse_Client_Expanded_Produit("Collapse_Client_Expanded_Produit"),
    Change_Filtered_Produits_Du_TabletteDisplayer,  // no explicit string → falls back to name
    ;

    /**
     * The wire-protocol prefix for this message type.
     * Entries that pass an explicit string use it; entries that omit it fall back
     * to their own [name], which is how [Change_Filtered_Produits_Du_TabletteDisplayer]
     * (and any future entries) get a sensible default automatically.
     */
    val prefix: String get() = _prefix.ifEmpty { name }

    companion object {
        fun fromPayload(payload: String): Pair<Wifi_Messages_Types_NewProto, String>? =
            entries.firstOrNull { payload.startsWith(it.prefix) }
                ?.let { it to payload.removePrefix(it.prefix) }
    }
}
