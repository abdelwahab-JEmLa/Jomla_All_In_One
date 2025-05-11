package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels

interface InputSqlGroupeRepositorys {
    // Factory methods to return repository instances
    fun ProduitDataBase_Repository(): ProduitDataBase_Repository

    fun ClientDataBase_Repository(): ClientDataBase_Repository

    fun TypeTarificationDataBase_Repository(): TypeTarificationDataBase_Repository

    fun TarificationRepository(): TarificationRepository

    interface ProduitDataBase_Repository {
        val modelList: List<InputSqlModels.ProduitDataBase>
    }

    interface ClientDataBase_Repository {
        val modelList: List<InputSqlModels.ClientDataBase>

        fun add(client: InputSqlModels.ClientDataBase)

        fun update(
            client: InputSqlModels.ClientDataBase,
            onSuccess: (InputSqlModels.ClientDataBase) -> Unit = {}
        )
    }

    interface TypeTarificationDataBase_Repository {
        val modelList: List<InputSqlModels.TypeTarificationDataBase>
    }

    interface TarificationRepository {
        val modelList: List<InputSqlModels.Tarification>

        fun add(
            data: InputSqlModels.Tarification,
            onSuccess: (InputSqlModels.Tarification) -> Unit = {}
        )
    }
}
