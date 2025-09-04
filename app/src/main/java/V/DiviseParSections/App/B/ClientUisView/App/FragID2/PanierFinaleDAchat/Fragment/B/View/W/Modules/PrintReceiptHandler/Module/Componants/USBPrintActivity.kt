package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Componants

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class USBPrintActivity : AppCompatActivity() {

    private lateinit var usbManager: UsbManager
    private var targetPrinter: UsbDevice? = null

    companion object {
        private const val ACTION_USB_PERMISSION = "com.yourapp.USB_PERMISSION"
        private const val USB_VENDOR_ID_EPSON = 0x04b8
        private const val USB_VENDOR_ID_BROTHER = 0x04f9
        private const val USB_VENDOR_ID_CANON = 0x04a9
    }

    private val usbPermissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.apply {
                                targetPrinter = this
                                Toast.makeText(
                                    context,
                                    "Imprimante USB connectée: $productName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Permission USB refusée", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    device?.let { checkAndRequestPermission(it) }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (device == targetPrinter) {
                        targetPrinter = null
                        Toast.makeText(context, "Imprimante USB déconnectée", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbManager = getSystemService(USB_SERVICE) as UsbManager

        // Enregistrer les listeners USB
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(usbPermissionReceiver, filter)

        // Vérifier les imprimantes déjà connectées
        checkForConnectedPrinters()

        // Test d'impression
        testPrintSimpleReceipt()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbPermissionReceiver)
    }

    private fun checkForConnectedPrinters() {
        val deviceList = usbManager.deviceList
        deviceList.values.forEach { device ->
            if (isPrinterDevice(device)) {
                checkAndRequestPermission(device)
            }
        }
    }

    private fun isPrinterDevice(device: UsbDevice): Boolean {
        // Vérifier par Vendor ID (marques courantes)
        val knownPrinterVendors = listOf(
            USB_VENDOR_ID_EPSON,
            USB_VENDOR_ID_BROTHER,
            USB_VENDOR_ID_CANON,
            0x04e8, // Samsung
            0x03f0  // HP
        )

        return knownPrinterVendors.contains(device.vendorId) ||
               device.deviceClass == 7 || // Printer class
               device.productName?.lowercase()?.contains("print") == true
    }

    private fun checkAndRequestPermission(device: UsbDevice) {
        if (usbManager.hasPermission(device)) {
            targetPrinter = device
            Toast.makeText(this, "Imprimante prête: ${device.productName}", Toast.LENGTH_SHORT).show()
        } else {
            val permissionIntent = PendingIntent.getBroadcast(
                this, 0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            usbManager.requestPermission(device, permissionIntent)
        }
    }

    // ============================================================================
    // MÉTHODES DE PRINT FACILES
    // ============================================================================

    fun testPrintSimpleReceipt() {
        // Créer un PDF simple en mémoire
        val pdfFile = createSimpleReceiptPDF()

        // Imprimer via Android Print Framework
        printPDFFile(pdfFile, "Test Receipt")
    }

    private fun createSimpleReceiptPDF(): File {
        val file = File(cacheDir, "test_receipt.pdf")

        // Utilisation d'iText pour créer un PDF simple
        try {
            val writer = com.itextpdf.kernel.pdf.PdfWriter(file.absolutePath)
            val pdfDoc = com.itextpdf.kernel.pdf.PdfDocument(writer)
            val document = com.itextpdf.layout.Document(pdfDoc)

            // Contenu simple
            document.add(com.itextpdf.layout.element.Paragraph("REÇU DE TEST"))
            document.add(com.itextpdf.layout.element.Paragraph("=================="))
            document.add(com.itextpdf.layout.element.Paragraph("Produit: Test Item"))
            document.add(com.itextpdf.layout.element.Paragraph("Quantité: 1"))
            document.add(com.itextpdf.layout.element.Paragraph("Prix: 100.00 Da"))
            document.add(com.itextpdf.layout.element.Paragraph("=================="))
            document.add(com.itextpdf.layout.element.Paragraph("TOTAL: 100.00 Da"))

            document.close()

        } catch (e: Exception) {
            e.printStackTrace()
            // Créer un fichier vide si erreur
            file.createNewFile()
        }

        return file
    }

    private fun printPDFFile(file: File, jobName: String) {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager

        val printAdapter = SimplePDFPrintAdapter(file)

        // Attributs d'impression pour USB
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("default", "default", 300, 300))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

        // Lancer l'impression
        printManager.print(jobName, printAdapter, printAttributes)

        Toast.makeText(this, "Document envoyé vers l'imprimante USB", Toast.LENGTH_SHORT).show()
    }

    // ============================================================================
    // ADAPTER POUR PDF PRINT
    // ============================================================================

    private inner class SimplePDFPrintAdapter(private val pdfFile: File) : PrintDocumentAdapter() {

        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes,
            cancellationSignal: android.os.CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle?
        ) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }

            val info = android.print.PrintDocumentInfo.Builder(pdfFile.name)
                .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(android.print.PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()

            callback.onLayoutFinished(info, true)
        }

        override fun onWrite(
            pages: Array<out android.print.PageRange>?,
            destination: android.os.ParcelFileDescriptor,
            cancellationSignal: android.os.CancellationSignal?,
            callback: WriteResultCallback
        ) {
            try {
                pdfFile.inputStream().use { input ->
                    FileOutputStream(destination.fileDescriptor).use { output ->
                        input.copyTo(output)
                    }
                }
                callback.onWriteFinished(arrayOf(android.print.PageRange.ALL_PAGES))
            } catch (e: Exception) {
                callback.onWriteFailed(e.message)
                e.printStackTrace()
            }
        }
    }

    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================

    fun printExistingPDF(pdfFilePath: String, jobName: String = "Document") {
        val file = File(pdfFilePath)
        if (file.exists()) {
            printPDFFile(file, jobName)
        } else {
            Toast.makeText(this, "Fichier PDF non trouvé", Toast.LENGTH_SHORT).show()
        }
    }

    fun isUSBPrinterConnected(): Boolean {
        return targetPrinter != null
    }

    fun getConnectedPrinterInfo(): String? {
        return targetPrinter?.let {
            "Marque: ${it.manufacturerName}\n" +
            "Modèle: ${it.productName}\n" +
            "ID: ${it.vendorId}:${it.productId}"
        }
    }

    // ============================================================================
    // INTÉGRATION AVEC VOTRE CLASSE EXISTANTE
    // ============================================================================

    // Ajoutez cette méthode dans votre PrintInPdf_itextpdf_Handler
    /*
    suspend fun generateAndPrintViaUSB(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        tarificationRepo: Repo13TarificationInfos,
        produitRepo: RepoM1Produit,
        transactionId: String = ""
    ): Result<String> {

        return try {
            // 1. Générer le PDF
            val pdfResult = generateVentReceiptPdf(
                context, client, operations,
                tarificationRepo, produitRepo, transactionId
            )

            pdfResult.fold(
                onSuccess = { path ->
                    val filePath = path.substringAfter("PDF saved: ").substringBefore("\nFirebase:")

                    // 2. Imprimer via USB
                    val usbActivity = context as? USBPrintActivity
                    usbActivity?.printExistingPDF(filePath, "Receipt_$transactionId")

                    Result.success("PDF généré et envoyé vers imprimante USB")
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    */
}
