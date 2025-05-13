package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test


data class TypeTarification(
    val id: Long = 0,
    val timestamp: Long = 0,
    val infos: Infos = Infos(),
    val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
    val parent: Parent = Parent(),
    val PrixsCurrency: List<Prix> = emptyList(),
) {
    data class Parent(
        var produit: Produit = Produit(),
    )

    data class Infos(
        var type: TypeTarificationEnum = TypeTarificationEnum.NonDefini,
    )

    data class CesStatuesMutable(
        val cActiveDonsSonListParent: Boolean = false,
    )

    enum class TypeTarificationEnum {
        NonDefini,
        ParBenifice,
        Historique,
        LeMaxPrixArrive
    }

    data class Prix(
        val id: Long = 0,
        val timestamp: Long = 0,
        val valeur: Double = 0.0,
    )
}

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
        val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
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
