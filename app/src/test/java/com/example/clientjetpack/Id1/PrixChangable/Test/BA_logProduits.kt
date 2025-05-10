package com.example.clientjetpack.Id1.PrixChangable.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.TreePrefix
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun _TestsDisplayerLogDataBase.logProduits(value: A_DataBase_Imbricant) {
        val produitRepository = B_GroupeRepositoryImp.ProduitDataBase_RepositoryImp()

        value.produits.forEachIndexed { produitIndex, produit ->
            val isLastProduit = produitIndex == value.produits.size - 1
            val produitPrefix = TreePrefix.Type1.get(isLastProduit)

            val (produitDate, produitTime) = strDateEtTempFromVidTimestamp(produit.vidTimestamp)
            val relatedInfos = produitRepository.modelList.find { it.id == produit.id }

            val produitInfos = StringBuilder().apply {
                append(produitPrefix)
                append(" Product : ")
                append(produit.id)
                append("=(${relatedInfos?.nom ?: " Unknown "})")
            }.toString()

            println("$produitInfos, Date: $produitDate Time: $produitTime (${produit.clients.size} clients)")

            logClients(produit.clients, isLastProduit)
        }
    }
