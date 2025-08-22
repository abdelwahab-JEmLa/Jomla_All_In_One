package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A

// Add these imports to CreditCard.kt
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraXDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
private fun DatePickerDialog(
    currentItem: TransactionItem,
    onDateSelected: (TransactionItem) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Generate list of last 7 days
    val last7Days = remember {
        val calendar = Calendar.getInstance()
        val days = mutableListOf<Pair<String, Long>>()

        repeat(7) { dayOffset ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)

            val dateStr = dateFormat.format(calendar.time)
            val timestamp = calendar.timeInMillis
            val dayName = when (dayOffset) {
                0 -> "Aujourd'hui"
                1 -> "Hier"
                else -> SimpleDateFormat("EEEE", Locale.FRENCH).format(calendar.time)
            }

            days.add("$dayName - $dateStr" to timestamp)
        }
        days
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choisir une date",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            LazyColumn {
                items(last7Days) { (dayLabel, timestamp) ->
                    TextButton(
                        onClick = {
                            // Create updated item with new timestamp and formatted date/time
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = timestamp
                                // Keep the same time as original or set to current time
                                val originalCalendar = Calendar.getInstance().apply {
                                    timeInMillis = currentItem.timestamp
                                }
                                set(Calendar.HOUR_OF_DAY, originalCalendar.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, originalCalendar.get(Calendar.MINUTE))
                                set(Calendar.SECOND, originalCalendar.get(Calendar.SECOND))
                            }

                            val updatedItem = currentItem.copy(
                                timestamp = calendar.timeInMillis,
                                date = dateFormat.format(calendar.time),
                                time = timeFormat.format(calendar.time)
                            )

                            onDateSelected(updatedItem)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dayLabel,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (last7Days.indexOf(dayLabel to timestamp) < last7Days.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

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
    var showImageDropdown by remember { mutableStateOf(false) }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var isDownloadingImage by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Get all available image paths
    val availableImages = remember(item) {
        listOfNotNull(
            item.receiptImagePath,
            item.receiptImage2Path,
            item.receiptImage3Path,
            item.receiptImage4Path
        ).filter { File(it).exists() }
    }

    // Firebase storage reference for receipts
    val storageRef = Firebase.storage.reference.child("Images Receipts").child("bons_achat")
    val localPath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BonsAchat"

    LaunchedEffect(item.receiptImagePath, item.receiptImage2Path, item.receiptImage3Path, item.receiptImage4Path, item.firebaseStoragePath) {
        checkAndDownloadAllImages(
            item = item,
            onImagesReady = { /* handled in availableImages computation */ },
            onDownloadStart = { isDownloadingImage = true },
            onDownloadEnd = { isDownloadingImage = false }
        )
    }

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
                    val firebaseFileName = "receipt_${item.id}_${System.currentTimeMillis()}.webp"
                    val firebaseStoragePath = "Images Receipts/bons_achat/$firebaseFileName"

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            storageRef.child(firebaseFileName).putBytes(bytes).await()
                        } catch (e: Exception) {
                            // Handle upload error silently
                        }
                    }

                    withContext(Dispatchers.Main) {
                        // Find the next available image slot and update the item
                        val updatedItem = when {
                            item.receiptImagePath == null -> item.copy(
                                receiptImagePath = localFile.absolutePath,
                                firebaseStoragePath = firebaseStoragePath
                            )
                            item.receiptImage2Path == null -> item.copy(
                                receiptImage2Path = localFile.absolutePath
                            )
                            item.receiptImage3Path == null -> item.copy(
                                receiptImage3Path = localFile.absolutePath
                            )
                            item.receiptImage4Path == null -> item.copy(
                                receiptImage4Path = localFile.absolutePath
                            )
                            else -> {
                                // All slots full, replace the first one
                                item.copy(
                                    receiptImagePath = localFile.absolutePath,
                                    firebaseStoragePath = firebaseStoragePath
                                )
                            }
                        }

                        onUpdateItem(updatedItem)

                        Toast.makeText(
                            context,
                            "Photo du bon d'achat sauvegardée: $firebaseFileName",
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
    if (showImageDialog && selectedImagePath != null) {
        ImageViewDialog(
            imagePath = selectedImagePath!!,
            onDismiss = {
                showImageDialog = false
                selectedImagePath = null
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Add some vertical padding
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.clickable {
                        showDatePicker = true
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${String.format("%.2f", item.credit)} DA",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Show download indicator
                if (isDownloadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Show image button with dropdown if multiple images available
                if (availableImages.isNotEmpty()) {
                    Box {
                        IconButton(
                            onClick = {
                                if (availableImages.size == 1) {
                                    selectedImagePath = availableImages.first()
                                    showImageDialog = true
                                } else {
                                    showImageDropdown = true
                                }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Voir photos bon d'achat",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                if (availableImages.size > 1) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Plus d'images",
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = showImageDropdown,
                            onDismissRequest = { showImageDropdown = false }
                        ) {
                            availableImages.forEachIndexed { index, imagePath ->
                                DropdownMenuItem(
                                    text = { Text("Image ${index + 1}") },
                                    onClick = {
                                        selectedImagePath = imagePath
                                        showImageDialog = true
                                        showImageDropdown = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Image,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                } else if (item.firebaseStoragePath != null && !isDownloadingImage) {
                    // Show download button if image is only in Firebase
                    IconButton(
                        onClick = {
                            scope.launch {
                                checkAndDownloadImage(
                                    item = item,
                                    onImageReady = { imagePath ->
                                        if (imagePath != null) {
                                            Toast.makeText(
                                                context,
                                                "Image téléchargée avec succès",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    onDownloadStart = { isDownloadingImage = true },
                                    onDownloadEnd = { isDownloadingImage = false }
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Télécharger photo",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
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
        // Date picker dialog
        if (showDatePicker) {
            DatePickerDialog(
                currentItem = item,
                onDateSelected = { updatedItem ->
                    showDatePicker = false
                    onUpdateItem(updatedItem)

                    // Update in Firebase
                    saveTransactionToFirebase(updatedItem)

                    Toast.makeText(
                        context,
                        "Date mise à jour: ${updatedItem.date}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }
    }
}
// FIX 1: Enhanced image download function in CreditCard.kt
// Replace the existing checkAndDownloadImage function with this enhanced version

suspend fun checkAndDownloadAllImages(
    item: TransactionItem,
    onImagesReady: (List<String>) -> Unit,
    onDownloadStart: () -> Unit,
    onDownloadEnd: () -> Unit
) {
    val imagePaths = listOfNotNull(
        item.receiptImagePath,
        item.receiptImage2Path,
        item.receiptImage3Path,
        item.receiptImage4Path
    )

    if (imagePaths.isEmpty()) {
        onImagesReady(emptyList())
        return
    }

    val availableImages = mutableListOf<String>()
    var downloadStarted = false

    // Check each image path
    for (imagePath in imagePaths) {
        val localFile = File(imagePath)

        if (localFile.exists()) {
            availableImages.add(imagePath)
        } else {
            // Try to download from Firebase if path exists
            val firebasePath = when (imagePath) {
                item.receiptImagePath -> item.firebaseStoragePath
                else -> null // For additional images, you might need separate Firebase paths
            }

            if (firebasePath != null) {
                if (!downloadStarted) {
                    onDownloadStart()
                    downloadStarted = true
                }

                val success = withContext(Dispatchers.IO) {
                    try {
                        val storageRef = Firebase.storage.reference.child(firebasePath)

                        // Create parent directories if they don't exist
                        localFile.parentFile?.mkdirs()

                        // Download the file
                        storageRef.getFile(localFile).await()
                        true
                    } catch (e: Exception) {
                        false
                    }
                }

                if (success) {
                    availableImages.add(imagePath)
                }
            }
        }
    }

    if (downloadStarted) {
        onDownloadEnd()
    }

    onImagesReady(availableImages)
}
@Composable
fun ImageViewDialog(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    imagePath: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val active_Central_Values = focusedValuesGetter.active_Central_Values

    fun handel_add_AuFlotatn(): Unit {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            focusedValuesGetter.update_activeCentralValues(
                active_Central_Values.copy(
                    image_Flotant = imageFile
                )
            )
            Toast.makeText(
                context,
                "Image ajoutée au flotant",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Erreur: Fichier image introuvable",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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

                // Add to Float button
                IconButton(
                    onClick = { handel_add_AuFlotatn() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.CloudDownload, // Or use a more appropriate icon
                        contentDescription = "Ajouter au flotant",
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

// Helper function for image management
suspend fun checkAndDownloadImage(
    item: TransactionItem,
    onImageReady: (String?) -> Unit,
    onDownloadStart: () -> Unit,
    onDownloadEnd: () -> Unit
) {
    val localPath = item.receiptImagePath

    // Check if local file exists
    if (localPath != null && File(localPath).exists()) {
        onImageReady(localPath)
        return
    }

    // If local file doesn't exist but Firebase path is available, download it
    val firebasePath = item.firebaseStoragePath
    if (firebasePath != null && localPath != null) {
        onDownloadStart()

        val success = withContext(Dispatchers.IO) {
            try {
                val storageRef = Firebase.storage.reference.child(firebasePath)
                val localFile = File(localPath)

                // Create parent directories if they don't exist
                localFile.parentFile?.mkdirs()

                // Download the file
                storageRef.getFile(localFile).await()
                true
            } catch (e: Exception) {
                false
            }
        }

        onDownloadEnd()
        onImageReady(if (success) localPath else null)
    } else {
        onImageReady(null)
    }
}
