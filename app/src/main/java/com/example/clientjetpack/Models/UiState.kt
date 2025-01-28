package com.example.clientjetpack.Models

import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Packages._3.C_Serveur.App.Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.CategoriesTabelle
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.SuppliersTabelle
import com.example.Models.DiviseurDeDisplayProductForEachClient

// 1. First, update the UiState class to properly use devicesTypeManager
data class UiState(
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val devicesTypeManager: List<DevicesTypeManager> = emptyList(),
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
    val clientsModel: List<ClientsModel> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),

    val diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient> = emptyList(),

    val productDisplayController: ProductDisplayController,
    val maxPriceMap: Map<Pair<Long, Long>, List<PriceRecord>> = emptyMap(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null
)
// Update the price mapping to include client ID
data class PriceRecord(
    val price: Double,
    val clientId: Long,
    val date: Long
)
