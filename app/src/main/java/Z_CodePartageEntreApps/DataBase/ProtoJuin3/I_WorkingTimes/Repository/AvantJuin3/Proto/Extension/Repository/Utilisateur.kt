package Z_CodePartageEntreApps.DataBase.ProtoJuin3.I_WorkingTimes.Repository.AvantJuin3.Proto.Extension.Repository

import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps

enum class Utilisateur(val comp: String) {
    Admin(""),
    Abdelmoumen(M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId),
    Walid(M18CentralParametresOfAllApps().walid_Compt_KeyId);

    override fun toString(): String {
        return name
    }

    /**
     * Toggle to next utilisateur in cycle: Admin -> Abdelmoumen -> Walid -> Admin
     * When Admin is active, shows all data
     * When specific user is active, filters to show only their data
     */
    fun toggle(): Utilisateur {
        return when (this) {
            Admin -> Abdelmoumen
            Abdelmoumen -> Walid
            Walid -> Admin
        }
    }

    /**
     * Get display name for UI
     */
    fun getDisplayName(): String {
        return when (this) {
            Admin -> "Admin (Tous)"
            Abdelmoumen -> "Abdelmoumen"
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
         * Find utilisateur by compte key ID
         */
        fun fromCompKey(compKey: String): Utilisateur? {
            return values().firstOrNull { it.comp == compKey }
        }
    }
}
