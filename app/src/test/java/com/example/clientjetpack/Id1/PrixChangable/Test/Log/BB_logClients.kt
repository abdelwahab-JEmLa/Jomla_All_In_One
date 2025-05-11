package com.example.clientjetpack.Id1.PrixChangable.Test.Log

import com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel.OutputViewModelNoSqlDB
import com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Repository.InputSqlDBGroupeRepositoryImp
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test._TestsDisplayerLogDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun _TestsDisplayerLogDataBase.logClients(
    clients: List<OutputViewModelNoSqlDB.Produit.Client>,
    isLastProduit: Boolean,
    ) {
        val clientRepository = InputSqlDBGroupeRepositoryImp.clientRepository

        clients.forEachIndexed { clientIndex, client ->
            val isLastClient = clientIndex == clients.size - 1
            val clientPrefix =
                if (isLastProduit) TreePrefix.Type3.get(isLastClient) else TreePrefix.Type2.get(
                    isLastClient
                )

            val (clientDate, clientTime) = strDateEtTempFromVidTimestamp(client.vidTimestamp)
            val clientInfo = clientRepository.modelList.find { it.id == client.id }

            val clientInfos = StringBuilder().apply {
                append(clientPrefix)
                append(" Client ID: ")
                append(client.id)
                append("=(${clientInfo?.nom ?: "Unknown"})")
                append(", Date: ")
                append(clientDate)
                append(" Time: ")
                append(clientTime)
                append(" (${client.typeTarification.size} tarification types)")
            }.toString()

            println(clientInfos)

            logTarificationTypes(client.typeTarification, isLastProduit, isLastClient)
        }
    }
