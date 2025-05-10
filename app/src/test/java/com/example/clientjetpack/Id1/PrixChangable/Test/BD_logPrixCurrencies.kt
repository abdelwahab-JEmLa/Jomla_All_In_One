package com.example.clientjetpack.Id1.PrixChangable.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun _TestsDisplayerLogDataBase.logPrixCurrencies(
        currencies: List<A_DataBase_Imbricant.Produit.Client.TypeTarification.Prix>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
        isLastType: Boolean,
    ) {
        currencies.forEachIndexed { currencyIndex, currency ->
            val isLastCurrency = currencyIndex == currencies.size - 1
            val currencyPrefix = when {
                isLastProduit && isLastClient && isLastType -> "          ${if (isLastCurrency) "└─" else "├─"}"
                isLastClient && isLastType -> "          ${if (isLastCurrency) "└─" else "├─"}"
                else -> "  │     │  ${if (isLastCurrency) "└─" else "├─"}"
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
