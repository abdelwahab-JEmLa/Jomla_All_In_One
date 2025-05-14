package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Modules.Log

import com.example.clientjetpack.ID1.Test.Z.Fragment.Log.TreePrefix
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Z.Function.strDateEtTempFromVidTimestamp

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
            append(" D_TarificationInfos Type : ")
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
