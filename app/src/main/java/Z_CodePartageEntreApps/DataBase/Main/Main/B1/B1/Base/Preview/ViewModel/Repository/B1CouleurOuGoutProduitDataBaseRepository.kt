package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseFactory_B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ACentralCompoRepositoryProtoJuin9.Companion.getPushFireBase
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@Stable
class B1CouleurOuGoutProduitDataBaseRepository(
    val mainInitDataBase: DataBaseFactory_B1CouleurOuGoutProduitDataBase,
) {
    val repoTAG = "B1CouleurOuGoutProduitDataBase"
    val dao = mainInitDataBase.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<B1CouleurOuGoutProduitDataBase>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }

    init {
        composScope.launch {
            _datas.value = dao.getAll()
            dao.getAllFlow().collect { newData -> _datas.value = newData }
        }
    }

    fun addOrUpdateData(data: B1CouleurOuGoutProduitDataBase) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            B1CouleurOuGoutProduitDataBase.compareEntre(ancien = ancien, newData = data)
        }

        val updatedData = if (existingIndex >= 0) {
            data.copy(
                key = datasValue[existingIndex].key, // Keep existing key
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        } else {
            data.copy(
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }

        addOrUpdatedAncienRepo(existingIndex, updatedData)
    }

    fun deleteData(data: B1CouleurOuGoutProduitDataBase) {
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            mainInitDataBase.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: B1CouleurOuGoutProduitDataBase
    ) {
        composScope.launch {
            mainInitDataBase.deleteDataAncienRepo(data)
        }
    }
}

@Entity
data class B1CouleurOuGoutProduitDataBase(
    @PrimaryKey
    var key: String = getPushFireBase(ref),
    var pushKey: String = getPushFireBase(ref),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val aAffiche: Type = Type.Image,
    val nomImageFichie: String = "Non Dispo",
    val nomCouleurStrSiSonImageDispo: String = "",

    var parentBProduitOldID: Long? = null,
    var parentBProduitNom: String = "",
) {
    enum class Type { Image, Nom }

    companion object {
        val ref =
            Firebase.database.getReference(
                "00_DataPrototype-04-02" +
                        "/_1_developingRef" +
                        "/C_InfosSqlDataBases" +
                        "/B1CouleurOuGoutProduitDataBase"
            )

        fun compareEntre(
            ancien: B1CouleurOuGoutProduitDataBase,
            newData: B1CouleurOuGoutProduitDataBase
        ): Boolean {
            // Compare by parent product ID and color/image info for better matching
            return ancien.parentBProduitOldID == newData.parentBProduitOldID &&
                    ancien.nomCouleurStrSiSonImageDispo == newData.nomCouleurStrSiSonImageDispo &&
                    ancien.nomImageFichie == newData.nomImageFichie
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CouleurDisplayer(
    modifier: Modifier = Modifier,
    data: B1CouleurOuGoutProduitDataBase,
    onClickToOpenWindow: (B1CouleurOuGoutProduitDataBase) -> Unit = {}
) {
    val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

    val imageFile by derivedStateOf {
        if (data.nomImageFichie != "Non Dispo") {
            File(basePath, data.nomImageFichie)
        } else null
    }

    Card(
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display image if available
            when (data.aAffiche) {
                B1CouleurOuGoutProduitDataBase.Type.Image -> {
                    ImageDisplayer(
                        modifier = Modifier.size(120.dp),
                        imageFile = imageFile,
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        contentScale = ContentScale.Crop,
                        imageSize = DpSize(120.dp, 120.dp),
                        onClickToOpenWindow = { onClickToOpenWindow(data) }
                    )
                }
                B1CouleurOuGoutProduitDataBase.Type.Nom -> {
                    ColorNameDisplayer(
                        modifier = Modifier.size(120.dp),
                        colorName = data.nomCouleurStrSiSonImageDispo,
                        onClickToOpenWindow = { onClickToOpenWindow(data) }
                    )
                }
            }

            // Product information
            Text("ID: ${data.key}")
            Text("Product: ${data.parentBProduitNom}")
            Text("Color: ${data.nomCouleurStrSiSonImageDispo}")
            Text("Type: ${data.aAffiche}")
            Text("Image: ${data.nomImageFichie}")
            data.parentBProduitOldID?.let { Text("Parent ID: $it") }
        }
    }
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayer(
    modifier: Modifier = Modifier,
    imageFile: File? = null,
    colorName: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    onClickToOpenWindow: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val imageExists = imageFile?.exists() == true

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClickToOpenWindow() }
                .size(imageSize.width, imageSize.height)
        ) {
            if (imageExists && imageFile != null) {
                GlideImage(
                    model = imageFile,
                    contentDescription = "Color image for $colorName",
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect =
                                    BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        }
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey(imageFile.absolutePath))
                        listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ) = false

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (isFirstResource) isLoading = false
                                return false
                            }
                        })
                    }
                }
            } else {
                ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    colorName = colorName,
                    onClickToOpenWindow = onClickToOpenWindow
                )
            }
        }
    }
}

@Composable
fun ColorNameDisplayer(
    modifier: Modifier = Modifier,
    colorName: String,
    onClickToOpenWindow: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray)
            .clickable { onClickToOpenWindow() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colorName.ifEmpty { "No Color" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
