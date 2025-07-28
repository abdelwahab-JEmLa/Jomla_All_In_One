package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ImageDisplayer
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import org.koin.compose.koinInject
import java.io.File
import kotlin.coroutines.resume

class DataBaseInitFactory_B1CouleurOuGoutProduitDataBase(
    val dao: B1CouleurOuGoutProduitDataBaseDao,
) {
    val repoTAG = "M3CouleurProduitInfos"
    val repoRef = M3CouleurProduitInfos.ref
    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.4f)
        val data: List<M3CouleurProduitInfos> = if (isInternetAvailable) {
            updateRepoProgress(
                WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name,
                0.6f
            )
            onLoadFromFireBase()
        } else {
            emptyList()
        }
        updateRepoProgress(WDatabaseInitializationManager.Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
    }

    fun initCreationDepuitOld(
        produit: ArticlesBasesStatsTable,
    ): List<M3CouleurProduitInfos> {

        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"
        val results = mutableListOf<M3CouleurProduitInfos>()

        val colorMappings = listOf(
            produit.couleur1 to 0,
            produit.couleur2 to 1,
            produit.couleur3 to 2,
            produit.couleur4 to 3
        )

        colorMappings.forEach { (couleur, colorIndex) ->
            val imageIndex = colorIndex + 1
            val fileName = "${produit.id}_$imageIndex"

            val imageFile = listOf("jpg", "webp", "jpeg", "png")
                .map { File("$basePath/$fileName.$it") }
                .firstOrNull { it.exists() && it.canRead() && it.length() > 0 }
                ?: File("$basePath/NonTrouve.webp")

            val imageExists = imageFile.name != "NonTrouve.webp" &&
                    imageFile.exists() && imageFile.canRead() && imageFile.length() > 0

            if (imageExists || !couleur.isNullOrBlank()) {
                val colorData = M3CouleurProduitInfos(
                    aAffiche = if (imageExists) M3CouleurProduitInfos.Type.Image else M3CouleurProduitInfos.Type.Nom,
                    nomImageFichieSansEtansion = if (imageExists) imageFile.nameWithoutExtension else "Non Dispo",
                    extensionDisponible = if (imageExists) imageFile.extension else "webp",
                    nomCouleurStrSiSonImageDispo = couleur ?: "",
                    parentBProduitInfosKeyID = produit.keyID,
                    parentBProduitOldID = produit.id,
                    parentId1ProduitInfosDebugName = produit.nom,
                    indexCouleurDansAncienProto = colorIndex
                )

                results.add(colorData)
            }
        }

        return results
    }

    suspend fun onLoadFromFireBase(): MutableList<M3CouleurProduitInfos> {
        return suspendCancellableCoroutine { continuation ->
            M3CouleurProduitInfos.ref.get()
                .addOnSuccessListener { snapshot ->
                    val dataList = mutableListOf<M3CouleurProduitInfos>()
                    snapshot.children.forEach { child ->
                        child.getValue(M3CouleurProduitInfos::class.java)?.let { item ->
                            dataList.add(item)
                        }
                    }
                    continuation.resume(dataList)
                }
                .addOnFailureListener { exception ->
                    println("Firebase load error: ${exception.message}")
                    continuation.resume(mutableListOf()) // Return empty list instead of throwing
                }
        }
    }

    var isListenerRegistered = false
    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        var updateCount = 0
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M3CouleurProduitInfos::class.java)
                                    ?.let { entity ->
                                        val entityWithKey = entity.copy(keyID = child.key ?: "")
                                        val shouldUpdate = try {
                                            val localEntity =
                                                dao.getAll().find { it.keyID == entityWithKey.keyID }
                                            if (localEntity == null) {
                                                true
                                            } else {
                                                entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase
                                            }
                                        } catch (e: Exception) {
                                            true
                                        }

                                        if (shouldUpdate) {
                                            dao.update(entityWithKey)
                                            updateCount++
                                        }
                                    }
                            } catch (e: Exception) {
                                println("Error processing child: ${e.message}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Error in data change listener: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Firebase listener cancelled: ${error.message}")
                isListenerRegistered = false
            }
        })
    }

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: M3CouleurProduitInfos
    ) {
        composScope.launch {
            try {
                if (existingIndex >= 0) {
                    dao.update(dataAvecTigerUpdate)
                    batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
                } else {
                    dao.insert(dataAvecTigerUpdate)
                    batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(listOf(dataAvecTigerUpdate))
                }
            } catch (e: Exception) {
                println("Error in addOrUpdatedAncienRepo: ${e.message}")
            }
        }
    }

    fun deleteDataAncienRepo(data: M3CouleurProduitInfos) {
        composScope.launch {
            try {
                dao.delete(data)
                deleteFromFireBase(data)
            } catch (e: Exception) {
                println("Error in deleteDataAncienRepo: ${e.message}")
            }
        }
    }

    private suspend fun deleteFromFireBase(data: M3CouleurProduitInfos) {
        try {
            repoRef.child(data.keyID).removeValue().await()
        } catch (e: Exception) {
            println("Error deleting from Firebase: ${e.message}")
        }
    }

    private suspend fun batchFireBaseUpdateB1CouleurOuGoutProduitDataBase(datas: List<M3CouleurProduitInfos>) {
        try {
            val updates = mutableMapOf<String, Any>()
            datas.forEach { data ->
                updates[data.keyID] = data
            }
            val firebaseRef = M3CouleurProduitInfos.ref
            firebaseRef.updateChildren(updates).await()
        } catch (e: Exception) {
            println("Error in batch Firebase upsert: ${e.message}")
        }
    }

    fun deleteAll() {
        composScope.launch {
            try {
                // Delete from local database
                dao.deleteAll()

                // Delete from Firebase
                repoRef.removeValue().await()
            } catch (e: Exception) {
                // Handle error - could log or throw based on requirements
                println("Error deleting all data: ${e.message}")
            }
        }
    }
}
@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    b1CouleurOuGoutProduitDataBaseRepository: Repo03CouleurProduitInfos = koinInject(),
    keyCouleur: String,
    onClickToOpenWindow: (M3CouleurProduitInfos) -> Unit = {},
    size: Dp = 200.dp
) {
    val datas = b1CouleurOuGoutProduitDataBaseRepository.datasValue
    val rela_CouleurProduit = datas.find { it.keyID == keyCouleur }!!

    val imageFile by derivedStateOf {
        if (rela_CouleurProduit.nomImageFichieSansEtansion != "Non Dispo") {
            val fileName = "${rela_CouleurProduit.nomImageFichieSansEtansion}.${rela_CouleurProduit.extensionDisponible}"
            File("/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne", fileName)
        } else null
    }

    Card(
        modifier = modifier
            .getSemanticsTag(rela_CouleurProduit,"")
        .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            when (rela_CouleurProduit.aAffiche) {
                M3CouleurProduitInfos.Type.Image -> {
                    // Fixed: Removed the comment and properly called the composable
                    ImageDisplayer(
                        modifier = Modifier.size(size),
                        imageFile = imageFile,
                        colorName = rela_CouleurProduit.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(size, size),
                        onClickToOpenWindow = { onClickToOpenWindow(rela_CouleurProduit) }
                    )
                }

                M3CouleurProduitInfos.Type.Nom -> {
                    ColorNameDisplayer(
                        modifier = Modifier.size(size),
                        colorName = rela_CouleurProduit.nomCouleurStrSiSonImageDispo,
                        onClickToOpenWindow = { onClickToOpenWindow(rela_CouleurProduit) }
                    )
                }
            }

            if (rela_CouleurProduit.keyID.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopEnd)
                ) {
                    AfficheKeyCouleurAvecVentDebug(rela_CouleurProduit)
                }
            }
        }
    }
}

