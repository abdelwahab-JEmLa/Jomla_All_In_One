package com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test.Modules.Log

import com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test._A.Tests._ID1.Test.Z.Function.strDateEtTempFromVidTimestamp

fun logProduits(value: OutputNoSqlModel) {
        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(
                produit.vidTimestamp
            )

            val produitInfos = StringBuilder().apply {
                append(produitPrefix)
                append(" Product : ")
                append(produit.id)
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(produit.clients, isLastProduit)
        }
    }
