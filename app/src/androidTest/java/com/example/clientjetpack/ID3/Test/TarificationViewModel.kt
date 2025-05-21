package com.example.clientjetpack.ID3.Test

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class TarificationViewModel(
    infosSqlDataBasesRepository: InfosSqlDataBasesRepository
) {
    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState


    init {
        //<--
        //TODO(1): ajout un collect du
    }

//
//    fun getSqlClient(id: Long): B_ClientInfos? {
//    }
//
//    fun getSqlProduit(id: Long): A_ProduitInfos? {
//        infosSqlDataBasesRepository.get
//    }
//
//    fun getSqlTypeTarification(id: Long): C_TypeTarificationInfos? {
//    }
//
//    /**
//     * Adds a random tarification entry to the database
//     */
//    fun addRandomTarification() {
//    }
//
//    fun getOutputModel(): ProduitsNoSqlDataBase {
//    }

}

class UiState(
    val outputModel: ProduitsNoSqlDataBase =
        ProduitsNoSqlDataBase(emptyList()),

    val isLoading: Boolean = false,
    val error: String? = null
)
