package com.example.clientjetpack.Models

data class ProductDisplayController(
    val windowsProductIdWhoInfoDisplayed: Long? = null,
    val windowsPickerDisplayedQuantity: Int = 0,
    val windowsBottomRowScrollPosition: Int = 0,

    val selectedColorId: Int = 0,
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val clientDisplayerScrollPosition: Int = 0,
    val error: String? = null,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val wifiTestDisplayer: Boolean = false,
    val isHostPhone: Boolean = true,
    val messageByWifi: String = "",
)
