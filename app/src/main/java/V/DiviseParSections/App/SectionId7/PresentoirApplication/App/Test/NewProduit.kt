package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

fun newProduit(produits: List<Produit>): Produit {
    val newProduct = Produit(
        id = (produits.maxOfOrNull { it.id } ?: 0) + 1,
        timestamp = System.currentTimeMillis(),
        infos = Produit.ProduitInfos(nom = "Nouveau Produit"),
        clients = listOf(
            Produit.Client(
                id = 1000,
                timestamp = System.currentTimeMillis(),
                infos = Produit.Client.ClientInfos(nom = "Nouveau Client"),
                typesTarification = listOf(
                    Produit.Client.TypeTarification(
                        id = 10000,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.TypeTarification.Infos(nom = "Tarif Standard"),
                        PrixsCurrency = listOf(
                            Produit.Client.TypeTarification.Prix(
                                id = 100000,
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
