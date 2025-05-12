package com.example.clientjetpack.ID1.Test.Fragment.Log

import com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp

fun logPrixCurrencies(
    currencies: List<com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel.Produit.Client.TypeTarification.Prix>,
    isLastProduit: Boolean,
    isLastClient: Boolean,
    isLastType: Boolean,
) {
    currencies.forEachIndexed { currencyIndex, currency ->
        val isLastCurrency = currencyIndex == currencies.size - 1
        val currencyPrefix = when {
            isLastProduit && isLastClient && isLastType -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type6.get(isLastCurrency)
            else -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type7.get(isLastCurrency)
        }

        val (currencyDate, currencyTime) = com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp(
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
