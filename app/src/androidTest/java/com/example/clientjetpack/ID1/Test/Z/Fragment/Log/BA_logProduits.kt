package com.example.clientjetpack.ID1.Test.Z.Fragment.Log

import com.example.clientjetpack.ID1.Test.Z.Fragment.Passive.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel

fun logProduits(value: OutputNoSqlModel, viewModel: TarificationViewModel) {
        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

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
