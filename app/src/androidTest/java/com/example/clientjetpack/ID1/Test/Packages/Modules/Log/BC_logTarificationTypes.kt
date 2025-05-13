package com.example.clientjetpack.ID1.Test.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

fun logTarificationTypes(
    types: List<OutputNoSqlModel.Produit.Client.TypeTarification>,
    isLastProduit: Boolean,
    isLastClient: Boolean,
) {
    types.forEachIndexed { typeIndex, type ->
        val isLastType = typeIndex == types.size - 1
        val typePrefix = when {
            isLastProduit && isLastClient -> TreePrefix.Type4.get(isLastType)
            else -> TreePrefix.Type5.get(isLastType)
        }

        val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(
            type.vidTimestamp
        )


        val typeInfos = StringBuilder().apply {
            append(typePrefix)
            append(" Tarification Type : ")
            append(type.id)
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
