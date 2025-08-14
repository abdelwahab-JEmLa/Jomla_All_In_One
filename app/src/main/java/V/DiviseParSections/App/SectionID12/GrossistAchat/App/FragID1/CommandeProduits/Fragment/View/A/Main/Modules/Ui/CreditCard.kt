package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui

import Z_CodePartageEntreApps.Modules.CameraHandler.CameraXDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@Composable
fun CreditCard(
    item: TransactionItem,
    onDelete: () -> Unit,
    onUpdateItem: (TransactionItem) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCameraDialog by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Firebase storage reference for receipts
    val storageRef = Firebase.storage.reference.child("Images Receipts").child("bons_achat")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BonsAchat"

    suspend fun handleReceiptImageCapture(uri: Uri) {
        if (isProcessing) return
        isProcessing = true

        try {
            val fileName = "receipt_${item.id}_${System.currentTimeMillis()}.webp"
            val localDir = File(localPath).apply { if (!exists()) mkdirs() }
            val localFile = File(localDir, fileName)

            withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val bytes = input.readBytes()

                    FileOutputStream(localFile).use { output ->
                        output.write(bytes)
                        output.flush()
                    }

                    // Upload to Firebase Storage
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            storageRef.child(fileName).putBytes(bytes).await()
                        } catch (e: Exception) {
                            // Handle upload error silently
                        }
                    }

                    withContext(Dispatchers.Main) {
                        // Update the item with the image path
                        val updatedItem = item.copy(receiptImagePath = localFile.absolutePath)
                        onUpdateItem(updatedItem)

                        Toast.makeText(
                            context,
                            "Photo du bon d'achat sauvegardée: $fileName",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Erreur lors de la sauvegarde: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } finally {
            isProcessing = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCameraDialog = true
        } else {
            Toast.makeText(context, "Permission caméra requise", Toast.LENGTH_SHORT).show()
        }
    }

    // Dialog pour afficher l'image
    if (showImageDialog && item.receiptImagePath != null) {
        ImageViewDialog(
            imagePath = item.receiptImagePath,
            onDismiss = { showImageDialog = false }
        )
    }

    // Dialog pour la caméra
    if (showCameraDialog) {
        CameraXDialog(
            onImageCaptured = { uri ->
                showCameraDialog = false
                scope.launch { handleReceiptImageCapture(uri) }
            },
            onDismiss = {
                showCameraDialog = false
                isProcessing = false
            },
            webPQuality = 85
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nouveau crédit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${item.date} à ${item.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.credit} DA",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Show image button (only if image exists)
                if (item.receiptImagePath != null && File(item.receiptImagePath).exists()) {
                    IconButton(
                        onClick = { showImageDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Voir photo bon d'achat",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Camera button for receipt photo
                IconButton(
                    onClick = {
                        if (!isProcessing) {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Photo bon d'achat",
                        modifier = Modifier.size(16.dp),
                        tint = if (isProcessing)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageViewDialog(imagePath: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = Color.White
                    )
                }

                // Image display
                AsyncImage(
                    model = imagePath,
                    contentDescription = "Photo du bon d'achat",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
