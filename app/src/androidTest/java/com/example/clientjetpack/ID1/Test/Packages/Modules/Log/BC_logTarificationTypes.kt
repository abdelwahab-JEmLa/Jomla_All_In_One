package com.example.clientjetpack.ID1.Test.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

fun logTarificationTypes(
    types: List<OutputNoSqlModel.Produit.ClientAchteur.TypeTarification>,
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
            append(" D_Tarification Type : ")
            append(type.infosId)
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
