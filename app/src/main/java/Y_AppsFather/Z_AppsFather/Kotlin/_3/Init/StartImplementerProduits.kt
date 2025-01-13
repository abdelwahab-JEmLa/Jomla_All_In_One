package com.example.Z_AppsFather.Kotlin._3.Init

import Y_AppsFather.Kotlin.ModelAppsFather
import Y_AppsFather.Kotlin.ModelAppsFather.Companion.UpdateFireBase
import com.example.Z_AppsFather.Kotlin._3.Init.Z.Parent.GetAncienDataBasesMain
import java.text.SimpleDateFormat
import java.util.Locale


suspend fun CreeNewStart(
    _appsHeadModel: ModelAppsFather,
    NOMBRE_ENTRE: Int,
) {
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val ancienData = GetAncienDataBasesMain()

        // Predefined clients for consistent data
        val clients = listOf(
            Triple(1L, "Client Alpha", "#FF5733"),
            Triple(2L, "Client Beta", "#33FF57"),
            Triple(3L, "Client Gamma", "#5733FF"),
            Triple(4L, "Client Delta", "#FF33E6"),
            Triple(5L, "Client Epsilon", "#33FFF3")
        )

        // Predefined grossists
        val grossists = listOf(
            Triple(1L, "Grossist Alpha", "#FF5733"),
            Triple(2L, "Grossist Beta", "#33FF57"),
            Triple(3L, "Grossist Gamma", "#5733FF")
        )

        // Process each product in the ancien database
        ancienData.produitsDatabase.forEachIndexed { index, ancien ->
            val depuitAncienDataBase = ModelAppsFather.ProduitModel(
                id = ancien.idArticle,
                itsTempProduit = ancien.idArticle > 2000,
                init_nom = ancien.nomArticleFinale,
                init_visible = false,
                init_besoin_To_Be_Updated = true
            )

            // Add colors/tastes
            listOf(
                ancien.idcolor1 to 1L,
                ancien.idcolor2 to 2L,
                ancien.idcolor3 to 3L,
                ancien.idcolor4 to 4L
            ).forEach { (colorId, position) ->
                ancienData.couleurs_List.find { it.idColore == colorId }?.let { couleur ->
                    depuitAncienDataBase.coloursEtGouts.add(
                        ModelAppsFather.ProduitModel.ColourEtGout_Model(
                            position_Du_Couleur_Au_Produit = position,
                            nom = couleur.nameColore,
                            imogi = couleur.iconColore,
                            sonImageNeExistPas = depuitAncienDataBase.itsTempProduit && position == 1L,
                        )
                    )
                }
            }
                  /*

            // Generate sales history
            repeat(Random.nextInt(1, 6)) { _ ->
                val saleDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, -Random.nextInt(1, 31))
                }.time

                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = ModelAppsFather.ProduitModel.ClientBonVentModel(
                    init_clientInformations = ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = depuitAncienDataBase.coloursEtGouts.take(Random.nextInt(1, 4))
                        .map { couleur ->
                            ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = Random.nextInt(1, 11),
                                imogi = couleur.imogi
                            )
                        }
                )
                depuitAncienDataBase.historiqueBonsVents.add(bonVent)
            }

            // Generate current sales
            repeat(Random.nextInt(1, 4)) { _ ->
                val (clientId, clientName, clientColor) = clients.random()

                val bonVent = ModelAppsFather.ProduitModel.ClientBonVentModel(
                    init_clientInformations = ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations(
                        id = clientId,
                        nom = clientName,
                        couleur = clientColor
                    ),
                    init_colours_achete = depuitAncienDataBase.coloursEtGouts.take(Random.nextInt(1, 4))
                        .map { couleur ->
                            ModelAppsFather.ProduitModel.ClientBonVentModel.ColorAchatModel(
                                vidPosition = couleur.position_Du_Couleur_Au_Produit,
                                nom = couleur.nom,
                                quantity_Achete = Random.nextInt(1, 11),
                                imogi = couleur.imogi
                            )
                        }
                )
                if (ancien.idArticle < 100 || ancien.idArticle > 2000) {
                    depuitAncienDataBase.bonsVentDeCetteCota.add(bonVent)
                }
            }

            // Handle grossist orders
            if (ancien.idArticle < 100 || ancien.idArticle > 2000) {
                val (grossistId, grossistName, grossistColor) = grossists.random()
                val currentDate = dateFormat.format(Calendar.getInstance().time)

                // Calculate total sales quantities and prepare colors for grossist
                val totalSalesQuantities = calculateTotalSalesQuantities(depuitAncienDataBase)
                val grossisteColoursToOrder = if (depuitAncienDataBase.itsTempProduit) {
                    depuitAncienDataBase.coloursEtGouts.take(1)
                } else {
                    depuitAncienDataBase.coloursEtGouts.take(Random.nextInt(1, 5))
                }

                val grossiste = ModelAppsFather.ProduitModel.GrossistBonCommandes(
                    vid = grossistId,
                    init_grossistInformations = ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations(
                        id = grossistId,
                        nom = grossistName,
                        couleur = grossistColor
                    ).apply {
                        positionInGrossistsList = grossistId.toInt() - 1
                    },
                    date = currentDate,
                    date_String_Divise = currentDate.split(" ")[0],
                    time_String_Divise = currentDate.split(" ")[1],
                    currentCreditBalance = Random.nextDouble(1000.0, 2001.0),
                    init_position_Produit_Don_Grossist_Choisi_Pour_Acheter_CeProduit =
                    if (Random.nextDouble() < 0.4) 0 else Random.nextInt(1, 11),
                    init_coloursEtGoutsCommendee = distributeQuantitiesAmongGrossists(
                        totalSalesQuantities,
                        grossisteColoursToOrder,
                        Triple(grossistId, grossistName, grossistColor)
                    )
                )

                depuitAncienDataBase.bonCommendDeCetteCota = grossiste
                depuitAncienDataBase.historiqueBonsCommend.add(grossiste)
            }

            // Update product status
            depuitAncienDataBase.let { pro ->
                pro.statuesBase.prePourCameraCapture =
                    (pro.bonCommendDeCetteCota?.positionProduitDonGrossistChoisiPourAcheterCeProduit
                        ?: 0) > 0 && pro.itsTempProduit
            }
               */
            // Add product to main database
            _appsHeadModel.produitsMainDataBase.add(depuitAncienDataBase)
        }

        // Clear and update Firebase database
        ModelAppsFather.produitsFireBaseRef.removeValue()
        UpdateFireBase(_appsHeadModel.produitsMainDataBase)

    } catch (e: Exception) {
        throw e
    }
}
   /*
// Calculate total sales quantities per color from bon vents
private fun calculateTotalSalesQuantities(depuitAncienDataBase: ModelAppsFather.ProduitModel): Map<Long, Int> {
    return depuitAncienDataBase.bonsVentDeCetteCota
        .flatMap { bonVent -> bonVent.colours_Achete }
        .groupBy { it.vidPosition }
        .mapValues { (_, colorSales) ->
            colorSales.sumOf { it.quantity_Achete }
        }
}

// Distribute total quantities among grossists
private fun distributeQuantitiesAmongGrossists(
    totalQuantities: Map<Long, Int>,
    colors: List<ModelAppsFather.ProduitModel.ColourEtGout_Model>,
    grossist: Triple<Long, String, String>
): List<ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee> {
    return colors.map { couleur ->
        val totalQuantity = totalQuantities[couleur.position_Du_Couleur_Au_Produit] ?: 0
        // Add some randomness but keep it proportional to total sales
        val quantity = if (totalQuantity > 0) {
            (totalQuantity * (0.8 + Random.nextDouble() * 0.4)).toInt() // Random factor between 80% and 120%
        } else {
            Random.nextInt(10, 51) // Fallback to original random range if no sales data
        }

        ModelAppsFather.ProduitModel.GrossistBonCommandes.ColoursGoutsCommendee(
            id = couleur.position_Du_Couleur_Au_Produit,
            nom = couleur.nom,
            emoji = couleur.imogi,
            init_quantityAchete = quantity
        )
    }
}
    */
