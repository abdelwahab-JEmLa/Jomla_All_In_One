package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nom: String = "",
    // FIXED: Removed circular dependency - keyFireBase is now calculated after construction
    val keyFireBase: String = "",
    val timestamps: Long = System.currentTimeMillis(),
    val needUpdate: Boolean = true
) {
    // FIXED: Added a method to create a proper instance with keyFireBase
    fun withProperKeyFireBase(): A_ProduitInfos {
        return if (keyFireBase.isEmpty()) {
            this.copy(keyFireBase = getKeyFireBase(id, nom))
        } else {
            this
        }
    }

    companion object {
        // FIXED: Moved the creation logic to companion object to avoid circular dependency
        fun create(
            id: Long = 0L,
            nom: String = "",
            timestamps: Long = System.currentTimeMillis(),
            needUpdate: Boolean = true
        ): A_ProduitInfos {
            return A_ProduitInfos(
                id = id,
                nom = nom,
                keyFireBase = getKeyFireBase(id, nom),
                timestamps = timestamps,
                needUpdate = needUpdate
            )
        }
    }
}