@Composable
private fun AfficheKeyCouleurAvecVentDebug(data: M3CouleurProduitInfos) {
    val text = "${
        data.keyID.takeLast(4).uppercase()
    } ${data.nomImageFichieSansEtansion}.${data.extensionDisponible}"

    Text(
        text = text,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        // Fixed: Use 'modifier' parameter name instead of 'androidx.compose.ui.Modifier'
        modifier = Modifier
            .background(
                color = Color.Red,
                shape = RoundedCornerShape(bottomStart = 8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun AfficheKeyCouleurAvecVentDebugParAncienMethodePreviewRepo(
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    getter: RepositorysMainGetter = koinInject(),
) {
    val couleur = getter.relatedCouleurKeyParAncienMethod(article, colorIndex)
    val vent = getter.getVentForArticleAndColorInThisApp(article, colorIndex)

    couleur?.let {
        val text = with(couleur) {
            "${keyID.takeLast(4).uppercase()} $nomImageFichieSansEtansion.$extensionDisponible" +
                    " V= ${vent?.parent_M1Produit_DebugInfos ?: "NO"} ${vent?.quantity}"
        }

        Box(
            // Fixed: Use 'modifier' parameter name instead of 'androidx.compose.ui.Modifier'
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(
                        color = Color.Red,
                        shape = RoundedCornerShape(bottomStart = 8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
