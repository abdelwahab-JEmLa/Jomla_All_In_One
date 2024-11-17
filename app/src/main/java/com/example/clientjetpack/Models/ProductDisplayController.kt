package com.example.clientjetpack.Models

data class ProductDisplayController(
    val clientWindowsDisplayedProductId: Long? = null,
    val clientWindowsPickerDisplayedQuantity: Int = 0,
    val clientWindowsSelectedColorId: Long = 0,
    val clientWindowsLazyRowSupColorsScroll: Int = 0,
    val mainGridScrollPosition: Int = 0,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val isHostPhone: Boolean = true,
    val switchRoles: Boolean = true,
    val testMessageByWifi: String = "",
    val error: String? = null,
    )
