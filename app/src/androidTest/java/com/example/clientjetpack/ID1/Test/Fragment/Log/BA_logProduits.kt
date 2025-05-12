package com.example.clientjetpack.ID1.Test.Fragment.Log

import com.example.clientjetpack.ID1.Test.Fragment.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.ID1.Test.Fragment.ViewModel.TarificationViewModel

fun logProduits(value: com.example.clientjetpack.ID1.Test.Fragment.DataBase.Models.OutputNoSqlModel, viewModel: TarificationViewModel) {
        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix =TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(
                produit.vidTimestamp
            )
            val relatedInfos = viewModel.getSqlProduitInfos(produit.id)

            val produitInfos = StringBuilder().apply {
                append(produitPrefix)
                append(" Product : ")
                append(produit.id)
                append("=(${relatedInfos?.nom ?: " Unknown "})")
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(viewModel,produit.clients, isLastProduit)
        }
    }
