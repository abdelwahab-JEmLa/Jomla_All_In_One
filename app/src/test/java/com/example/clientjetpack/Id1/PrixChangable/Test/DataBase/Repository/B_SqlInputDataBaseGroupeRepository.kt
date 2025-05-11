package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository

import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models.AB_ReferentialSepareDataBases

interface B_SqlInputDataBaseGroupeRepository {
    interface ProduitDataBase_Repository {
        val modelList: List<AB_ReferentialSepareDataBases.ProduitDataBase>
    }
    
    interface ClientDataBase_Repository {
        val modelList: List<AB_ReferentialSepareDataBases.ClientDataBase>
        
        fun add(client: AB_ReferentialSepareDataBases.ClientDataBase)
        
        fun update(
            client: AB_ReferentialSepareDataBases.ClientDataBase,
            onSuccess: (AB_ReferentialSepareDataBases.ClientDataBase) -> Unit = {}
        )
    }

    interface TypeTarificationDataBase_Repository {
        val modelList: List<AB_ReferentialSepareDataBases.TypeTarificationDataBase>
    }

    interface A_TarificationDataBaseFacileEntreRepository {
        val modelList: List<AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre>

        fun add(
            data: AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre,
            onSuccess: (AB_ReferentialSepareDataBases.A_TarificationDataBaseFacileEntre) -> Unit = {}
        )
    }
}
