package com.example.clientjetpack.ID1.Test.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

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
                append(produit.infosId)
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(produit.clients, isLastProduit)
        }
    }
