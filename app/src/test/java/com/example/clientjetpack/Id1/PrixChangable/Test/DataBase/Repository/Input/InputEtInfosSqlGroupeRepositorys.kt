package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputEtInfosSqlModels

interface InputEtInfosSqlGroupeRepositorys {
    // Factory methods to return repository instances
    fun ProduitInfosRepository(): ProduitDataBase_Repository

    fun ClientDataBase_Repository(): ClientDataBase_Repository

    fun TypeTarificationInfosRepository(): TypeTarificationDataBase_Repository

    fun TarificationRepository(): TarificationRepository

    interface ProduitDataBase_Repository {
        val modelList: List<InputEtInfosSqlModels.ProduitInfos>
    }

    interface ClientDataBase_Repository {
        val modelList: List<InputEtInfosSqlModels.ClientDataBase>

        fun add(client: InputEtInfosSqlModels.ClientDataBase)

        fun update(
            client: InputEtInfosSqlModels.ClientDataBase,
            onSuccess: (InputEtInfosSqlModels.ClientDataBase) -> Unit = {}
        )
    }

    interface TypeTarificationDataBase_Repository {
        val modelList: List<InputEtInfosSqlModels.TypeTarificationDataBase>
    }

    interface TarificationRepository {
        val modelList: List<InputEtInfosSqlModels.Tarification>

        fun add(
            data: InputEtInfosSqlModels.Tarification,
            onSuccess: (InputEtInfosSqlModels.Tarification) -> Unit = {}
        )
    }
}
