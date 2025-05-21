package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TypeTarificationEnum(val iconVector: ImageVector? = null, val couleur: Color = Color.White) {
    ParBenifice(Icons.Filled.ShoppingCart, Color(0xFF4CAF50)),
    Historique(Icons.Filled.History, Color(0xFF2196F3)),
    LeMaxPrixArrive(Icons.Filled.ArrowUpward, Color(0xFFFF9800)),
    PRIX_BASE(Icons.Filled.AttachMoney, Color(0xFFF44336))
}
