package Z_MasterOfApps.E_GoogleAuth// ComptApp.kt    /*
// comptAppDao.kt
/*import android.os.Build
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity
data class ComptApp(
    @PrimaryKey(autoGenerate = true)
    var keyID: Long = 0L,
    var googleId: String? = null,
    var nom: String = "Manager Vendor",
    var email: String? = null,
    var photoUrl: String? = null,
    var deviceModel: String = Build.MODEL,
    var deviceId: String = Build.ID,
    var isGoogleAuth: Boolean = false,
    var allowOffline: Boolean = true,
    var localPassword: String? = null,
    var lastSync: Long = 0L,
    var productionMode: Boolean = false,
    var hideScreen: Boolean = false,
    var migrateAtStart: Boolean = false,
    var connectDevDb: Boolean = false,
    var periodId: Long = 0L,
    var startPeriod: Long = 0L
)

class AuthManager(
    private val dao: ComptAppDao,
    private val googleAuth: GoogleAuthHelper? = null
) {

    suspend fun authenticate(): AuthResult = withContext(Dispatchers.IO) {
        when {
            isGoogleAvailable() && hasInternet() -> authenticateGoogle()
            isAlreadyAuthenticated() -> AuthResult.Success(getCachedAccount())
            else -> authenticateLocal()
        }
    }

    // Function called by background sync service or app lifecycle events
    suspend fun performBackgroundSync() = withContext(Dispatchers.IO) {
        val compte = dao.getCompte() ?: return@withContext
        if (compte.isGoogleAuth && hasInternet()) {
            googleAuth?.sync(compte)
            dao.update_showDetailsExpanded(compte.copy(lastSync = System.currentTimeMillis()))
        }
    }

    // Function called when user explicitly wants to upgrade to Google auth
    suspend fun upgradeAccountToGoogle(): Boolean = withContext(Dispatchers.IO) {
        if (!hasInternet()) return@withContext false

        val localCompte = dao.getCompte() ?: return@withContext false
        val googleResult = googleAuth?.signIn() ?: return@withContext false

        val updatedCompte = localCompte.copy(
            googleId = googleResult.keyID,
            email = googleResult.email,
            photoUrl = googleResult.photoUrl,
            isGoogleAuth = true,
            lastSync = System.currentTimeMillis()
        )

        dao.update_showDetailsExpanded(updatedCompte)
        googleAuth.migrateData(localCompte)
        true
    }

    // Function called when user wants to sign out
    suspend fun performSignOut() = withContext(Dispatchers.IO) {
        googleAuth?.signOut()
        dao.deleteAll()
    }

    // Public method to check if sync is needed and perform it
    suspend fun syncIfNeeded() {
        val compte = dao.getCompte() ?: return
        val lastSyncTime = compte.lastSync
        val currentTime = System.currentTimeMillis()
        val syncInterval = 24 * 60 * 60 * 1000 // 24 hours in milliseconds

        if (currentTime - lastSyncTime > syncInterval) {
            performBackgroundSync()
        }
    }

    private suspend fun authenticateGoogle(): AuthResult {
        val result = googleAuth?.signIn() ?: return AuthResult.Error("Google non disponible")

        val compte = ComptApp(
            googleId = result.keyID,
            nom = result.name,
            email = result.email,
            photoUrl = result.photoUrl,
            isGoogleAuth = true,
            lastSync = System.currentTimeMillis()
        )

        dao.insert(compte)
        return AuthResult.Success(compte)
    }

    private suspend fun authenticateLocal(): AuthResult {
        val compte = dao.getCompte() ?: run {
            val newCompte = ComptApp()
            dao.insert(newCompte)
            newCompte
        }
        return AuthResult.Success(compte)
    }

    private suspend fun isAlreadyAuthenticated() = dao.getCompte() != null

    private fun isGoogleAvailable() = googleAuth != null

    private fun hasInternet(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
            process.waitFor() == 0
        } catch (e: Exception) { false }
    }

    private suspend fun getCachedAccount() = dao.getCompte()!!
}

// AuthResult.kt
sealed class AuthResult {
    data class Success(val compte: ComptApp) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

// GoogleAuthHelper.kt
data class GoogleUserInfo(
    val keyID: String,
    val name: String,
    val email: String,
    val photoUrl: String?
)

interface GoogleAuthHelper {
    suspend fun signIn(): GoogleUserInfo?
    suspend fun signOut()
    suspend fun sync(compte: ComptApp)
    suspend fun migrateData(compte: ComptApp)
}

@Dao
interface ComptAppDao {
    @Query("SELECT * FROM ComptApp LIMIT 1")
    suspend fun getCompte(): ComptApp?

    @Insert
    suspend fun insert(compte: ComptApp): Long

    @Update
    suspend fun update_showDetailsExpanded(compte: ComptApp)

    @Query("DELETE FROM ComptApp")
    suspend fun deleteAll()
}

package com.example.clientjetpack

import AuthManager
import AuthResult
import P0_MainScreen.Main.MainScreen
import P6_AiGroupeForSupplier.GenerativeAiViewModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Apps.Manager.Module.C.Permission.PermissionHandler
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clientjetpack.ui.theme.ClientJetPackTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

private const val TAG = "MainActivity"

data class AppViewModels(
    val generativeAiViewModel: GenerativeAiViewModel,
)

class ViewModelFactory(
    private val context: Context,
    private val database: AppDatabase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(GenerativeAiViewModel::class.java) ->
            GenerativeAiViewModel() as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.DatabaseModule.getDatabase(this) }
    private val permissionHandler by lazy { PermissionHandler(this) }
    private val viewModelFactory by lazy { ViewModelFactory(applicationContext, database) }
    private val dao by lazy { database.comptAppDao() }

    private val generativeAiViewModel: GenerativeAiViewModel by viewModels { viewModelFactory }
    private val appViewModels by lazy {
        AppViewModels(generativeAiViewModel)
    }

    private var permissionsChecked by mutableStateOf(false)
    private var applicationAfficheProduitsPourCompt by mutableStateOf(false)
    private var authenticationCompleted by mutableStateOf(false)

    private lateinit var authManager: AuthManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: API level ${Build.VERSION.SDK_INT}")
        setupActivityContent()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(KoinExperimentalAPI::class)
    private fun setupActivityContent() {
        runCatching {
            setContent {
                ClientJetPackTheme {
                    KoinAndroidContext {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when {
                                !permissionsChecked -> {
                                    // Show loading or permission request screen
                                }
                                !authenticationCompleted -> {
                                    // Show authentication loading
                                    setupAuth()
                                }
                                else -> {
                                    // Show main screen when both permissions and auth are ready
                                    MainScreen()
                                }
                            }
                        }
                    }
                }
            }
            handlePermissions()
        }.onFailure { exception ->
            Log.e(TAG, "Error setting up activity content", exception)
        }
    }

    private fun setupAuth() {
        if (!::authManager.isInitialized) {
            authManager = AuthManager(dao)

            lifecycleScope.launch {
                try {
                    when (val result = authManager.authenticate()) {
                        is AuthResult.Success -> {
                            val compte = result.compte
                            applicationAfficheProduitsPourCompt = true
                            authenticationCompleted = true

                            // Perform background sync if needed
                            authManager.syncIfNeeded()

                            Log.d(TAG, "Auth success: ${compte.nom}")
                        }
                        is AuthResult.Error -> {
                            Log.e(TAG, "Auth error: ${result.message}")
                            // Still allow app to continue with limited functionality
                            authenticationCompleted = true
                        }
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "Authentication failed", exception)
                    authenticationCompleted = true
                }
            }
        }
    }

    private fun handlePermissions() {
        Log.d(TAG, "Checking permissions...")

        if (permissionHandler.arePermissionsGranted()) {
            Log.d(TAG, "All permissions are already granted")
            permissionsChecked = true
            return
        }

        permissionHandler.checkAndRequestPermissions(object : PermissionHandler.PermissionCallback {
            override fun onPermissionsGranted() {
                Log.d(TAG, "All permissions granted through request")
                permissionsChecked = true
            }

            override fun onPermissionsDenied() {
                Log.d(TAG, "Some permissions denied")
                showPermissionDeniedMessage()
                // Still setting permissionsChecked to true to allow the app to run with limited functionality
                permissionsChecked = true
            }

            override fun onPermissionRationale(permissions: Array<String>) {
                Log.d(TAG, "Permission rationale needed for: ${permissions.joinToString()}")
                // This is handled in PermissionHandler
            }
        })
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            "Certaines fonctionnalités seront limitées sans les permissions nécessaires",
            Toast.LENGTH_LONG
        ).show()
    }

    // Public methods to be called from UI components
    fun upgradeToGoogleAuth() {
        if (::authManager.isInitialized) {
            lifecycleScope.launch {
                val success = authManager.upgradeAccountToGoogle()
                if (success) {
                    Log.d(TAG, "Successfully upgraded to Google authentication")
                } else {
                    Log.e(TAG, "Failed to upgrade to Google authentication")
                }
            }
        }
    }

    fun signOut() {
        if (::authManager.isInitialized) {
            lifecycleScope.launch {
                authManager.performSignOut()
                authenticationCompleted = false
                applicationAfficheProduitsPourCompt = false
                Log.d(TAG, "User signed out")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Trigger sync when app resumes
        if (::authManager.isInitialized && authenticationCompleted) {
            lifecycleScope.launch {
                authManager.syncIfNeeded()
            }
        }
    }
}
*/
