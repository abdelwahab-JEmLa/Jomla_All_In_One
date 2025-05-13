package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

fun newData(data: List<TypeTarification>): TypeTarification {
    val newId = (data.maxOfOrNull { it.id } ?: 0) + 1
    val newData = TypeTarification(
        id = newId,
        timestamp = System.currentTimeMillis(),
        infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.NonDefini),
        parent = TypeTarification.Parent(
            produit = Produit(
                id = newId,
                timestamp = System.currentTimeMillis(),
                infos = Produit.ProduitInfos(nom = "Nouveau Produit $newId"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                clients = listOf(
                    Produit.Client(
                        id = newId,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Nouveau Client $newId"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = emptyList()
                    )
                )
            )
        ),
        PrixsCurrency = listOf(
            TypeTarification.Prix(
                id = 1L,
                timestamp = System.currentTimeMillis(),
                valeur = 0.0
            )
        )
    )
    return newData
}
