package com.example.clientjetpack.ID3.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.DataBase.A.SQL.Models.DataBasesInfosSql
import org.junit.Assert.assertEquals

fun TypeTarifications(
    expected: DataBasesInfosSql,
    actual: DataBasesInfosSql
) {
    assertEquals(
        "Type Tarifications list size should match",
        expected.c_TypeTarificationInfos.size,
        actual.c_TypeTarificationInfos.size
    )

    expected.c_TypeTarificationInfos.forEach { expectedType ->
        val actualType = actual.c_TypeTarificationInfos.find { it.id == expectedType.id }
            ?: throw AssertionError("Type Tarification with ID ${expectedType.id} not found")

        assertEquals(
            "Type Tarification enum should match for ID ${expectedType.id}",
            expectedType.entityCorrespond,
            actualType.entityCorrespond
        )
    }
}
fun Tarifications(
    expected: DataBasesInfosSql,
    actual: DataBasesInfosSql
) {
    // Tarifications
    assertEquals(
        "Tarifications list size should match",
        expected.d_TarificationInfos.size,
        actual.d_TarificationInfos.size
    )

    expected.d_TarificationInfos.forEach { expectedTarif ->
        val actualTarif =
            actual.d_TarificationInfos.find { it.vidTimestamp == expectedTarif.vidTimestamp }
                ?: throw AssertionError("Tarification with timestamp ${expectedTarif.vidTimestamp} not found")

        assertEquals(
            "Tarification product ID should match for timestamp ${expectedTarif.vidTimestamp}",
            expectedTarif.idProduit,
            actualTarif.idProduit
        )
        assertEquals(
            "Tarification client ID should match for timestamp ${expectedTarif.vidTimestamp}",
            expectedTarif.idClient,
            actualTarif.idClient
        )
        assertEquals(
            "Tarification type ID should match for timestamp ${expectedTarif.vidTimestamp}",
            expectedTarif.idTypeTarification,
            actualTarif.idTypeTarification
        )
        assertEquals(
            "Tarification price should match for timestamp ${expectedTarif.vidTimestamp}",
            expectedTarif.prixCurrency,
            actualTarif.prixCurrency,
            0.01
        )
    }
}

fun Clients(
    expected: DataBasesInfosSql,
    actual: DataBasesInfosSql
) {
    // Clients
    assertEquals(
        "Clients list size should match",
        expected.b_ClientInfosList.size,
        actual.b_ClientInfosList.size
    )

    expected.b_ClientInfosList.forEach { expectedClient ->
        val actualClient = actual.b_ClientInfosList.find { it.id == expectedClient.id }
            ?: throw AssertionError("Client with ID ${expectedClient.id} not found")

        assertEquals(
            "Client name should match for ID ${expectedClient.id}",
            expectedClient.nom,
            actualClient.nom
        )
        assertEquals(
            "Client active tarification ID should match for ID ${expectedClient.id}",
            expectedClient.idActiveTypeTarificationDataBase,
            actualClient.idActiveTypeTarificationDataBase
        )
    }
}

fun Products(
    expected: DataBasesInfosSql,
    actual: DataBasesInfosSql
) {
    // Products
    assertEquals(
        "Products list size should match",
        expected.a_ProduitInfos.size,
        actual.a_ProduitInfos.size
    )

    expected.a_ProduitInfos.forEach { expectedProduct ->
        val actualProduct = actual.a_ProduitInfos.find { it.id == expectedProduct.id }
            ?: throw AssertionError("Product with ID ${expectedProduct.id} not found")

        assertEquals(
            "Product name should match for ID ${expectedProduct.id}",
            expectedProduct.nom,
            actualProduct.nom
        )
    }
}
