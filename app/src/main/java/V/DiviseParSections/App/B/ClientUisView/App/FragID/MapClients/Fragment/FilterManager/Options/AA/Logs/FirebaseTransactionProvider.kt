package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA.Logs

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Provider for fetching transaction data from Firebase
 */
object FirebaseTransactionProvider {

    private const val FIREBASE_URL = "https://abdelwahab-jemla-com-default-rtdb.europe-west1.firebasedatabase.app"
    private const val TRANSACTION_PATH = "00_DataPrototype-04-02/_2_productionTestRef/D_TransactionCommercialDataBAse"

    /**
     * Fetches transaction data from Firebase database
     * Implementation of the fetchTransactionsFromFirebase function that was previously a placeholder
     */
    suspend fun fetchTransactionsFromFirebase(): List<D_Repo_TransactionCommercial> {
        return withContext(Dispatchers.IO) {
            val database = FirebaseDatabase.getInstance(FIREBASE_URL)
            val reference = database.getReference(TRANSACTION_PATH)

            // 2. Query _2_productionTestRef/D_TransactionCommercialDataBAse using coroutines
            suspendCancellableCoroutine { continuation ->
                reference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            // 3. Convert Firebase results to D_Repo_TransactionCommercial objects
                            val transactions = mutableListOf<D_Repo_TransactionCommercial>()
                            
                            for (transactionSnapshot in snapshot.children) {
                                // Parse the transaction path which contains parent ID and client info
                                // Format: "(parentId)->(clientId_(clientName))->(state)"
                                val firebaseKey = transactionSnapshot.key ?: continue
                                
                                try {
                                    // Parse transaction data from snapshot
                                    val transaction = parseTransactionSnapshot(transactionSnapshot, firebaseKey)
                                    transactions.add(transaction)
                                } catch (e: Exception) {
                                    println("Error parsing transaction $firebaseKey: ${e.message}")
                                    // Continue with other transactions even if one fails
                                }
                            }
                            
                            // 4. Return the list
                            continuation.resume(transactions)
                        } catch (e: Exception) {
                            continuation.resumeWithException(
                                Exception("Failed to parse transaction data: ${e.message}", e)
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(
                            Exception("Firebase query cancelled: ${error.message}", error.toException())
                        )
                    }
                })
                
                // If coroutine is cancelled, remove the listener
                continuation.invokeOnCancellation {
                    reference.removeEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {}
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
        }
    }
    
    /**
     * Parses a Firebase DataSnapshot into a D_Repo_TransactionCommercial object
     */
    private fun parseTransactionSnapshot(
        snapshot: DataSnapshot, 
        firebaseKey: String
    ): D_Repo_TransactionCommercial {
        // Extract basic fields with null-safety
        val clientId = snapshot.child("clientAcheteurID").getValue(Long::class.java) ?: 0L
        val clientName = snapshot.child("nomClientConcerned").getValue(String::class.java) ?: "Non Defini"
        val timestamp = snapshot.child("timestamps").getValue(Long::class.java) ?: Date().time
        val parentId = snapshot.child("parentVID_1_4_PeriodeVent").getValue(Long::class.java) ?: 0L
        val heurDebut = snapshot.child("heurDebutInString").getValue(String::class.java) ?: "00:00"
        val heurFin = snapshot.child("heurFinInString").getValue(String::class.java) ?: "Non Defini"
        val ouvert = snapshot.child("ouvert").getValue(Boolean::class.java) ?: false
        val vid = snapshot.child("vid").getValue(Long::class.java) ?: 0L
        
        // Parse transaction state - this requires mapping from the string state to the enum
        val stateStr = snapshot.child("etateActuellementEst").getValue(String::class.java)
        val state = parseTransactionState(stateStr)
        
        // Process "vocale" (voice) related fields
        val vocaleKeyID = snapshot.child("vocaleKeyID").getValue(String::class.java) ?: ""
        val sonVocaleEstEcoute = snapshot.child("sonVocaleEstEcoute").getValue(Boolean::class.java) ?: false
        val sonEcoutementEstFaitAutimestamps = 
            snapshot.child("sonEcoutementEstFaitAutimestamps").getValue(Long::class.java) ?: 0L
        
        // Parse additional flags
        val cJustPourVoirPanie = snapshot.child("cjustPourVoirPanie").getValue(Boolean::class.java) ?: false
        
        // Create and return the transaction object
        return D_Repo_TransactionCommercial(
            vid = vid,
            parentVID_1_4_PeriodeVent = parentId,
            clientAcheteurID = clientId, 
            nomClientConcerned = clientName,
            timestamps = timestamp,
            heurDebutInString = heurDebut,
            heurFinInString = heurFin,
            ouvert = ouvert,
            vocaleKeyID = vocaleKeyID,
            sonVocaleEstEcoute = sonVocaleEstEcoute,
            sonEcoutementEstFaitAutimestamps = sonEcoutementEstFaitAutimestamps,
            cJustPourVoirPanie = cJustPourVoirPanie,
            etateActuellementEst = state
        )
    }
    
    /**
     * Maps a string state value to the Type enum
     */
    private fun parseTransactionState(stateStr: String?): Type {
        return when (stateStr) {
            "ON_MODE_COMMEND_ACTUELLEMENT" -> Type.ON_MODE_COMMEND_ACTUELLEMENT
            "A_COMMANDE_CONFIRME" -> Type.A_COMMANDE_CONFIRME
            "COMMANDE_LIVRAI" -> Type.COMMANDE_LIVRAI
            "AVEC_MARCHANDISE" -> Type.AVEC_MARCHANDISE
            "ACHETEUR_NON_DISPO" -> Type.ACHETEUR_NON_DISPO
            "FERME" -> Type.FERME
            "A_EVITE" -> Type.A_EVITE
            "RAPPORT_AU_ENREGESTREMENT_VOCALE" -> Type.RAPPORT_AU_ENREGESTREMENT_VOCALE
            "ON_MODE_VOIRE_PANIE_ARTICLES" -> Type.ON_MODE_VOIRE_PANIE_ARTICLES
            "Cible" -> Type.Cible
            "CIBLE_PRIORITE_2" -> Type.CIBLE_PRIORITE_2
            "CIBLE_PRIORITE_3" -> Type.CIBLE_PRIORITE_3
            "CIBLE_POUR_2" -> Type.CIBLE_POUR_2
            else -> Type.NON_DEFINI
        }
    }
    
    /**
     * Parse the Firebase key to extract information (optional utility function)
     * For keys in format: "(parentId)->(clientId_(clientName))->(state)"
     */
    private fun parseFirebaseKey(key: String): Triple<Long, Long, String>? {
        try {
            // Extract parent ID between parentheses
            val parentIdMatch = "\\((\\d+)\\)".toRegex().find(key)
            val parentId = parentIdMatch?.groupValues?.get(1)?.toLongOrNull() ?: return null
            
            // Extract client ID and name
            val clientMatch = "\\((\\d+)_\\((.+?)\\)\\)".toRegex().find(key)
            val clientId = clientMatch?.groupValues?.get(1)?.toLongOrNull() ?: return null
            val clientName = clientMatch.groupValues[2]
            
            return Triple(parentId, clientId, clientName)
        } catch (e: Exception) {
            println("Error parsing key $key: ${e.message}")
            return null
        }
    }
}
