package V.DiviseParSections.App.C_AtelieModbile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model

data class ProduitsNoSqlDataBase(
    val produits: List<Produit>,
) {
    data class Produit(
        val vidTimestamp: Long,
        val infosId: Long,
        var itsActiveOne: Boolean=false,

        val clientAchteurs: List<ClientAchteur>,
    ) {
        data class ClientAchteur(
            val vidTimestamp: Long,
            val infosId: Long,
            var itsActiveOne: Boolean=false,

            val typeTarification: List<TypeTarification>,
        ) {
            data class TypeTarification(
                val vidTimestamp: Long,
                val infosId: Long,
                var itsActiveOne: Boolean=false,

                val tariffsList: List<Tariff>,
            ) {
                data class Tariff(
                    val vidTimestamp: Long,
                    val valeur: Double,
                )
            }
        }
    }
}

