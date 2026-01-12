package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

enum class Utilisateur(
    val comp: String,
    val ayam_tadriss: String = "dimanch/jeudi",
    val nom_arab: String = ""
) {
    Admin("", "", "المسؤول"),
    Abdelwahab_Osstad(
        M18CentralParametresOfAllApps().abdelwahabTravailleChezGros_KeyId,
        "dimanch/jeudi",
        "عبدالوهاب حمنيش"
    ),
    Abdelmoumen(
        M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId,
        "dimanch/jeudi",
        "عبدالمؤمن"
    ),
    Walid(
        M18CentralParametresOfAllApps().walid_Compt_KeyId,
        "dimanch/jeudi",
        "وليد"
    ),
    Amine_Madrassa(
        M18CentralParametresOfAllApps().amine_madrasa_Compt_KeyId,
        "dimanch/jeudi",
        "أمين"
    );

    override fun toString(): String {
        return name
    }

    /**
     * Toggle to next utilisateur in cycle
     */
    fun toggle(): Utilisateur {
        return when (this) {
            Admin -> Abdelwahab_Osstad
            Abdelwahab_Osstad -> Abdelmoumen
            Abdelmoumen -> Walid
            Walid -> Amine_Madrassa
            Amine_Madrassa -> Admin
        }
    }

    /**
     * Get display name for UI
     */
    fun getDisplayName(): String {
        return when (this) {
            Admin -> "Admin (Tous)"
            Abdelwahab_Osstad -> "Abdelwahab Oustade"
            Abdelmoumen -> "Abdelmoumen"
            Amine_Madrassa -> "Amine Madrassa"
            Walid -> "Walid"
        }
    }


    companion object {
        /**
         * Get next utilisateur from current, or return Admin if null
         */
        fun toggleFrom(current: Utilisateur?): Utilisateur {
            return current?.toggle() ?: Abdelmoumen
        }

        /**
         * Find utilisateur by comp ID
         */
        fun fromCompId(compId: String): Utilisateur? {
            return values().find { it.comp == compId }
        }
    }
}

