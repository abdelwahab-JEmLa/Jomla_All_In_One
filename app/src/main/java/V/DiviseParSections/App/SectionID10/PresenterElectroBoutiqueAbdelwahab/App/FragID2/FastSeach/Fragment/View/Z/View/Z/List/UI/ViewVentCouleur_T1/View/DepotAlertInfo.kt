package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

private fun getArabicProductPlural(count: Int): String {
    return when {
        count == 1 -> "منتج واحد"
        count == 2 -> "منتجين"
        count in 3..10 -> "$count منتجات"
        else -> "$count منتج"
    }
}

@Composable
fun DepotAlertInfo(
    alertInfo: DepotUpdateResult,
    onDismissRequeste: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequeste() },
        title = {
            Text(
                text = if (alertInfo.deficit > 0) "⚠️ نقص في المخزن" else "✓ تنبيه المخزن",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (alertInfo.deficit > 0) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "انتبه هناك ${getArabicProductPlural(alertInfo.deficit)} غير متوفرة في المخزن",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "✓ تم إضافة الطلب للجملة تلقائياً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismissRequeste() }) {
                Text("حسناً")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}
