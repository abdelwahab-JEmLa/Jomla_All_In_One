package com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.InputEtInfosSqlModels

interface InputEtInfosSqlGroupeRepositorys {
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
    }
}
