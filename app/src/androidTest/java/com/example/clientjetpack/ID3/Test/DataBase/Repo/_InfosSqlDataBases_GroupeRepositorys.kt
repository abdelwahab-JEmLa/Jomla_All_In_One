package com.example.clientjetpack.ID3.Test.DataBase.Repo

import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.D_TarificationInfos
import kotlinx.coroutines.flow.StateFlow

interface _InfosSqlDataBases_GroupeRepositorys {
    interface A_ProduitInfos_Repository {
        var modelList: List<A_ProduitInfos>
        val modelListFlow: StateFlow<List<A_ProduitInfos>>

        fun add(
            produitInfos: A_ProduitInfos,
            onSuccess: (A_ProduitInfos) -> Unit = {}
        )

        fun deleteAll(onSuccess: () -> Unit = {})
    }

    interface B_ClientInfos_Repository {
        var modelList: List<B_ClientInfos>
        val modelListFlow: StateFlow<List<B_ClientInfos>>

        fun add(client: B_ClientInfos)

        fun update(
            client: B_ClientInfos,
            onSuccess: (B_ClientInfos) -> Unit,
        )

        fun deleteAll(onSuccess: () -> Unit = {})
    }

    interface C_TypeTarificationInfos_Repository {
        var modelList: List<C_TypeTarificationInfos>
        val modelListFlow: StateFlow<List<C_TypeTarificationInfos>>

        fun add(typeTarification: C_TypeTarificationInfos)

        fun deleteAll(onSuccess: () -> Unit = {})
    }

    interface D_TarificationInfos_Repository {
        var modelList: List<D_TarificationInfos>
        val modelListFlow: StateFlow<List<D_TarificationInfos>>

        suspend fun loadDataFromFirebase()

        fun add(
            tarification: D_TarificationInfos,
            onSuccess: (D_TarificationInfos) -> Unit = {}
        )

        fun deleteAll(onSuccess: () -> Unit = {})
    }
}
