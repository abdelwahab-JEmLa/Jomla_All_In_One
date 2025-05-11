package com.example.clientjetpack.Id1.PrixChangable.Test.DataBase.Models

data class OutputNoSqlModel(
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
                val PrixsCurrency: List<Prix>,
            ) {
                data class Prix(
                    val vidTimestamp: Long,
                    val valeur: Double,
                )
            }
        }
    }
}
