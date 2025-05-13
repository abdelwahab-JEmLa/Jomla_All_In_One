package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

fun newProduit(produits: List<Produit>): Produit {
    val findproduit = produits
        .find { it.cesStatuesMutable.cActiveDonsSonListParent }
    val findclient = findproduit
        ?.clients?.find { it.cesStatuesMutable.cActiveDonsSonListParent }

    // Find the TypeTarification with id 3
    val typeTarification3 = findclient?.typesTarification?.find { it.id == 3L }

    // Calculate the new prix ID safely
    val newPrixId = typeTarification3?.PrixsCurrency?.maxOfOrNull { it.id }?.plus(1) ?: 1L

    val newProduct = Produit(
        id = findproduit?.id!!,
        timestamp = System.currentTimeMillis(),
        infos = Produit.ProduitInfos(nom = "Nouveau Produit"),
        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
        clients = listOf(
            Produit.Client(
                id = findclient?.id!!,
                timestamp = System.currentTimeMillis(),
                infos = Produit.Client.ClientInfos(nom = "Nouveau Client"),
                cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                typesTarification = listOf(
                    Produit.Client.TypeTarification(
                        id = 3,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.LeMaxPrixArrive),
                        PrixsCurrency = listOf(
                            Produit.Client.TypeTarification.Prix(
                                id = newPrixId,
                                timestamp = System.currentTimeMillis(),
                                valeur = 500.0
                            )
                        )
                    )
                )
            )
        )
    )
    return newProduct
}
