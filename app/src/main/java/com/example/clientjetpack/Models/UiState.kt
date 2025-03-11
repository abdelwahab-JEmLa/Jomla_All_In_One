package com.example.clientjetpack.Models

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.CategoriesTabelle
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.SuppliersTabelle
import com.example.Models.DiviseurDeDisplayProductForEachClient

data class UiState(
    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val devicesTypeManager: List<DevicesTypeManager> = emptyList(),
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val newProduitsList: List<A_ProduitModel> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
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
