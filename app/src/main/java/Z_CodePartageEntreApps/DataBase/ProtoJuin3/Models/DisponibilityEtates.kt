package Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models

enum class DisponibilityEtates(val nomArabe: String = "") {
    DISPO("متوفر"),
    NON_DISPO("غير متوفر"),
    PETITE_PROBABILITY("احتمال كبير");

    fun toggleEntreEtates(): DisponibilityEtates = when (this) {
        DISPO -> NON_DISPO
        NON_DISPO -> PETITE_PROBABILITY
        PETITE_PROBABILITY -> DISPO
    }
}
