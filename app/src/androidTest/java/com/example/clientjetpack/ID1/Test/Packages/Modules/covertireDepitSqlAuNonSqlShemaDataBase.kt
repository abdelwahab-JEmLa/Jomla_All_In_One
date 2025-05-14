package com.example.clientjetpack.ID1.Test.Packages.Modules

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test.Models.OutputNoSqlModel

fun covertireDepitSqlAuNonSqlShemaDataBase(noSqlDataBases: com.example.clientjetpack.ID1.Test.Packages.Models.NoSqlDataBases): OutputNoSqlModel {
    val tarificationEntries = noSqlDataBases.tarificationEntries
    val produitInfos = noSqlDataBases.produitInfos
    val clientDataBase = noSqlDataBases.clientDataBase

    val produitsList = mutableListOf<OutputNoSqlModel.Produit>()

    // Process each product in the database
    for (produitDB in produitInfos) {
        val produitId = produitDB.id
        val produitClientAchteurs = mutableListOf<OutputNoSqlModel.Produit.ClientAchteur>()

        // Find clientAchteurs for this product
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
                    mutableListOf<OutputNoSqlModel.Produit.ClientAchteur.TypeTarification>()

                for (typeId in uniqueTypeIds) {
                    val typeEntries = clientEntries.filter { it.idTypeTarification == typeId }
                        .sortedByDescending { it.vidTimestamp }

                    if (typeEntries.isNotEmpty()) {
                        val latestTimestamp = typeEntries.first().vidTimestamp
                        val priceList = typeEntries.map { entry ->
                            OutputNoSqlModel.Produit.ClientAchteur.TypeTarification.Prix(
                                vidTimestamp = entry.vidTimestamp,
                                valeur = entry.prixCurrency
                            )
                        }

                        typeTarifications.add(
                            OutputNoSqlModel.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = latestTimestamp,
                                infosId = typeId,
                                PrixsCurrency = priceList
                            )
                        )
                    }
                }

                if (typeTarifications.isNotEmpty()) {
                    val clientLatestTimestamp = typeTarifications.maxOf { it.vidTimestamp }
                    produitClientAchteurs.add(
                        OutputNoSqlModel.Produit.ClientAchteur(
                            vidTimestamp = clientLatestTimestamp,
                            infosId = clientId,
                            typeTarification = typeTarifications
                        )
                    )
                }
            }
        }

        // Even if no clientAchteurs found, still include the product in the list
        val produitLatestTimestamp = if (produitClientAchteurs.isNotEmpty()) {
            produitClientAchteurs.maxOf { it.vidTimestamp }
        } else {
            System.currentTimeMillis()
        }

        produitsList.add(
            OutputNoSqlModel.Produit(
                vidTimestamp = produitLatestTimestamp,
                infosId = produitId,
                clientAchteurs = produitClientAchteurs
            )
        )
    }

    return OutputNoSqlModel(produitsList)
}
