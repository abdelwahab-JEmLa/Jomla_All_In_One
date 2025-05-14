package com.example.clientjetpack.ID1.Test._A.Tests.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

fun logClients(
    clientAchteurs: List<OutputNoSqlModel.Produit.ClientAchteur>,
    isLastProduit: Boolean,
) {
    clientAchteurs.forEachIndexed { clientIndex, client ->
        val isLastClient = clientIndex == clientAchteurs.size - 1
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
            append(client.infosId)
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
