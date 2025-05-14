package com.example.clientjetpack.ID1.Test.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

fun logPrixCurrencies(
    currencies: List<OutputNoSqlModel.Produit.ClientAchteur.TypeTarification.Prix>,
    isLastProduit: Boolean,
    isLastClient: Boolean,
    isLastType: Boolean,
) {
    currencies.forEachIndexed { currencyIndex, currency ->
        val isLastCurrency = currencyIndex == currencies.size - 1
        val currencyPrefix = when {
            isLastProduit && isLastClient && isLastType -> TreePrefix.Type6.get(isLastCurrency)
            else -> TreePrefix.Type7.get(isLastCurrency)
        }

        val (currencyDate, currencyTime) = strDateEtTempFromVidTimestamp(
            currency.vidTimestamp
        )

        // Using StringBuilder for more efficient string concatenation
        val currencyInfos = StringBuilder().apply {
            append(currencyPrefix)
            append(" Currency: ")
            append(currency.valeur)
            append(", Date: ")
            append(currencyDate)
            append(" Time: ")
            append(currencyTime)
        }.toString()

        println(currencyInfos)
    }
}
