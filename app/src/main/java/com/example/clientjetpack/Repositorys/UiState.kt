package com.example.clientjetpack.Repositorys

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.CategoriesTabelle
import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_CodePartageEntreApps.Model.Z.Archive.ProductDisplayController
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SuppliersTabelle

data class UiState(
    val articlesBasesStatTables: List<ArticlesBasesStatsTable> = emptyList(),
    val categories: List<CategoriesTabelle> = emptyList(),

    val appSettingsSaverModel: List<AppSettingsSaverModel> = emptyList(),
    val devicesTypeManager: List<DevicesTypeManager> = emptyList(),
    val newProduitsList: List<A_ProduitModel> = emptyList(),
    val colorsArticlesTabelleModel: List<ColorsArticlesTabelle> = emptyList(),
    val soldArticlesModel: List<SoldArticlesTabelle?> = emptyList(),
    val suppliers: List<SuppliersTabelle> = emptyList(),

    val diviseurDeDisplayProductForEachClient: List<DiviseurDeDisplayProductForEachClient> = emptyList(),

    val productDisplayController: ProductDisplayController,
    val maxPriceMap: Map<Pair<Long, Long>, List<PriceRecord>> = emptyMap(),
    val isLoading: Boolean = false,
    val loadingProgress: Float = 0f,
    val error: String? = null,

    )
// Update the price mapping to include client ID
data class PriceRecord(
    val price: Double,
    val clientId: Long,
    val date: Long
)
