package com.example.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DiviseurDeDisplayProductForEachClient(
    @PrimaryKey(autoGenerate = true) // Fixed: Made primary key auto-increment
    val vidSu: Long = 0,           // "${client.idClientsSu}->${product.idArticle}"
    var keyVid: String = "",
    var idClientsSu: Long = 0,
    var nomClientsSu: String = "",
    var productId: Long = 0,
    var productName: String = "",
    var itsBigImage: Boolean = false,
    var deniedFromDislplayToClient: Boolean = true
) {
    // No-argument constructor for Room compatibility
    constructor() : this(0)
}
