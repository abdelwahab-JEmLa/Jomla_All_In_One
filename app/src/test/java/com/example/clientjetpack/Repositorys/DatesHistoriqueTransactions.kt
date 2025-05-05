package com.example.clientjetpack.Repositorys

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Class to manage historical transaction dates and their states
 */
class DatesHistoriqueTransactions {
    var semaines by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var jours by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var clientTransactions by mutableStateOf<Map<Long, List<Long>>>(emptyMap())

    var etate by mutableStateOf<Map<Long, EtateActuellementEst>>(emptyMap())

    fun init() {

    }

}
