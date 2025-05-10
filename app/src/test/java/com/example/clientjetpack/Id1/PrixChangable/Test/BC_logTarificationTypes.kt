package com.example.clientjetpack.Id1.PrixChangable.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.TreePrefix
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.strDateEtTempFromVidTimestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
fun _TestsDisplayerLogDataBase.logTarificationTypes(
        types: List<A_DataBase_Imbricant.Produit.Client.TypeTarification>,
        isLastProduit: Boolean,
        isLastClient: Boolean,
    ) {
        val typeRepository = B_GroupeRepositoryImp.TypeTarificationDataBase_RepositoryImp()
        val clientRepository = B_GroupeRepositoryImp.clientRepository

        val currentClient = viewModel.imbriquantFlow.value.produits
            .flatMap { it.clients }
            .find { client -> client.typeTarification.any { types.contains(it) } }

        if (currentClient != null) {
            val clientInfo = clientRepository.modelList.find { it.id == currentClient.id }

            types.forEachIndexed { typeIndex, type ->
                val isLastType = typeIndex == types.size - 1
                val typePrefix = when {
                    isLastProduit && isLastClient -> TreePrefix.Type4.get(isLastType)
                    isLastClient -> "  │     ${if (isLastType) "└─" else "├─"}"
                    else -> "  │     ${if (isLastType) "└─" else "├─"}"
                }

                val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)
                val typeInfo = typeRepository.modelList.find { it.id == type.id }

                val isActive = clientInfo?.idActiveTypeTarificationDataBase == type.id
                val activeStatus = if (isActive) " [ACTIVE]" else ""

                val typeInfos = StringBuilder().apply {
                    append(typePrefix)
                    append(" Tarification Type : ")
                    append(type.id)
                    append("=(${typeInfo?.typeTarificationEnum ?: "Unknown"})")
                    append(activeStatus)
                    append(" , Date: ")
                    append(typeDate)
                    append(" Time: ")
                    append(typeTime)
                    append(" (${type.PrixsCurrency.size} currencies)")
                }.toString()

                println(typeInfos)

                logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
            }
        }
    }
