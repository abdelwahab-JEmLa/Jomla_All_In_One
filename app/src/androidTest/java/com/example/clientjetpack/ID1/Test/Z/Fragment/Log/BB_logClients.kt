package com.example.clientjetpack.ID1.Test.Z.Fragment.Log

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel

fun logClients(
    viewModel: TarificationViewModel,
    clients: List<OutputNoSqlModel.Produit.Client>,
    isLastProduit: Boolean,
) {
    clients.forEachIndexed { clientIndex, client ->
        val isLastClient = clientIndex == clients.size - 1
        val clientPrefix =
            if (isLastProduit) TreePrefix.Type3.get(isLastClient) else TreePrefix.Type2.get(
                isLastClient
            )

        val (clientDate, clientTime) = strDateEtTempFromVidTimestamp(
            client.vidTimestamp
        )
        val clientInfo = viewModel.getSqlClient(client.id)

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

        logTarificationTypes(viewModel,client.typeTarification, isLastProduit, isLastClient)
    }
}
