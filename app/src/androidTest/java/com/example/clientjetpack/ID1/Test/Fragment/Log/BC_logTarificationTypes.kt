package com.example.clientjetpack.ID1.Test.Fragment.Log

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Fragment.ViewModel.TarificationViewModel

fun logTarificationTypes(
    viewModel: TarificationViewModel,
    types: List<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client.TypeTarification>,
    isLastProduit: Boolean,
    isLastClient: Boolean,
) {
    val currentClient = viewModel.outputNoSqlFlow.value.produits
        .flatMap { it.clients }
        .find { client -> client.typeTarification.any { types.contains(it) } }

    if (currentClient != null) {
        val clientInfo = viewModel.getSqlClient(currentClient.id)

        types.forEachIndexed { typeIndex, type ->
            val isLastType = typeIndex == types.size - 1
            val typePrefix = when {
                isLastProduit && isLastClient -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type4.get(isLastType)
                else -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type5.get(isLastType)
            }

            val (typeDate, typeTime) = com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp(
                type.vidTimestamp
            )
            val typeInfo = viewModel.getSqlTypeTarification(type.id)

            val isActive = clientInfo?.idActiveTypeTarificationDataBase == type.id
            val activeStatus = if (isActive) " [ACTIVE]" else ""

            val typeInfos = StringBuilder().apply {
                append(typePrefix)
                append(" Tarification Type : ")
                append(type.id)
                append("=(${typeInfo?.typeTarificationEnum ?: "Unknown"})")
                append(activeStatus)
                append(" , Date: ")
                append(typeDate)
                append(" Time: ")
                append(typeTime)
                append(" (${type.PrixsCurrency.size} currencies)")
            }.toString()

            println(typeInfos)

            logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
        }
    }
}
