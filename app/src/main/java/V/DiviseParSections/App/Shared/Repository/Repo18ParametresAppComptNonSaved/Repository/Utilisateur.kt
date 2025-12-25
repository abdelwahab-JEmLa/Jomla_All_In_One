package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

enum class Utilisateur(val comp: String) {
    Admin(""),
    Abdelmoumen(M18CentralParametresOfAllApps().abdelmomen_Compt_KeyId),
    Walid(M18CentralParametresOfAllApps().walid_Compt_KeyId),
    Amine_Madrassa(M18CentralParametresOfAllApps().amine_madrasa_Compt_KeyId),
    ;

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
            Abdelmoumen -> "Abdelmoumen"
            Amine_Madrassa -> "Amine_Madrassa"
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

    }
}
