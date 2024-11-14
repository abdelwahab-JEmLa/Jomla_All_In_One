package com.example.clientjetpack.Models

data class ProductDisplayController(
    val windowsProductIdWhoInfoDisplayed: Long? = null,
    val windowsPickerDisplayedQuantity: Int = 0,
    val windowsBottomRowScrollPosition: Int = 0,
    val windowsSelectedColorId: Int = 0,

    val clientDisplayerScrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHostPhone: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null,
    )
