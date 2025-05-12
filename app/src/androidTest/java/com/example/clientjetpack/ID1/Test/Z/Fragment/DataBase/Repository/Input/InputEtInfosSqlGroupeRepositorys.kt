package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.InputEtInfosSqlModels

interface InputEtInfosSqlGroupeRepositorys {
    fun ProduitInfosRepository(): ProduitDataBase_Repository
    fun ClientDataBase_Repository(): ClientDataBase_Repository
    fun TypeTarificationInfosRepository(): TypeTarificationDataBase_Repository
    fun TarificationRepository(): TarificationRepository

    interface ProduitDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.ProduitInfos>
    }

    interface ClientDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.ClientDataBase>
        fun add(client: InputEtInfosSqlModels.ClientDataBase)
        fun update(client: InputEtInfosSqlModels.ClientDataBase, onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit = {})
    }

    interface TypeTarificationDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase>
    }

    interface TarificationRepository {
        var modelList: List<InputEtInfosSqlModels.Tarification>
        fun add(tarification: InputEtInfosSqlModels.Tarification, onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit = {})

        // Add the method to load data on demand
        suspend fun loadDataFromFirebase()
    }
}
