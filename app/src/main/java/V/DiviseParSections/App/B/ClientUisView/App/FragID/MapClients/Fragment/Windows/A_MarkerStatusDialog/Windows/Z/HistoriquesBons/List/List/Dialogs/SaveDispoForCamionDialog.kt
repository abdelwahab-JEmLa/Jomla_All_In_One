package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import EntreApps.Shared.Models.M8BonVent
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SaveDispoForCamionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    context: Context,
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent
) {
    val operationsToSave = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
        it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
    }
    
    val productsWithStates = operationsToSave.mapNotNull { vent ->
        repositorysMainGetter.repo1ProduitInfos.datasValue.find {
            it.keyID == vent.parent_M1Produit_KeyId
        }
    }.distinctBy { it.keyID }
    
    val uniqueProducts = productsWithStates.size
    val dispoCount = productsWithStates.count { it.disponibilityEtates == DisponibilityEtates.DISPO }
    val nonDispoCount = productsWithStates.count { it.disponibilityEtates == DisponibilityEtates.NON_DISPO }
    val petiteCount = productsWithStates.count { it.disponibilityEtates == DisponibilityEtates.PETITE_PROBABILITY }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Save",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "حفظ حالة التوفر للشاحنة",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "سيتم حفظ الحالة الحالية لجميع المنتجات في:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "disponibilityEtates_Pour_presentaion_par_Camion",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "رقم المعاملة: ${relative_M8BonVent.keyID.takeLast(6)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        HorizontalDivider()
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "إجمالي المنتجات: $uniqueProducts",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "متوفر: $dispoCount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "غير متوفر: $nonDispoCount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFF44336)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Help,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = "احتمال: $petiteCount",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سيتم نسخ الحالة الحالية للاستخدام في عرض الشاحنة",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    save_Current_Dispo_To_Camion_Presentation(
                        repositorysMainGetter,
                        repositorysMainSetter,
                        relative_M8BonVent,
                        context
                    )
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "حفظ للشاحنة",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

private fun save_Current_Dispo_To_Camion_Presentation(
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
            val currentState = prod.disponibilityEtates
            
            repositorysMainSetter.upsert_M1Produit(
                prod.copy(
                    disponibilityEtates_Pour_presentaion_par_Camion = currentState,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            )
            
            successCount++
        }
    }
    
    Toast.makeText(
        context,
        "تم حفظ حالة $successCount منتج لعرض الشاحنة",
        Toast.LENGTH_SHORT
    ).show()
}
