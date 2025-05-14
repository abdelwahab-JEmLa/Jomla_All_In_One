package com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models

data class OutputNoSqlModel(
    val produits: List<com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models.OutputNoSqlModel.Produit>,
) {
    data class Produit(
        val vidTimestamp: Long,
        val id: Long,
        val clients: List<com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models.OutputNoSqlModel.Produit.Client>,
    ) {
        data class Client(
            val vidTimestamp: Long,
            val id: Long,
            val typeTarification: List<com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models.OutputNoSqlModel.Produit.Client.TypeTarification>,
        ) {
            data class TypeTarification(
                val vidTimestamp: Long,
                val id: Long,
                val PrixsCurrency: List<com.example.clientjetpack.ID1.Test._A.Tests.Packages.Models.OutputNoSqlModel.Produit.Client.TypeTarification.Prix>,
            ) {
                data class Prix(
                    val vidTimestamp: Long,
                    val valeur: Double,
                )
            }
        }
    }
}
