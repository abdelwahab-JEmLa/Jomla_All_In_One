// ComptApp.kt
// AuthManager.kt
// comptAppDao.kt
import android.os.Build
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
    var id: Long = 0L,
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

    suspend fun syncWhenPossible() = withContext(Dispatchers.IO) {  //->
        //TODO(FIXME):Fix erreur Function "syncWhenPossible" is never used 
        val compte = dao.getCompte() ?: return@withContext
        if (compte.isGoogleAuth && hasInternet()) {
            googleAuth?.sync(compte)
            dao.update(compte.copy(lastSync = System.currentTimeMillis()))
        }
    }

    suspend fun upgradeToGoogle(): Boolean = withContext(Dispatchers.IO) { //->
        //TODO(FIXME):Fix erreur Function "upgradeToGoogle" is never used 
        if (!hasInternet()) return@withContext false

        val localCompte = dao.getCompte() ?: return@withContext false
        val googleResult = googleAuth?.signIn() ?: return@withContext false

        val updatedCompte = localCompte.copy(
            googleId = googleResult.id,
            email = googleResult.email,
            photoUrl = googleResult.photoUrl,
            isGoogleAuth = true,
            lastSync = System.currentTimeMillis()
        )

        dao.update(updatedCompte)
        googleAuth.migrateData(localCompte)
        true
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {     //->
        //TODO(FIXME):Fix erreur Function "signOut" is never used 
        googleAuth?.signOut()
        dao.deleteAll()
    }

    private suspend fun authenticateGoogle(): AuthResult {
        val result = googleAuth?.signIn() ?: return AuthResult.Error("Google non disponible")

        val compte = ComptApp(
            googleId = result.id,
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
    val id: String,
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
    suspend fun update(compte: ComptApp)

    @Query("DELETE FROM ComptApp")
    suspend fun deleteAll()
}
