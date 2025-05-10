package com.example.clientjetpack.Id1.PrixChangable.Test

import com.example.clientjetpack.Id1.PrixChangable.Test.Passive.createTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class AA_TarificationDataBaseFacileEntre(
    val vidTimestamp: Long,
    val idProduit: Long,
    val idClient: Long,
    val idTypeTarification: Long,
    val prixCurrency: Double,
)

class TarificationDataBaseFacileEntre_RepositoryImp {
    // Using MutableStateFlow to expose data changes
    private val _dataFlow = MutableStateFlow(
        listOf(
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
    )

    var modelList: List<AA_TarificationDataBaseFacileEntre>
        get() = _dataFlow.value
        set(value) {
            _dataFlow.value = value
        }

    fun add(
        data: AA_TarificationDataBaseFacileEntre,
        onSuccess: (AA_TarificationDataBaseFacileEntre) -> Unit = {},
    ) {
        _dataFlow.update { currentList ->
            currentList + data
        }
        onSuccess(data)
    }
}
