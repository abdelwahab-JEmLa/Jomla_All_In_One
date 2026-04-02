package Application4.App.Modules.Wi.Module

enum class Wifi_Messages_Types_NewProto(val prefix: String) {
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

    Collapse_Client_Expanded_Produit("Collapse_Client_Expanded_Produit");

    companion object {
        fun fromPayload(payload: String): Pair<Wifi_Messages_Types_NewProto, String>? =
            entries.firstOrNull { payload.startsWith(it.prefix) }
                ?.let { it to payload.removePrefix(it.prefix) }
    }
}
