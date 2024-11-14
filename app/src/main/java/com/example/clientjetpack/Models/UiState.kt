package com.example.clientjetpack.Models

import a_RoomDB.AppSettingsSaverModel
import a_RoomDB.ArticlesBasesStatsTable
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ClientsModel
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import a_RoomDB.SuppliersTabelle

data class UiState(
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
    val clientsModel: List<ClientsModel> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),
    val productDisplayController: List<ProductDisplayController> = emptyList(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val scrollPosition: Int = 0,
    val error: String? = null,
    val isConnected: Boolean = false,
    val connectionStatus: String = "Déconnecté",
    val wifiTestDisplayer: Boolean = false,
    val isHostPhone: Boolean = true,
    val messageByWifi: String = "",
    )
