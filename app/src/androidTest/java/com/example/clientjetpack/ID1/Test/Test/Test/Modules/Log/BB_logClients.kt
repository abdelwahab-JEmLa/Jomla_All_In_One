package com.example.clientjetpack.ID1.Test.Test.Test.Modules.Log

import com.example.clientjetpack.ID1.Test.Test.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Test.Test.Z.Function.strDateEtTempFromVidTimestamp

fun logClients(
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

        val clientInfos = StringBuilder().apply {
            append(clientPrefix)
            append(" Client ID: ")
            append(client.id)
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
