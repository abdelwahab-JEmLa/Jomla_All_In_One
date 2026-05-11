package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Modules

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("unused")
class Setter_LongOperations(
    val appDatabase: AppDatabase,
) {
    // ──────────────────────────── M03 ────────────────────────────────────────
    suspend fun delete_All_M03() {
        appDatabase.dao_M03CouleurProduitInfos().deleteAll()
    }

    suspend fun insertAll_M03(items: List<M3CouleurProduitInfos>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    suspend fun get_Firebase_M03_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            // M03 has no credit concept → second value is always 0
            Pair(total, 0)
        }

    suspend fun export_M03_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M03CouleurProduitInfos().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] =
                item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M03_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext

        items.forEach { item ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(item.keyID).setValue(item.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun import_M03_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] =
                item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M03Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    suspend fun import_M03_FireBase_To_Room(
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    /** Deserialise a flat String map (from CSV or Firebase) into [M3CouleurProduitInfos]. */
    private fun m03_from_Map(map: Map<String, String?>): M3CouleurProduitInfos =
        M3CouleurProduitInfos(
            keyID = map["keyID"] ?: M3CouleurProduitInfos.generePushKey(),
            debugInfos = map["debugInfos"] ?: "",
            creationTimestamp = map["creationTimestamp"]?.toLongOrNull()
                ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull()
                ?: System.currentTimeMillis(),
            its_in_echantiallants = map["its_in_echantiallants"]?.equals(
                "true", ignoreCase = true
            ) ?: false,
            its_pour_affiche_au_presenter = map["its_pour_affiche_au_presenter"]?.equals(
                "true", ignoreCase = true
            ) ?: false,
            parentProduit_Classement = map["parentProduit_Classement"]?.toIntOrNull(),
            processPositioningInFactory = map["processPositioningInFactory"]?.let {
                runCatching {
                    M3CouleurProduitInfos.ProcessPositioningInFactory.valueOf(it)
                }.getOrDefault(M3CouleurProduitInfos.ProcessPositioningInFactory.CreeAuGeneralHandler)
            } ?: M3CouleurProduitInfos.ProcessPositioningInFactory.CreeAuGeneralHandler,
            aAffiche = map["aAffiche"]?.let {
                runCatching { M3CouleurProduitInfos.Type.valueOf(it) }
                    .getOrDefault(M3CouleurProduitInfos.Type.Image)
            } ?: M3CouleurProduitInfos.Type.Image,
            dropBox_key = map["dropBox_key"] ?: "Non Dispo",
            nomImageFichieSansEtansion = map["nomImageFichieSansEtansion"] ?: "Non Dispo",
            telephone_Prise_depuit = map["telephone_Prise_depuit"] ?: "",
            count_Don_Depot = map["count_Don_Depot"]?.toIntOrNull() ?: 0,
            a_cammende_depuit_grossist = map["a_cammende_depuit_grossist"]?.toIntOrNull() ?: 0,
            nomCouleurStrSiSonImageDispo = map["nomCouleurStrSiSonImageDispo"] ?: "",
            parentBProduitInfosKeyID = map["parentBProduitInfosKeyID"] ?: "",
            parentBProduitOldID = map["parentBProduitOldID"]?.toLongOrNull() ?: 0L,
            parentId1ProduitInfosDebugName = map["parentId1ProduitInfosDebugName"] ?: "",
            indexCouleurDansAncienProto = map["indexCouleurDansAncienProto"]?.toIntOrNull() ?: 0,
            extensionDisponible = map["extensionDisponible"] ?: "webp",
        )

    // ──────────────────────────── M8 ─────────────────────────────────────────

    suspend fun add_New_M8BonVent(bon: M8BonVent) {
        appDatabase.dao_M8BonVent().insert(bon)
    }

    suspend fun update_M8(bon: M8BonVent) = withContext(Dispatchers.IO) {
        appDatabase.dao_M8BonVent().upsert(bon)
    }

    suspend fun insertAll(bons: List<M8BonVent>) = withContext(Dispatchers.IO) {
        bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun bach_update_FireBase_M8(
        bons: List<M8BonVent>,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        bons.forEachIndexed { _, bon ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(bon.keyID).setValue(bon.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun get_Firebase_M8_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val creditNames = M8BonVent.EtateActuellementEst.entries
                .filter { it.credit_type }.map { it.name }.toSet()

            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            val creditCount = snapshot.children.count { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@count false
                @Suppress("UNCHECKED_CAST")
                val etat = (raw as Map<String, Any?>)["etateActuellementEst"]?.toString()
                etat != null && etat in creditNames
            }
            Pair(total, creditCount)
        }

    suspend fun get_Firebase_M8_Count(refDataBase: DatabaseReference): Int =
        withContext(Dispatchers.IO) {
            suspendFirebaseSnapshot(refDataBase).childrenCount.toInt()
        }

    suspend fun export_M8_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M8BonVent().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { bon ->
            existingRows[bon.keyID] = bon.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_m8_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext
        bach_update_FireBase_M8(bons, refDataBase)
    }

    suspend fun import_M8_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = bons.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        bons.forEach { bon ->
            existingRows[bon.keyID] = bon.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M8Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (bons.isNotEmpty()) bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun import_M8_FireBase_To_Room(
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext
        bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun delete_All_M8() {
        appDatabase.dao_M8BonVent().deleteAll()
    }

    suspend fun suspendFirebaseSnapshot(ref: DatabaseReference): DataSnapshot =
        suspendCancellableCoroutine { cont ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    if (cont.isActive) cont.resume(snap)
                }
                override fun onCancelled(error: DatabaseError) {
                    if (cont.isActive) cont.resumeWithException(error.toException())
                }
            }
            ref.addListenerForSingleValueEvent(listener)
            cont.invokeOnCancellation { ref.removeEventListener(listener) }
        }
}


fun String.escapeCsv(): String {
    val sanitized = replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
    return if (sanitized.contains(',') || sanitized.contains('"')) {
        "\"${sanitized.replace("\"", "\"\"")}\""
    } else sanitized
}

fun String.splitCsvLine(): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < length) {
        val c = this[i]
        when {
            c == '"' && inQuotes && i + 1 < length && this[i + 1] == '"' -> {
                current.append('"'); i += 2; continue
            }
            c == '"' -> inQuotes = !inQuotes
            c == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}
