package com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.W.Init

import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Z.Function.createTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Models.InputEtInfosSqlModels

/**
 * Données de test pour l'initialisation des tarifications
 * - Chaque produit doit avoir au moins un client
 * - Chaque client doit avoir au moins un type de tarification
 */
val initialTestData = listOf(
    // Produit 1, Client 1, TypeTarification 1
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 1, hour = 10, minute = 30),
        idProduit = 1L,
        idClient = 1L,
        idTypeTarification = 1L,
        prixCurrency = 10.99
    ),
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 1, hour = 11, minute = 30),
        idProduit = 1L,
        idClient = 1L,
        idTypeTarification = 2L,
        prixCurrency = 10.99
    ),
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 1, hour = 12, minute = 30),
        idProduit = 1L,
        idClient = 1L,
        idTypeTarification = 2L,
        prixCurrency = 10.99
    ),

    // Produit 2, Client 2, TypeTarification 2
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 2, hour = 11, minute = 45),
        idProduit = 2L,
        idClient = 2L,
        idTypeTarification = 2L,
        prixCurrency = 15.50
    ),
    
    // Produit 3, Client 3, TypeTarification 1
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 3, hour = 14, minute = 15),
        idProduit = 3L,
        idClient = 3L,
        idTypeTarification = 1L,
        prixCurrency = 8.75
    ),
    
    // S'assurer que chaque produit a au moins un client
    // Ajout d'un client pour le Produit 4 (qui semble manquer de clients)
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 4, hour = 9, minute = 0),
        idProduit = 4L,
        idClient = 1L,
        idTypeTarification = 2L,
        prixCurrency = 12.99
    ),
    
    // Ajouter des tarifications supplémentaires pour avoir plus de clients par produit
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 5, hour = 16, minute = 30),
        idProduit = 1L,
        idClient = 2L,
        idTypeTarification = 3L,
        prixCurrency = 9.99
    ),
    
    InputEtInfosSqlModels.Tarification(
        vidTimestamp = createTimestamp(day = 6, hour = 13, minute = 45),
        idProduit = 2L,
        idClient = 3L,
        idTypeTarification = 1L,
        prixCurrency = 17.25
    )
)

/**
 * Données de test pour les produits
 */
val initialProductsData = listOf(
    InputEtInfosSqlModels.ProduitInfos(
        id = 1L,
        nom = "Produit A"
    ),
    InputEtInfosSqlModels.ProduitInfos(
        id = 2L,
        nom = "Produit B"
    ),
    InputEtInfosSqlModels.ProduitInfos(
        id = 3L,
        nom = "Produit C"
    ),
    InputEtInfosSqlModels.ProduitInfos(
        id = 4L,
        nom = "Produit D"
    )
)

/**
 * Données de test pour les clients
 */
val initialClientsData = listOf(
    InputEtInfosSqlModels.ClientDataBase(
        id = 1L,
        nom = "Client Alpha",
        idActiveTypeTarificationDataBase = 1L
    ),
    InputEtInfosSqlModels.ClientDataBase(
        id = 2L,
        nom = "Client Beta",
        idActiveTypeTarificationDataBase = 2L
    ),
    InputEtInfosSqlModels.ClientDataBase(
        id = 3L,
        nom = "Client Gamma",
        idActiveTypeTarificationDataBase = 1L
    )
)


