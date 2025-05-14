package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Modules.Log

import com.example.clientjetpack.ID1.Test.Z.Fragment.Log.TreePrefix
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Z.Function.strDateEtTempFromVidTimestamp

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

        val clientAchteurInfos = StringBuilder().apply {
            append(clientPrefix)
            append(" ClientAchteur ID: ")
            append(client.id)
            append(", Date: ")
            append(clientDate)
            append(" Time: ")
            append(clientTime)
            append(" (${client.typeTarification.size} tarification types)")
        }.toString()

        println(clientAchteurInfos)

        logTarificationTypes(client.typeTarification, isLastProduit, isLastClient)
    }
}
