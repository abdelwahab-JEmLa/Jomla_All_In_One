package Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _2_2_CouleursDataBase(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0,

    // Section InfosDeBase
    var nom: String = "",
    var emballageCartone: Boolean = false,

    // Section Etates Mutable
    var itsTempProduit: Boolean = false,
    var besoinToBeUpdated: Boolean = false,
    var non_Trouve: Boolean = false,
    var isVisible: Boolean = true,
    var imageGlidReloadTigger: Int = 0,
    var prePourCameraCapture: Boolean = true,
    var diponibilityEtate: Boolean = true,
    var probablementNonDispo: Boolean = true,
    var enumVarNonDispoPourClients: NON_DISPO_POUR_CLIENTS = NON_DISPO_POUR_CLIENTS.DISPONIBLE_POUR_TOUT,

    // Section sonCategory
    var parentCategoryId: Long = 0L,
    var indexInParentCategorie: Int = 0,

    // Section InfosCoutes
    var monPrixAchat: Double = 0.0,
    var monPrixVent: Double = 0.0,
) {
    enum class NON_DISPO_POUR_CLIENTS(val color: Color) {
        DISPONIBLE_POUR_TOUT(Color(0xFF4CAF50)),
        TOUT(Color(0xFFF44336)),
        NEVEAU(Color(0xFFFF9800)),
        DEFINIE(Color(0xFF2196F3));
    }
}
