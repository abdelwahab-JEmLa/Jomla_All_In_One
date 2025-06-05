package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3

enum class DisponibilityEtates(val nomArabe: String = "") {
    DISPO("متوفر"),
    NON_DISPO("غير متوفر"),
    PETITE_PROBABILITY("احتمال كبير");

    fun toggleEntreEtates(): DisponibilityEtates = when (this) {
        DISPO -> NON_DISPO
        NON_DISPO -> PETITE_PROBABILITY
        PETITE_PROBABILITY -> DISPO
    }

    companion object {
        fun fromString(value: String): DisponibilityEtates {
            return when (value) {
                "DISPO" -> DISPO
                "NON_DISPO" -> NON_DISPO
                "PETITE_PROBABILITY" -> PETITE_PROBABILITY
                else -> DISPO // default value
            }
        }
    }
}
