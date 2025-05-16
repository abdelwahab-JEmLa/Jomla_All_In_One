package com.example.clientjetpack.ID3.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.InfosSqlDataBasesRepository
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.ProduitNoSqlDataBase
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
//    fun getOutputModel(): ProduitNoSqlDataBase {
//    }

}

class UiState(
    val outputModel: ProduitNoSqlDataBase =
        ProduitNoSqlDataBase(emptyList()),

    val isLoading: Boolean = false,
    val error: String? = null
)
