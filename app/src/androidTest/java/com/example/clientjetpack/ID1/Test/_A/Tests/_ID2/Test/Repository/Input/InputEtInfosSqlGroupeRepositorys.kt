package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test.Repository.Input

import com.example.clientjetpack.ID1.Test.Packages.Models.InputEtInfosSqlModels

interface InputEtInfosSqlGroupeRepositorys {
    fun ProduitInfosRepository(): ProduitDataBase_Repository
    fun ClientDataBase_Repository(): ClientDataBase_Repository
    fun TypeTarificationInfosRepository(): TypeTarificationDataBase_Repository
    fun TarificationRepository(): TarificationRepository

    interface ProduitDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.ProduitInfos>

        fun add(
            produitInfos: InputEtInfosSqlModels.ProduitInfos,
            onSuccess: (InputEtInfosSqlModels.ProduitInfos) -> Unit = {}
        )
    }

    interface ClientDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.ClientDataBase>

        fun add(client: InputEtInfosSqlModels.ClientDataBase)

        fun update(
            client: InputEtInfosSqlModels.ClientDataBase,
            onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit,
        )
    }

    interface TypeTarificationDataBase_Repository {
        var modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase>
    }

    interface TarificationRepository {
        var modelList: List<InputEtInfosSqlModels.Tarification>

        suspend fun loadDataFromFirebase()

        fun add(
            tarification: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit = {}
        )
    }
}
