package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

data class Produit(
    val id: Long = 0,
    val timestamp: Long = 0,
    val infos: ProduitInfos = ProduitInfos(),
    val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
    val clients: List<Client> = emptyList(),
) {
    data class ProduitInfos(
        val nom: String = ""
    )
    data class CesStatuesMutable(
        val cActiveDonsSonListParent: Boolean = false,
    )

    data class Client(
        val id: Long = 0,
        val timestamp: Long = 0,
        val infos: ClientInfos = ClientInfos(),
        val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(cActiveDonsSonListParent = true),
        val typesTarification: List<TypeTarification> = emptyList(),
    ) {
        data class ClientInfos(
            val nom: String = ""
        )
        data class CesStatuesMutable(
            val cActiveDonsSonListParent: Boolean = false,
        )
    }
}

fun hardData(): List<Produit> {
    return listOf(
        Produit(
            id = 1L,
            timestamp = createTimestamp(day = 10, month = 5, hour = 9, minute = 15),
            infos = Produit.ProduitInfos(nom = "Produit Premium"),
            cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
            clients = listOf(
                Produit.Client(
                    id = 101L,
                    timestamp = createTimestamp(day = 11, month = 5, hour = 10, minute = 0),
                    infos = Produit.Client.ClientInfos(nom = "Client A"),
                    typesTarification = emptyList()
                )
            )
        ),
        Produit(
            id = 2L,
            timestamp = createTimestamp(day = 11, month = 5, hour = 14, minute = 30),
            infos = Produit.ProduitInfos(nom = "Produit Standard"),
            cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
            clients = listOf(
                Produit.Client(
                    id = 102L,
                    timestamp = createTimestamp(day = 12, month = 5, hour = 8, minute = 45),
                    infos = Produit.Client.ClientInfos(nom = "Client B"),
                    typesTarification = emptyList()
                )
            )
        ),
        Produit(
            id = 3L,
            timestamp = createTimestamp(day = 12, month = 5, hour = 16, minute = 0),
            infos = Produit.ProduitInfos(nom = "Produit Basic"),
            cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
            clients = listOf(
                Produit.Client(
                    id = 103L,
                    timestamp = createTimestamp(day = 12, month = 5, hour = 17, minute = 20),
                    infos = Produit.Client.ClientInfos(nom = "Client C"),
                    typesTarification = emptyList()
                )
            )
        ),
        Produit(
            id = 4L,
            timestamp = createTimestamp(day = 13, month = 5, hour = 8, minute = 0),
            infos = Produit.ProduitInfos(nom = "Produit Deluxe"),
            cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
            clients = listOf(
                Produit.Client(
                    id = 104L,
                    timestamp = createTimestamp(day = 13, month = 5, hour = 9, minute = 10),
                    infos = Produit.Client.ClientInfos(nom = "Client D"),
                    typesTarification = emptyList()
                )
            )
        )
    )
}
