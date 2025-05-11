package com.example.clientjetpack.Id1.PrixChangable.Test.Log

import com.example.clientjetpack.Id1.PrixChangable.Test.ViewModel.OutputViewModelNoSqlDB
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test._TestsDisplayerLogDataBase
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun _TestsDisplayerLogDataBase.logPrixCurrencies(
    currencies: List<OutputViewModelNoSqlDB.Produit.Client.TypeTarification.Prix>,
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

        val (currencyDate, currencyTime) = strDateEtTempFromVidTimestamp(currency.vidTimestamp)

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
