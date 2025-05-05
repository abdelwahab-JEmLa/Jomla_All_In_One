package com.example.clientjetpack.Repositorys

data class StrNomJourEtSonSemainToStartJourTimeTemp (
    val vid: Long = 0,
    val nomJourArabe: String,
    val estDonLaSemainDistantDe: Int,
    val jourEstEntreTimeTemp: Pair<Long, Long>,
    val key: String = "vid->estDonLaSemainDistant(nomJourArabe)",
)
