package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BluetoothPrintHandler {
    private val PRINT_INTENT = "pe.diegoveloper.printing"

    fun printBluetoothReceipt(
        context: Context,
        client: M2Client?,
        operations: List<M10OperationVentCouleur>,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit,
        bonVent: M8BonVent? = null,
        showCreditSection: Boolean = false,
        versement: Double = 0.0
    ): Boolean {
        if (!isBluetoothAvailable()) {
            return false
        }

        if (operations.isEmpty()) {
            return false
        }

        return try {
            val transactionId = "vent_${System.currentTimeMillis().toString().takeLast(4)}"
            
            val (texteImprimable, _) = prepareTexteToPrint(
                operations,
                client?.nom?.takeIf { it.isNotBlank() } ?: "Client",
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
                client?.currentCreditBalance ?: 0.0,
                repo13TarificationInfos,
                repoM1Produit
            )

            val finalBluetoothText = if (showCreditSection && bonVent != null && false) {
                addCreditSectionToBluetoothText(
                    texteImprimable.toString(), 
                    client, 
                    bonVent, 
                    versement, 
                    transactionId
                )
            } else {
                texteImprimable.toString()
            }

            handleBluetoothPrint(context, finalBluetoothText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun printCreditBluetoothReceipt(
        context: Context,
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double> = emptyList(),
        showPaymentHistory: Boolean = false
    ): Boolean {
        if (!isBluetoothAvailable()) {
            return false
        }

        return try {
            val transactionId = bonVent.keyID.takeLast(4)
            val bluetoothText = prepareCreditBluetoothText(
                client, bonVent, previousPayments, showPaymentHistory, transactionId
            )
            handleBluetoothPrint(context, bluetoothText)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun isBluetoothAvailable(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    private fun handleBluetoothPrint(context: Context, texteImprimable: String) {
        val intent = Intent(PRINT_INTENT).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, texteImprimable)
        }
        ContextCompat.startActivity(context, intent, null)
    }

    private fun prepareTexteToPrint(
        operations: List<M10OperationVentCouleur>,
        nomClient: String,
        dateString: String,
        ancienCredits: Double,
        repo13TarificationInfos: Repo13TarificationInfos,
        repoM1Produit: RepoM1Produit
    ): Pair<StringBuilder, Double> {
        val groupe_Produit = operations.groupBy { it.parent_M1Produit_KeyId }.toList()
        val texteImprimable = StringBuilder()
        var totaleBon = 0.0
        var pageCounter = 0

        texteImprimable.apply {
            append("<BIG><CENTER>Abdelwahab<BR>")
            append("<BIG><CENTER>JeMla.Com<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER>Facture<BR>")
            append("<BR>")
            append("<SMALL><CENTER>$nomClient                        $dateString<BR>")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<SMALL><BOLD>   Quantité      Tariff        <NORMAL>Sous-total<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
        }

        groupe_Produit.forEachIndexed { index, produit_vent ->
            val datas_repo13TarificationInfos = repo13TarificationInfos.datasValue
            val standart_Vent = produit_vent.second.first()
            val relative_Tariffication = datas_repo13TarificationInfos.find { it.keyID == standart_Vent.parentM13TarificationKeyID }
            val relative_M1Produit = repoM1Produit.datasValue.find { it.keyID == produit_vent.first }
            val quantite_Boit_Par_Carton = relative_M1Produit?.quantite_Boit_Par_Carton ?: 1
            val vent_quantity = produit_vent.second.sumOf { it.quantity }
            val quantityDisplay = formatQuantityDisplay(vent_quantity, quantite_Boit_Par_Carton)
            val vent_prix = relative_Tariffication!!.prixCurrency
            val subtotal = vent_prix * vent_quantity

            if (subtotal != 0.0) {
                texteImprimable.apply {
                    append("<MEDIUM1><LEFT>${relative_M1Produit?.nom}<BR>")
                    append(" <MEDIUM1><LEFT>$quantityDisplay ")
                    append("<MEDIUM1><LEFT>${vent_prix}Da ")
                    append("<SMALL>$subtotal<BR>")
                    append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
                }
                totaleBon += subtotal
                if ((index + 1) % 15 == 0) {
                    pageCounter++
                    texteImprimable.append("<BR><CENTER>PAGE $pageCounter<BR><BR><BR>")
                }
            }
        }

        texteImprimable.apply {
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR><BR>")
            append("<MEDIUM1><CENTER>Total<BR>")
            append("<MEDIUM3><CENTER>${round(totaleBon * 10) / 10}Da<BR>")

            if (ancienCredits < 0) {
                append("<MEDIUM1><CENTER>Credit Du Compte actuel<BR>")
                append("<MEDIUM2><CENTER>${round(ancienCredits * 10) / 10}Da<BR>")
            }

            append("<CENTER>---------------------<BR>")
            append("<BR><BR><BR>>")
        }

        return Pair(texteImprimable, totaleBon)
    }

    private fun formatQuantityDisplay(quantity: Int, quantiteBoitParCarton: Int): String {
        return if (quantiteBoitParCarton in 2..quantity && quantity % quantiteBoitParCarton == 0) {
            val cartons = quantity / quantiteBoitParCarton
            "$cartons X $quantiteBoitParCarton"
        } else {
            quantity.toString()
        }
    }

    private fun addCreditSectionToBluetoothText(
        originalText: String,
        client: M2Client?,
        bonVent: M8BonVent,
        versement: Double,
        transactionId: String
    ): String {
        val oldBalance = client?.currentCreditBalance ?: 0.0
        val currentBill = bonVent.sum_De_Totale_Vents
        val newBalance = oldBalance + currentBill - versement
        val baseText = originalText.replace("<BR><BR><BR>>", "")

        return StringBuilder().apply {
            append(baseText)
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR>")
            append("<MEDIUM1><CENTER>SECTION CREDIT<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Ancien Solde :<BR>")
            append("<MEDIUM2><CENTER>${round(oldBalance)}Da<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Bon actuel :<BR>")
            append("<MEDIUM2><CENTER>${round(currentBill)}Da<BR>")
            append("<BR>")

            append("<MEDIUM1><LEFT>Versement :<BR>")
            append("<MEDIUM2><CENTER>${round(versement)}Da<BR>")
            append("<BR>")

            append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
            append("<MEDIUM1><LEFT>Nouv. Solde :<BR>")

            when {
                newBalance > 0 -> {
                    append("<MEDIUM3><RIGHT><BOLD>${round(newBalance)}Da<BR>")
                    append("<SMALL><CENTER>(Reste a payer)<BR>")
                }
                newBalance < 0 -> {
                    append("<MEDIUM3><RIGHT><BOLD>${round(newBalance)}Da<BR>")
                    append("<SMALL><CENTER>(Credit client)<BR>")
                }
                else -> {
                    append("<MEDIUM2><CENTER>0.00 Da<BR>")
                    append("<MEDIUM1><CENTER> ✓ SOLDE ✓<BR>")
                }
            }

            append("<BR>")
            append("<SMALL><CENTER>Transaction: #$transactionId<BR>")
            append("<BR><BR><BR>>")
        }.toString()
    }

    private fun prepareCreditBluetoothText(
        client: M2Client?,
        bonVent: M8BonVent,
        previousPayments: List<Double>,
        showPaymentHistory: Boolean,
        transactionId: String
    ): String {
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val clientName = client?.nom?.takeIf { it.isNotBlank() } ?: "Client"
        val totalAmount = bonVent.sum_De_Totale_Vents
        val currentPayment = bonVent.versement
        val totalPaid = if (showPaymentHistory) previousPayments.sum() + currentPayment else currentPayment
        val remainingAmount = totalAmount - totalPaid

        return StringBuilder().apply {
            append("<BIG><CENTER>Abdelwahab<BR>")
            append("<BIG><CENTER>JeMla.Com<BR>")
            append("<SMALL><CENTER>0553885037<BR>")
            append("<SMALL><CENTER> - Credit Payment${if (showPaymentHistory) " Prix_Detaille" else ""}<BR>")
            append("<BR>")
            append("<SMALL><CENTER>$clientName                        $dateString<BR>")
            append("<BR>")
            append("<LEFT><NORMAL><MEDIUM1>=====================<BR>")
            append("<BR>")

            if (showPaymentHistory) {
                append("<SMALL><LEFT>Transaction: #$transactionId<BR>")
                append("<BR>")
            }

            append("<MEDIUM1><LEFT>${if (showPaymentHistory) "Montant Total" else "Total a Payer"} :<BR>")
            append("<MEDIUM2><CENTER>${round(totalAmount)}Da<BR>")
            append("<BR>")

            if (showPaymentHistory && previousPayments.isNotEmpty()) {
                append("<SMALL><LEFT>Paiements Precedents:<BR>")
                previousPayments.forEachIndexed { index, payment ->
                    append("<SMALL><LEFT>  ${index + 1}. ${round(payment)}Da<BR>")
                }
                append("<SMALL><LEFT>Sous-total: ${round(previousPayments.sum())}Da<BR>")
                append("<BR>")
                append("<MEDIUM1><LEFT>Paiement Actuel:<BR>")
            } else {
                append("<MEDIUM1><LEFT>Versement Effectue:<BR>")
            }

            append("<MEDIUM2><CENTER>${round(currentPayment)}Da<BR>")
            append("<BR>")

            if (showPaymentHistory) {
                append("<SMALL><LEFT>Total Paye: ${round(totalPaid)}Da<BR>")
                append("<BR>")
            }

            append("<LEFT><NORMAL><MEDIUM1>---------------------<BR>")
            when {
                remainingAmount > 0 -> {
                    append("<MEDIUM1><LEFT> ${if (showPaymentHistory) "Reste a Payer" else "Credit Restant"} :<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(remainingAmount)}Da<BR>")
                }
                remainingAmount < 0 -> {
                    append("<MEDIUM1><LEFT> ${if (showPaymentHistory) "Trop Paye" else "Surplus Paye"} :<BR>")
                    append("<MEDIUM3><RIGHT><BOLD>${round(-remainingAmount)}Da<BR>")
                }
                else -> {
                    if (showPaymentHistory) {
                        append("<MEDIUM1><CENTER> ✓ PAYE COMPLETEMENT ✓<BR>")
                        append("<MEDIUM2><CENTER>Merci pour votre confiance<BR>")
                    } else {
                        append("<MEDIUM1><CENTER> PAYE INTEGRALEMENT<BR>")
                        append("<MEDIUM2><CENTER> ✓ SOLDE<BR>")
                    }
                }
            }

            if (!showPaymentHistory) {
                append("<BR>")
                append("<SMALL><CENTER>Transaction: #$transactionId<BR>")
            }

            append("<BR><BR><BR>>")
        }.toString()
    }

    private fun round(value: Double): Double = kotlin.math.round(value * 10) / 10.0
}
