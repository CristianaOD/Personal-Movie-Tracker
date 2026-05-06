package cst.unibucfmiif2026.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun clearError() {
        if (_authState.value.errorMessage != null) {
            _authState.value = _authState.value.copy(errorMessage = null)
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (!hasUsableFirebaseConfig()) {
            _authState.value = AuthState(errorMessage = FIREBASE_CONFIG_ERROR)
            return
        }

        _authState.value = AuthState(isLoading = true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState()
                onSuccess()
            }
            .addOnFailureListener { error ->
                _authState.value = AuthState(errorMessage = error.toReadableMessage())
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        if (!hasUsableFirebaseConfig()) {
            _authState.value = AuthState(errorMessage = FIREBASE_CONFIG_ERROR)
            return
        }

        _authState.value = AuthState(isLoading = true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState()
                onSuccess()
            }
            .addOnFailureListener { error ->
                _authState.value = AuthState(errorMessage = error.toReadableMessage())
            }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState()
    }

    private fun hasUsableFirebaseConfig(): Boolean {
        return runCatching {
            val apiKey = FirebaseApp.getInstance().options.apiKey.orEmpty()
            apiKey.isNotBlank() && !apiKey.startsWith("dummy")
        }.getOrDefault(false)
    }

    private fun Exception.toReadableMessage(): String {
        return when (this) {
            is FirebaseAuthInvalidUserException -> "No account was found for this email."
            is FirebaseAuthInvalidCredentialsException -> "Email or password is invalid."
            is FirebaseAuthUserCollisionException -> "An account with this email already exists."
            is FirebaseTooManyRequestsException -> "Too many attempts. Try again in a few minutes."
            else -> localizedMessage ?: "Authentication failed. Please try again."
        }
    }

    private companion object {
        const val FIREBASE_CONFIG_ERROR =
            "Firebase is not configured yet. Replace app/google-services.json with your Firebase project's file."
    }
}
