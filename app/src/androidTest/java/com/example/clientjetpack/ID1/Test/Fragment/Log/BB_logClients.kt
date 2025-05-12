package com.example.clientjetpack.ID1.Test.Fragment.Log

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Fragment.ViewModel.TarificationViewModel

fun logClients(
    viewModel: TarificationViewModel,
    clients: List<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client>,
    isLastProduit: Boolean,
) {
    clients.forEachIndexed { clientIndex, client ->
        val isLastClient = clientIndex == clients.size - 1
        val clientPrefix =
            if (isLastProduit) V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type3.get(isLastClient) else V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type2.get(
                isLastClient
            )

        val (clientDate, clientTime) = com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp(
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
