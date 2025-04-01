package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class A_Produit(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

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
    fun hasRelevantChanges(oldData: A_Produit, newData: A_Produit): Boolean {
        return  oldData.nom != newData.nom ||  //<--
                oldData.parentCategoryId != newData.parentCategoryId ||
                oldData.indexInParentCategorie != newData.indexInParentCategorie ||
                oldData.isVisible != newData.isVisible
    }
    enum class NON_DISPO_POUR_CLIENTS(val color: Color) {
        DISPONIBLE_POUR_TOUT(Color(0xFF786C69)),
        TOUT(Color(0xFFF44336)),
        NEVEAU(Color(0xFFFF9800)),
        DEFINIE(Color(0xFF2196F3));
    }
}
