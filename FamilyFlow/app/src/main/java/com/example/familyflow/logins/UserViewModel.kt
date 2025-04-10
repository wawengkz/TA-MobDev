package com.example.familyflow.logins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.familyflow.api.RetrofitClient
import com.example.familyflow.api.SessionManager
import com.example.familyflow.data.database.AppDatabase
import com.example.familyflow.util.Event
import com.example.familyflow.util.NetworkUtils
import kotlinx.coroutines.launch
import java.security.MessageDigest

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    // Operation state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Status messages
    private val _statusMessage = MutableLiveData<Event<String>>()
    val statusMessage: LiveData<Event<String>> = _statusMessage

    // Authentication status
    private val _isAuthenticated = MutableLiveData<Event<Boolean>>(Event(false))
    val isAuthenticated: LiveData<Event<Boolean>> = _isAuthenticated

    // Current user
    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> = _currentUser

    // Network connection status
    private val _isOnline = MutableLiveData<Boolean>()
    val isOnline: LiveData<Boolean> = _isOnline

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()

        // Initialize session manager
        SessionManager.init(application)

        // Initialize repository with API service
        repository = UserRepository(userDao, RetrofitClient.userApiService)

        // Check network connectivity
        _isOnline.value = NetworkUtils.isNetworkAvailable(application)
    }

    fun registerUser(username: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            // Update network status
            _isOnline.value = NetworkUtils.isNetworkAvailable(getApplication())

            val result = repository.registerUser(username, email, password)
            result.fold(
                onSuccess = { userId ->
                    _statusMessage.value = Event("Registration successful!")
                    _isAuthenticated.value = Event(true)
                    // Fetch the user to set as current user
                    _currentUser.value = UserEntity(
                        id = userId,
                        username = username,
                        email = email,
                        passwordHash = hashPassword(password)
                    )
                },
                onFailure = { exception ->
                    _statusMessage.value = Event("Registration failed: ${exception.message}")
                    _isAuthenticated.value = Event(false)
                }
            )
        } catch (e: Exception) {
            _statusMessage.value = Event("Registration error: ${e.message}")
            _isAuthenticated.value = Event(false)
        } finally {
            _isLoading.value = false
        }
    }

    fun loginUser(usernameOrEmail: String, password: String) = viewModelScope.launch {
        _isLoading.value = true
        try {
            // Update network status
            _isOnline.value = NetworkUtils.isNetworkAvailable(getApplication())

            val result = repository.loginUser(usernameOrEmail, password)
            result.fold(
                onSuccess = { user ->
                    _statusMessage.value = Event("Login successful!")
                    _isAuthenticated.value = Event(true)
                    _currentUser.value = user
                },
                onFailure = { exception ->
                    _statusMessage.value = Event("Login failed: ${exception.message}")
                    _isAuthenticated.value = Event(false)
                }
            )
        } catch (e: Exception) {
            _statusMessage.value = Event("Login error: ${e.message}")
            _isAuthenticated.value = Event(false)
        } finally {
            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = Event(false)
        _statusMessage.value = Event("Logged out successfully")

        // Clear session
        SessionManager.clearSession()
    }

    // Password hashing utility (move this to a separate utility class in a real app)
    private fun hashPassword(password: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}