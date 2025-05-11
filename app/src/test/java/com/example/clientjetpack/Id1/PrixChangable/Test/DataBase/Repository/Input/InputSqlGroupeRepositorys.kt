package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.Input

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.InputSqlModels

interface InputSqlGroupeRepositorys {
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

    interface A_TarificationDataBaseFacileEntreRepository {
        val modelList: List<InputSqlModels.A_TarificationDataBaseFacileEntre>

        fun add(
            data: InputSqlModels.A_TarificationDataBaseFacileEntre,
            onSuccess: (InputSqlModels.A_TarificationDataBaseFacileEntre) -> Unit = {}
        )
    }
}
