package com.example.clientjetpack.Id1.PrixChangable.Test

import android.icu.util.Currency

data class A_DataBase_Imbricant(
    val produits: List<Produit>,
) {
    data class Produit(
        val vidTimestamp: Long,
        val id: Long,
        val clients: List<Client>,
    ) {
        data class Client(
            val vidTimestamp: Long,
            val id: Long,
            val typeTarification: List<TypeTarification>,
        ) {
            data class TypeTarification(
                val vidTimestamp: Long,
                val id: Long,
                val PrixsCurrency: List<PrixCurrency>,
            ) {
                data class PrixCurrency(
                    val vidTimestamp: Long,
                    val currency: Currency,
                )
            }
        }
    }
}
