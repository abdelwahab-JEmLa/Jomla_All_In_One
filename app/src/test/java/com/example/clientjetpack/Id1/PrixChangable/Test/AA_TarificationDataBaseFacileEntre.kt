package com.example.clientjetpack.Id1.PrixChangable.Test

import androidx.compose.runtime.mutableStateListOf
import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp

data class AA_TarificationDataBaseFacileEntre(
    val vidTimestamp: Long,
    val idProduit: Long,
    val idClient: Long,
    val idTypeTarification: Long,
    val prixCurrency: Double,
)

class TarificationDataBaseFacileEntre_RepositoryImp {
    var modelList: List<AA_TarificationDataBaseFacileEntre> =
        mutableStateListOf(
            // Test data for Caramels (product id 1)
            AA_TarificationDataBaseFacileEntre(
                vidTimestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                idProduit = 1L,
                idClient = 1L, // Client 1
                idTypeTarification = 1L, // ParBenifice
                prixCurrency = 2.99
            ),
            AA_TarificationDataBaseFacileEntre(
                vidTimestamp = createTimestamp(
                    day = 10,
                    hour = 14,
                    minute = 30
                ),
                idProduit = 1L,
                idClient = 1L,
                idTypeTarification = 2L,
                prixCurrency = 5.99
            ),

            // Test data for Chocolats (product id 2)
            AA_TarificationDataBaseFacileEntre(
                vidTimestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                idProduit = 2L,
                idClient = 2L,
                idTypeTarification = 2L, // ParBenifice
                prixCurrency = 4.99
            ),

        )

    fun add(data: AA_TarificationDataBaseFacileEntre) {
        val updatedList = modelList.toMutableList()
        updatedList.add(data)
        modelList = updatedList
    }
}
