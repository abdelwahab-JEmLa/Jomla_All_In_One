package com.example.clientjetpack.Id1.PrixChangable.Test.Main.Modules

import com.example.clientjetpack.Id1.PrixChangable.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.Id1.PrixChangable.Test.Models.NoSql

fun mockOutputNoSqlModel(noSql: NoSql): OutputNoSqlModel {
    val produitInfos=  noSql.produitInfos
    val tarificationEntries=     noSql.tarificationEntries
    val clientDataBase=   noSql.clientDataBase

    val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

    for (produitDB in produitInfos) {
        val produitId = produitDB.id
        val produitClients = mutableListOf<OutputNoSqlModel.Produit.Client>()

        val uniqueClientIds = mutableSetOf<Long>()
        for (entry in tarificationEntries) {
            if (entry.idProduit == produitId) {
                uniqueClientIds.add(entry.idClient)
            }
        }

        for (clientId in uniqueClientIds) {
            val clientDB = clientDataBase.find { it.id == clientId }
            if (clientDB != null) {
                val clientEntries = tarificationEntries.filter {
                    it.idProduit == produitId && it.idClient == clientId
                }

                val uniqueTypeIds = clientEntries.map { it.idTypeTarification }.toSet()
                val typeTarifications =
                    mutableListOf<OutputNoSqlModel.Produit.Client.TypeTarification>()

                for (typeId in uniqueTypeIds) {
                    val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                        .sortedByDescending { it.vidTimestamp }

                    if (typeEntries.isNotEmpty()) {
                        val latestTimestamp = typeEntries.first().vidTimestamp
                        val priceList = typeEntries.map { entry ->
                            OutputNoSqlModel.Produit.Client.TypeTarification.Prix(
                                vidTimestamp = entry.vidTimestamp,
                                valeur = entry.prixCurrency
                            )
                        }

                        typeTarifications.add(
                            OutputNoSqlModel.Produit.Client.TypeTarification(
                                vidTimestamp = latestTimestamp,
                                id = typeId,
                                PrixsCurrency = priceList
                            )
                        )
                    }
                }

                if (typeTarifications.isNotEmpty()) {
                    val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }
                    produitClients.add(
                        OutputNoSqlModel.Produit.Client(
                            vidTimestamp = clientLatestTimestamp,
                            id = clientId,
                            typeTarification = typeTarifications
                        )
                    )
                }
            }
        }

        val produitLatestTimestamp = if (produitClients.isNotEmpty()) {
            produitClients.maxOf { it.vidTimestamp }
        } else {
            System.currentTimeMillis()
        }

        produitsList.add(
            OutputNoSqlModel.Produit(
                vidTimestamp = produitLatestTimestamp,
                id = produitId,
                clients = produitClients
            )
        )
    }

    return OutputNoSqlModel(produitsList)
}
