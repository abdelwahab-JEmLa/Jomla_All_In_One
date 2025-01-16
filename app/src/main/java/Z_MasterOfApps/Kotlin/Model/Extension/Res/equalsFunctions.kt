package Z_MasterOfApps.Kotlin.Model.Extension.Res

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import java.util.Objects

fun _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations.equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations) return false
    return id == other.id &&
            nom == other.nom &&
            couleur == other.couleur
}

 fun _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations.hashCode(): Int {
    return Objects.hash(id, nom, couleur)
}
