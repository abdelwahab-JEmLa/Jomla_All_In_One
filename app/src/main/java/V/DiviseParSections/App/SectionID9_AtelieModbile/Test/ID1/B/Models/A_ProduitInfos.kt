package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nom: String = "",
    val keyFireBase: String = getKeyFireBase(id, nom),
    val timestamps: Long = System.currentTimeMillis(),

    val needUpdate: Boolean = true
)
