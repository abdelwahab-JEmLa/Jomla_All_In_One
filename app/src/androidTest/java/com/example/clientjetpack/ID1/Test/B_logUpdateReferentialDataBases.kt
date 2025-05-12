package com.example.clientjetpack.ID1.Test

/*
fun B_logUpdateReferentialDataBases(): Unit = runTest {
        // Ajoute une nouvelle tarification
        viewModel.addNewTestDataTarificationEtClient()

        // Vérifie que la mise à jour a été effectuée
        assertEquals(
            2L, // Maintenant 2L au lieu de 1L après l'appel à addNewTestDataTarificationEtClient
            viewModel.getSqlClient(1)?.idActiveTypeTarificationDataBase
        )

        val name = "A_DataBasesSepareReferential_AfterUpdate"
        val currentStrTime = strDateEtTempFromVidTimestamp(System.currentTimeMillis())

        println("\n========Après Update========\n")
        println(
            "======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                    "${currentStrTime.second} du  $name  ========"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val currentValue = viewModel.outputNoSqlFlow.first()
        assertTrue(currentValue.produits.isNotEmpty())

        // Vérifie que tous les produits ont au moins un client
        currentValue.produits.forEach { produit ->
            assertTrue(
                "Le produit ${produit.id} doit avoir au moins un client",
                produit.clients.isNotEmpty()
            )
        }

        println("\n-- Hierarchical Structure --")

        logProduits(
            currentValue,
            viewModel
        )

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")
    }
                 */
