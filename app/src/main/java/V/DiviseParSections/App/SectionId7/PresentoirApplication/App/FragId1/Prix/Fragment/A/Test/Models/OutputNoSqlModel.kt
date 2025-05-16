package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.A.Test.Models

data class OutputNoSqlModel(
    val produits: List<Produit>,
) {
    data class Produit(
        val vidTimestamp: Long,
        val infosId: Long,
        val clientAchteurs: List<ClientAchteur>,
    ) {
        data class ClientAchteur(
            val vidTimestamp: Long,
            val infosId: Long,
            val typeTarification: List<TypeTarification>,
        ) {
            data class TypeTarification(
                val vidTimestamp: Long,
                val infosId: Long,
                val PrixsCurrency: List<Prix>,
            ) {
                data class Prix(
                    val vidTimestamp: Long,
                    val valeur: Double,
                )
            }
        }
    }
}
