package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.Dialogs

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import EntreApps.Shared.Models.M8BonVent
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun AddToStockDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    context: Context,
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent
) {
    val operationsToAdd = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
        it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
    }
    
    val totalItems = operationsToAdd.size
    val totalQuantity = operationsToAdd.sumOf { it.quantity }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Stock",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "إضافة إلى المخزون",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "هل تريد إضافة هذه المنتجات إلى المخزون؟",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "رقم المعاملة: ${relative_M8BonVent.keyID.takeLast(6)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "عدد المنتجات: $totalItems",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "الكمية الإجمالية: $totalQuantity",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "سيتم تغيير حالة المنتجات إلى 'متوفر' وإضافة الكميات",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    add_Bon_Au_Stock(
                        repositorysMainGetter,
                        repositorysMainSetter,
                        relative_M8BonVent,
                        context
                    )
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("تأكيد الإضافة", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

private fun add_Bon_Au_Stock(
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent,
    context: Context
) {
    val ventOperations = repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
        it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
    }
    
    ventOperations.forEach { vent ->
        val couleurProduit = repositorysMainGetter.repo03CouleurProduitInfos.datasValue.find {
            it.keyID == vent.parent_M3CouleurProduit_KeyID
        }
        
        val produit = repositorysMainGetter.repo1ProduitInfos.datasValue.find {
            it.keyID == vent.parent_M1Produit_KeyId
        }
        
        couleurProduit?.let { couleur ->
            repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(
                couleur.copy(
                    count_Don_Depot = couleur.count_Don_Depot + vent.quantity,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            )
        }
        
        produit?.let { prod ->
            repositorysMainSetter.upsert_M1Produit(
                prod.copy(
                    disponibilityEtates = DisponibilityEtates.DISPO,
                    count_Don_Depot = prod.count_Don_Depot + vent.quantity,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            )
        }
    }
    
    Toast.makeText(
        context,
        "تمت إضافة المنتجات إلى المخزون بنجاح",
        Toast.LENGTH_SHORT
    ).show()
}
