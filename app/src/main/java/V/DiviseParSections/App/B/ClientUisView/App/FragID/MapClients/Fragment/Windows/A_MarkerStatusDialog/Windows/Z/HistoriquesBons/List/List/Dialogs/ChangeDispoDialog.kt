package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChangeDispoDialog(
    onDismiss: () -> Unit,
    context: Context,
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent
) {
    val operationsToChange = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
        it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
    }
    
    val uniqueProducts = operationsToChange.map { it.parent_M1Produit_KeyId }.distinct().size
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Change Availability",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "تغيير حالة التوفر",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "اختر الحالة الجديدة للمنتجات:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "رقم المعاملة: ${relative_M8BonVent.keyID.takeLast(6)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "عدد المنتجات: $uniqueProducts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            change_Dispo_Produits(
                                DisponibilityEtates.DISPO,
                                repositorysMainGetter,
                                repositorysMainSetter,
                                relative_M8BonVent,
                                context
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "متوفر (DISPO)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = {
                            change_Dispo_Produits(
                                DisponibilityEtates.NON_DISPO,
                                repositorysMainGetter,
                                repositorysMainSetter,
                                relative_M8BonVent,
                                context
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "غير متوفر (NON_DISPO)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = {
                            change_Dispo_Produits(
                                DisponibilityEtates.PETITE_PROBABILITY,
                                repositorysMainGetter,
                                repositorysMainSetter,
                                relative_M8BonVent,
                                context
                            )
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "احتمال كبير (PETITE_PROBABILITY)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

private fun change_Dispo_Produits(
    newDisponibilityEtate: DisponibilityEtates,
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent,
    context: Context
) {
    val ventOperations = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
        it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
    }
    
    var successCount = 0
    
    ventOperations.forEach { vent ->
        val produit = repositorysMainGetter.repo1ProduitInfos.datasValue.find {
            it.keyID == vent.parent_M1Produit_KeyId
        }
        
        produit?.let { prod ->
            repositorysMainSetter.upsert_M1Produit(
                prod.copy(
                    disponibilityEtates = newDisponibilityEtate,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            )
            successCount++
        }
    }
    
    val message = when (newDisponibilityEtate) {
        DisponibilityEtates.DISPO -> "تم تغيير $successCount منتج إلى 'متوفر'"
        DisponibilityEtates.NON_DISPO -> "تم تغيير $successCount منتج إلى 'غير متوفر'"
        DisponibilityEtates.PETITE_PROBABILITY -> "تم تغيير $successCount منتج إلى 'احتمال كبير'"
    }
    
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
