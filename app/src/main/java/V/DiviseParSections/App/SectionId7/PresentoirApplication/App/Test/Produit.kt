package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

data class Produit(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val infos: ProduitInfos = ProduitInfos(),
    val cesStatuesMutable: CesStatuesMutable = CesStatuesMutable(),
    val clients: List<Client> = emptyList(),
) {
    // The secondary constructor is no longer needed as default values are provided in the primary constructor

    data class ProduitInfos(
        val nom: String = ""
    )
    data class CesStatuesMutable(
        val cActiveDonsSonListParent: Boolean = false,
    )

    data class Client(
        val id: Long,
        val timestamp: Long,
        val infos: ClientInfos,
        val cesStatuesMutable: CesStatuesMutable= CesStatuesMutable(),
        val typesTarification: List<TypeTarification> = emptyList(),
    ) {
        constructor() : this(
            id = 0,
            timestamp = System.currentTimeMillis(),
            infos = ClientInfos(),
            cesStatuesMutable = CesStatuesMutable(),
            typesTarification = emptyList()
        )

        data class ClientInfos(
            val nom: String = ""
        )
        data class CesStatuesMutable(
            val cActiveDonsSonListParent: Boolean = false,
        )

        data class TypeTarification(
            val id: Long,
            val timestamp: Long,
            val infos: Infos,
            val PrixsCurrency: List<Prix>,
        ) {
            constructor() : this(
                id = 0,
                timestamp = System.currentTimeMillis(),
                infos = Infos(),
                PrixsCurrency = emptyList()
            )

            data class Infos(
                var type: TypeTarificationEnum = TypeTarificationEnum.NonDefini,
            )
            enum class TypeTarificationEnum {
                NonDefini,
                ParBenifice,
                Historique,
                LeMaxPrixArrive
            }

            data class Prix(
                val id: Long,
                val timestamp: Long,
                val valeur: Double,
            ) {
                constructor() : this(
                    id = 0,
                    timestamp = System.currentTimeMillis(),
                    valeur = 0.0
                )
            }
        }
    }
}
